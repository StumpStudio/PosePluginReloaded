package ru.armagidon.poseplugin.bukkit;

import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.pose.batch.BatchBuilder;
import ru.armagidon.poseplugin.api.subsystems.PlayerHider;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Direction;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.NPCTracker;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Pose;
import ru.armagidon.poseplugin.bukkit.seat.SeatDaemon;

import java.util.function.Function;

import static ru.armagidon.poseplugin.api.utility.Pair.of;
import static ru.armagidon.poseplugin.bukkit.doppelganger.BukkitDoppelganger.DOPPELGANGER_POOL;
import static ru.armagidon.poseplugin.bukkit.seat.BukkitSeat.getSeatPool;

public class BukkitBatchBuilder extends BatchBuilder<Player>
{

    @Override
    public BatchBuilder<Player> useSeat(Function<Poser<Player>, Boolean> dismountCallback) {
        instructions().add(of(player -> getSeatPool().create(player, seat -> {
            seat.seatPlayer(player);
            SeatDaemon.getInstance().connect(seat, dismountCallback);
        }), player -> {
            getSeatPool().access(player, seat -> {
                SeatDaemon.getInstance().disconnect(seat);
                seat.raisePlayer();
            });
            getSeatPool().flush(player);
        }));
        return this;
    }

    @Override
    public BatchBuilder<Player> usePlayerHider() {
        instructions.add(of(player -> PlayerHider.<Player>getInstance().hidePlayer(player),
                player -> PlayerHider.<Player>getInstance().showPlayer(player)));
        return this;
    }

    @Override
    public BatchBuilder<Player> useDoppelganger() {
        instructions.add(of(player -> DOPPELGANGER_POOL.create(player, doppelganger -> {
            NPCTracker.<Player>getInstance().registerNPC(doppelganger);
            doppelganger.lay(Direction.yawToDirection(player.getLocation().getYaw()));
        }),
                player -> DOPPELGANGER_POOL.flush(player, doppelganger ->
                        NPCTracker.<Player>getInstance().unregisterNPC(doppelganger))));
        return this;
    }
}
