package ru.armagidon.poseplugin.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.armagidon.poseplugin.api.utility.Seat;
import ru.armagidon.poseplugin.bukkit.wrappers.WrapperPlayClientSteerVehicle;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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
        seat.remove();
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

    public static final class SeatDaemon extends PacketAdapter {

        private static SeatDaemon DAEMON_INSTANCE;
        private final Map<Player, Function<Player, Boolean>> seats = new ConcurrentHashMap<>();

        private SeatDaemon(Plugin plugin) {
            super(plugin, PacketType.Play.Client.STEER_VEHICLE);
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            final var wrapper = new WrapperPlayClientSteerVehicle(event.getPacket());
            if (wrapper.isUnmount()) {
                if (seats.containsKey(event.getPlayer())) {
                    Optional.ofNullable(seats.get(event.getPlayer())).ifPresent(callback -> {
                        final var cancelled = callback.apply(event.getPlayer());
                        if (cancelled) {
                            event.setCancelled(true);
                        } else {
                            seats.remove(event.getPlayer());
                        }
                    });
                }
            }
        }

        private boolean connect(BukkitSeat seat, Function<Player, Boolean> dismountCallback) {
            if (!seats.containsKey(seat.getSeated())) {
                if (!seat.isUsed()) return false;
                seats.put(seat.getSeated(), dismountCallback);
                return true;
            }
            return false;
        }

        public static void start(Plugin plugin) {
            synchronized (BukkitSeat.SeatDaemon.class) {
                if (DAEMON_INSTANCE != null)
                    throw new IllegalStateException("Daemon already started");
                ProtocolLibrary.getProtocolManager().addPacketListener(DAEMON_INSTANCE = new SeatDaemon(plugin));
            }
        }

        public static SeatDaemon getDaemonInstance() {
            return DAEMON_INSTANCE;
        }
    }
}
