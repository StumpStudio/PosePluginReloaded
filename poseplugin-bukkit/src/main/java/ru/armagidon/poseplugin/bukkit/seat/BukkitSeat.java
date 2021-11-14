package ru.armagidon.poseplugin.bukkit.seat;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.utility.Pool;
import ru.armagidon.poseplugin.api.subsystems.Seat;

import static ru.armagidon.poseplugin.bukkit.utilities.SyncUtils.sync;


public class BukkitSeat extends Seat<Player>
{

    private static final String SCOREBOARD_TAG = "PosePlugin";

    private static final Pool<Player, Seat<Player>> seatPool = new Pool<>(p -> new BukkitSeat());

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

    @Override
    public int getId() {
        return seat != null ? seat.getEntityId() : -1;
    }

    public static Pool<Player, Seat<Player>> getSeatPool() {
        return seatPool;
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
}
