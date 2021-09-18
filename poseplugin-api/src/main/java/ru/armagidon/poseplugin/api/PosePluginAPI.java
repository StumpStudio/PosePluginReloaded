package ru.armagidon.poseplugin.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.armagidon.poseplugin.api.player.P3Map;
import ru.armagidon.poseplugin.api.ru.armagidon.poseplugin.utils.NameTagHider;
import ru.armagidon.poseplugin.api.ru.armagidon.poseplugin.utils.PlayerHider;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;


//Yeeeaah, SINGLETONS!
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public NameTagHider<P> getNameTagHider() {
        return nameTagHider;
    }

    public P3Map<P> getPlayerMap() {
        return playerMap;
    }

    public PlayerHider<P> getPlayerHider() {
        return playerHider;
    }

    /**
     * Creates an instance of PosePluginAPI
     * */

    public static <P> void create(NameTagHider<P> nameTagHider, PlayerHider<P> playerHider, P3Map<P> playerMap) {
        writeLock.lock();
        try {
            if (INSTANCE == null) {
                INSTANCE = new PosePluginAPI<>(playerMap, playerHider, nameTagHider);
            } else
                throw new IllegalStateException("API is already initialized");
        } finally {
            writeLock.unlock();
        }
    }
}
