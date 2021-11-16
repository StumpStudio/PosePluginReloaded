package ru.armagidon.poseplugin.bukkit;

import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.pose.PoseBatchBuilder;
import ru.armagidon.poseplugin.api.subsystems.PlayerHider;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Direction;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.NPCTracker;
import ru.armagidon.poseplugin.bukkit.seat.SeatDaemon;

import java.util.function.Function;

import static ru.armagidon.poseplugin.bukkit.doppelganger.BukkitDoppelganger.DOPPELGANGER_POOL;
import static ru.armagidon.poseplugin.bukkit.seat.BukkitSeat.getSeatPool;

public class BukkitPoseBatchBuilder extends PoseBatchBuilder<Player>
{

    @Override
    public PoseBatchBuilder<Player> useSeat(Function<Poser<Player>, Boolean> dismountCallback) {
        instructions().add(player -> getSeatPool().create(player, seat -> {
            seat.seatPlayer(player);
            SeatDaemon.getInstance().connect(seat, dismountCallback);
        }));
        return this;
    }

    @Override
    public PoseBatchBuilder<Player> usePlayerHider() {
        instructions.add(player -> PlayerHider.<Player>getInstance().hidePlayer(player));
        return this;
    }

    @Override
    public PoseBatchBuilder<Player> useDoppelganger() {
        instructions.add(player -> DOPPELGANGER_POOL.create(player, doppelganger -> {
            NPCTracker.<Player>getInstance().registerNPC(doppelganger);
            //doppelganger.lay(Direction.yawToDirection(player.getLocation().getYaw()));
        }));
        return this;
    }

    @Override
    public PoseBatchBuilder<Player> removeSeat() {
        instructions().add(player -> getSeatPool().flush(player, seat -> {
            SeatDaemon.getInstance().disconnect(seat);
            seat.raisePlayer();
        }));
        return this;
    }

    @Override
    public PoseBatchBuilder<Player> turnOffPlayerHiding() {
        instructions().add(player -> PlayerHider.<Player>getInstance().showPlayer(player));
        return this;
    }

    @Override
    public PoseBatchBuilder<Player> removeDoppelganger() {
        instructions.add(player -> DOPPELGANGER_POOL.flush(player, doppelganger ->
                NPCTracker.<Player>getInstance().unregisterNPC(doppelganger)));
        return this;
    }
}
