package ru.armagidon.poseplugin.bukkit;

import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.pose.batch.BatchBuilder;

public class BukkitBatchBuilder extends BatchBuilder<Player>
{

    private int armorStandId;

    @Override
    public BatchBuilder<Player> seatPlayer() {
        instructions().add(() -> {
            //armorStandId = PosePluginAPI.<Player>getAPI().getSeats().create(seat -> seat.seatPlayer())
        });
        return this;
    }
}
