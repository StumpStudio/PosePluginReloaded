package ru.armagidon.poseplugin.bukkit.seat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.subsystems.Seat;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayClientSteerVehicle;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerMount;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.comphenix.protocol.PacketType.Play.Client.STEER_VEHICLE;
import static ru.armagidon.poseplugin.api.utility.Optex.optex;

public final class SeatDaemon extends PacketAdapter {

    private static SeatDaemon DAEMON_INSTANCE;

    private final Map<Player, DaemonizedSeat> seats = new ConcurrentHashMap<>();

    public static void start(Plugin plugin) {
        synchronized (SeatDaemon.class) {
            if (DAEMON_INSTANCE != null)
                throw new IllegalStateException("Daemon already started");
            ProtocolLibrary.getProtocolManager().addPacketListener(DAEMON_INSTANCE = new SeatDaemon(plugin));
        }
    }

    public static SeatDaemon getInstance() {
        return DAEMON_INSTANCE;
    }

    private SeatDaemon(Plugin plugin) {
        super(plugin, STEER_VEHICLE, PacketType.Play.Server.MOUNT);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final var packet = event.getPacket();
        if (packet.getType().equals(STEER_VEHICLE)) {
            final var wrapper = new WrapperPlayClientSteerVehicle(event.getPacket());
            final var player = event.getPlayer();
            if (seats.containsKey(player)) {
                final var seat = seats.get(player);
                optex(seat)
                    .map(DaemonizedSeat::seat)
                    .ifPresent(s -> rotateSeat(s.getId(), s.getSeated().getLocation().getYaw()))
                    .rollback().map(DaemonizedSeat::dismountCallback).ifPresent(callback -> {
                        if (wrapper.isUnmount()) {
                            final var cancelled = callback
                                    .apply(PlayerMap.<Player>getInstance().getPlayer(player.getUniqueId()));
                            if (cancelled)
                                event.setCancelled(true);
                            else
                                disconnect(event.getPlayer());
                        }
                    });
            }
        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        final var wrapper = new WrapperPlayServerMount(event.getPacket());
        final var entity = wrapper.getEntity(event.getPlayer().getWorld());
        if (entity == null) return;

        seats.values().stream()
                .filter(seat -> seat.seat().getId() == entity.getEntityId())
                .map(s -> s.seat().getSeated()).findFirst()
                .ifPresent(owner -> {
                    wrapper.setPassengers(List.of(owner));
                    event.setPacket(wrapper.getHandle());
                });

    }

    public boolean connect(Seat<Player> seat, Function<Poser<Player>, Boolean> dismountCallback) {
        if (!seats.containsKey(seat.getSeated())) {
            if (!seat.isUsed()) return false;
            seats.put(seat.getSeated(), new DaemonizedSeat(seat, dismountCallback));
            return true;
        }
        return false;
    }

    public void disconnect(Seat<Player> seat) {
        if (!seats.containsKey(seat.getSeated())) return;
        seats.remove(seat.getSeated());
    }

    private void rotateSeat(int id, float yaw) {
        PacketContainer rotPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_LOOK);
        rotPacket.getIntegers().writeSafely(0, id);
        rotPacket.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F)).writeSafely(1, (byte) 0);
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(rotPacket);
    }

    private void disconnect(Player player) {
        if (!seats.containsKey(player)) return;
        seats.remove(player);
    }

    private record DaemonizedSeat(Seat<Player> seat, Function<Poser<Player>, Boolean> dismountCallback) {}
}
