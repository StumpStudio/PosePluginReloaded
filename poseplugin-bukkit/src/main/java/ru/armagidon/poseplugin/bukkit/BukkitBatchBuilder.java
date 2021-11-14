package ru.armagidon.poseplugin.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.pose.batch.BatchBuilder;
import ru.armagidon.poseplugin.api.subsystems.PlayerHider;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Doppelganger;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.NPCTracker;
import ru.armagidon.poseplugin.bukkit.doppelganger.BukkitDoppelganger;
import ru.armagidon.poseplugin.bukkit.seat.SeatDaemon;

import java.util.function.Function;

import static ru.armagidon.poseplugin.api.utility.Pair.of;
import static ru.armagidon.poseplugin.bukkit.seat.BukkitSeat.getSeatPool;

public class BukkitBatchBuilder extends BatchBuilder<Player>
{

    private int armorStandId;
    private Doppelganger<Player, ItemStack> doppelganger;

    @Override
    public BatchBuilder<Player> useSeat(Function<Poser<Player>, Boolean> dismountCallback) {
        instructions().add(of(player ->
                armorStandId = getSeatPool().create(seat -> {
            seat.seatPlayer(player);
            SeatDaemon.getInstance().connect(seat, dismountCallback);
        }), player -> {
            getSeatPool().access(armorStandId, seat -> {
                SeatDaemon.getInstance().disconnect(seat);
                seat.raisePlayer();
            });
            getSeatPool().flush(armorStandId);
        }));
        return this;
    }

    @Override
    public BatchBuilder<Player> usePlayerHider() {
        instructions.add(of(player -> {
            PlayerHider.<Player>getInstance().hidePlayer(player);
        }, player -> {
            PlayerHider.<Player>getInstance().showPlayer(player);
        }));
        return this;
    }

    @Override
    public BatchBuilder<Player> useDoppelganger() {
        instructions.add(of(player -> {
            synchronized (this){
                doppelganger = new BukkitDoppelganger(player);
                NPCTracker.<Player>getInstance().registerNPC(doppelganger);
            }
        }, player -> {
            NPCTracker.<Player>getInstance().unregisterNPC(doppelganger);
        }));
        return this;
    }
}
