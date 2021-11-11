package ru.armagidon.poseplugin.bukkit;

import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.pose.batch.BatchBuilder;
import ru.armagidon.poseplugin.api.utility.Pair;

import java.util.function.Function;

public class BukkitBatchBuilder extends BatchBuilder<Player>
{

    private int armorStandId;

    @Override
    public BatchBuilder<Player> useSeat(Function<Poser<Player>, Boolean> dismountCallback) {
        instructions().add(Pair.of(player ->
                armorStandId = BukkitPosePluginAPI.<Player>getAPI().getSeats().create(seat -> {
            seat.seatPlayer(player);
            BukkitSeat.SeatDaemon.getInstance().connect(seat, dismountCallback);
        }), () -> {
            BukkitPosePluginAPI.<Player>getAPI().getSeats().access(armorStandId, seat -> {
                BukkitSeat.SeatDaemon.getInstance().disconnect(seat);
                seat.raisePlayer();
            });
            BukkitPosePluginAPI.<Player>getAPI().getSeats().flush(armorStandId);
        }));
        return this;
    }
}
