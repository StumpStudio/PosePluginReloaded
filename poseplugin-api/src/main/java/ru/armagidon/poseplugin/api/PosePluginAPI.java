package ru.armagidon.poseplugin.api;

import lombok.Builder;
import lombok.Getter;
import ru.armagidon.poseplugin.api.player.P3Map;
import ru.armagidon.poseplugin.api.utility.ArmorStandSeat;
import ru.armagidon.poseplugin.api.utility.NameTagHider;
import ru.armagidon.poseplugin.api.utility.PlayerHider;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;


//Yeeeaah, SINGLETONS!
@Getter
@Builder(builderClassName = "Builder")
public class PosePluginAPI<P>
{

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    @SuppressWarnings("rawtypes")
    private static PosePluginAPI INSTANCE;

    private final P3Map<P> playerMap;
    private final PlayerHider<P> playerHider;
    private final NameTagHider<P> nameTagHider;
    private final ArmorStandSeat<P> armorStandSeat;


    private PosePluginAPI(P3Map<P> playerMap, PlayerHider<P> playerHider, NameTagHider<P> nameTagHider, ArmorStandSeat<P> armorStandSeat) {
        this.playerMap = playerMap;
        this.playerHider = playerHider;
        this.nameTagHider = nameTagHider;
        this.armorStandSeat = armorStandSeat;

        writeLock.lock();
        try {
            if (INSTANCE == null) {
                INSTANCE = this;
            } else
                throw new IllegalStateException("API is already initialized");
        } finally {
            writeLock.unlock();
        }

    }

    /**
     * Gets an instance of API in null-safe optional box
     * */

    @SuppressWarnings("unchecked")
    public static <P> Optional<PosePluginAPI<P>> getAPI() {
        readLock.lock();
        try {
            return Optional.ofNullable((PosePluginAPI<P>) INSTANCE);
        } finally {
            readLock.unlock();
        }
    }
}
