package ru.armagidon.poseplugin.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.pose.PoseBatchBuilder;
import ru.armagidon.poseplugin.api.subsystems.PlayerHider;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Doppelganger;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.DoppelgangerCommandExecutor;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.NPCTracker;
import ru.armagidon.poseplugin.api.utility.datastructures.Pool;
import ru.armagidon.poseplugin.bukkit.doppelganger.BukkitDoppelganger;
import ru.armagidon.poseplugin.bukkit.doppelganger.BukkitDoppelgangerCommandExecutor;
import ru.armagidon.poseplugin.bukkit.seat.SeatDaemon;

import java.util.function.Consumer;
import java.util.function.Function;

import static ru.armagidon.poseplugin.bukkit.seat.BukkitSeat.getSeatPool;

public class BukkitPoseBatchBuilder extends PoseBatchBuilder<Player>
{

    public static final Pool<Player, Doppelganger<Player, ItemStack>> DOPPELGANGER_POOL = new Pool<>(BukkitDoppelganger::new);

    @Override
    public PoseBatchBuilder<Player> useSeat(Function<Poser<Player>, Boolean> dismountCallback) {
        add(player -> getSeatPool().create(player, seat -> {
            seat.seatPlayer(player);
            SeatDaemon.getInstance().connect(seat, dismountCallback);
        }));
        return this;
    }

    @Override
    public PoseBatchBuilder<Player> usePlayerHider() {
        add(player -> PlayerHider.<Player>getInstance().hidePlayer(player));
        return this;
    }

    @Override
    public PoseBatchBuilder<Player> useDoppelganger(Consumer<DoppelgangerCommandExecutor> settings) {
        add(player -> DOPPELGANGER_POOL.create(player, doppelganger -> {
            DoppelgangerCommandExecutor executor = new BukkitDoppelgangerCommandExecutor(doppelganger);
            settings.accept(executor);
            doppelganger.setExecutor(executor);
            NPCTracker.<Player>getInstance().registerNPC(doppelganger);
        }));
        return this;
    }

    @Override
    public PoseBatchBuilder<Player> removeSeat() {
        add(player -> getSeatPool().flush(player, seat -> {
            SeatDaemon.getInstance().disconnect(seat);
            seat.raisePlayer();
        }));
        return this;
    }

    @Override
    public PoseBatchBuilder<Player> turnOffPlayerHiding() {
        add(player -> PlayerHider.<Player>getInstance().showPlayer(player));
        return this;
    }

    @Override
    public PoseBatchBuilder<Player> removeDoppelganger() {
        add(player -> DOPPELGANGER_POOL.flush(player, doppelganger ->
                NPCTracker.<Player>getInstance().unregisterNPC(doppelganger)));
        return this;
    }
}
