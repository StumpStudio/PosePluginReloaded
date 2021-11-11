package ru.armagidon.poseplugin.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.utility.Seat;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayClientSteerVehicle;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerEntityTeleport;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayServerMount;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.comphenix.protocol.PacketType.Play.Client.STEER_VEHICLE;
import static ru.armagidon.poseplugin.bukkit.utilities.SyncUtils.sync;


public class BukkitSeat extends Seat<Player>
{

    private static final String SCOREBOARD_TAG = "PosePlugin";
    private ArmorStand seat;

    @Override
    public synchronized void handlePlayerSeat(Player seated, int yOffset) {
        seat = createSeat(seated.getLocation().clone().add(0, yOffset, 0));
        seat.addPassenger(seated);
    }

    @Override
    public synchronized void handlePlayerRaise(Player seated) {
        sync(() -> {
            seated.teleport(seat.getLocation().clone().add(0, 0.2, 0).setDirection(seated.getLocation().getDirection()));
            seat.remove();
            seat = null;
        });
    }

    private static ArmorStand createSeat(Location location) {
        return location.getWorld().spawn(location.subtract(0, 0.2D, 0), ArmorStand.class, (armorStand -> {
            armorStand.setGravity(false);
            armorStand.setMarker(true);
            armorStand.setSmall(true);
            armorStand.setVisible(false);
            armorStand.addScoreboardTag(SCOREBOARD_TAG);
            armorStand.setCollidable(false);
        }));
    }

    @Override
    public int getId() {
        return seat != null ? seat.getEntityId() : -1;
    }

    //TODO add seat rotation
    public static final class SeatDaemon extends PacketAdapter implements Listener {

        private static SeatDaemon DAEMON_INSTANCE;

        private final Map<Player, DaemonizedSeat> seats = new ConcurrentHashMap<>();


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
                    Optional.ofNullable(seats.get(player)).ifPresent(daemonizedSeat -> {
                        float yaw = daemonizedSeat.seat().getSeated().getLocation().getYaw();

                        PacketContainer rotPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_LOOK);
                        rotPacket.getIntegers().writeSafely(0, daemonizedSeat.seat().getId());
                        rotPacket.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F)).writeSafely(1, (byte) 0);

                        ProtocolLibrary.getProtocolManager().broadcastServerPacket(rotPacket);

                        if (wrapper.isUnmount()) {
                            final var cancelled = daemonizedSeat.dismountCallback()
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

            Optional<Player> ownerOptional = seats.values().stream()
                    .filter(seat -> seat.seat().getId() == entity.getEntityId())
                    .map(s -> s.seat().getSeated()).findFirst();

            ownerOptional.ifPresent(owner -> {
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

        private void disconnect(Player player) {
            if (!seats.containsKey(player)) return;
            seats.remove(player);
        }

        public void disconnect(Seat<Player> seat) {
            if (!seats.containsKey(seat.getSeated())) return;
            seats.remove(seat.getSeated());
        }

        public static void start(Plugin plugin) {
            synchronized (BukkitSeat.SeatDaemon.class) {
                if (DAEMON_INSTANCE != null)
                    throw new IllegalStateException("Daemon already started");
                ProtocolLibrary.getProtocolManager().addPacketListener(DAEMON_INSTANCE = new SeatDaemon(plugin));
            }
        }

        public static SeatDaemon getInstance() {
            return DAEMON_INSTANCE;
        }
    }

    private record DaemonizedSeat(Seat<Player> seat, Function<Poser<Player>, Boolean> dismountCallback) {}
}
