package ru.armagidon.poseplugin.bukkit;

import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.PosePluginAPI;
import ru.armagidon.poseplugin.api.utility.Pool;
import ru.armagidon.poseplugin.api.utility.Seat;

public class BukkitPosePluginAPI extends PosePluginAPI<Player>
{

    private final Pool<Seat<Player>> seats;

    public BukkitPosePluginAPI() {
        seats = new Pool<>(BukkitSeat::new);
    }

    @Override
    public Pool<Seat<Player>> getSeats() {
        return seats;
    }
}
