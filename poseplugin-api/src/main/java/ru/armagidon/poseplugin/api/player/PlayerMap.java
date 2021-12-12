package ru.armagidon.poseplugin.api.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.armagidon.poseplugin.api.subsystems.implefine.Implefine;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public abstract class PlayerMap<PlayerHandle>
{

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private final Map<UUID, Poser<PlayerHandle>> PLAYER_MAP = new ConcurrentHashMap<>();

    private final Function<UUID, Poser<PlayerHandle>> POSER_FACTORY;

    protected PlayerMap(Function<UUID, Poser<PlayerHandle>> poserFactory) {
        this.POSER_FACTORY = poserFactory;
    }

    @SuppressWarnings("unchecked")
    public static <P> PlayerMap<P> getInstance() {
        return Implefine.get(PlayerMap.class);
    }

    public static <P> Poser<P> getPlayer(UUID uuid, Class<P> clazz) {
        final var player = PlayerMap.<P>getInstance().getPlayer(uuid);
        if (player != null && clazz.isAssignableFrom(player.getHandle().getClass())) return player;
        return null;
    }

    /**
     * Retrieves instance of ${@link Poser} from ${@link #PLAYER_MAP}
     * @param uuid UUID of a player.
     * @return Instance of ${@link Poser} with given UUID or null when such player with given UUID does not exist.
     * */

    @Nullable
    public Poser<PlayerHandle> getPlayer(@NotNull UUID uuid) throws IllegalArgumentException {
        readLock.lock();
        try {
            return PLAYER_MAP.get(uuid);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Registers a player in ${@link #PLAYER_MAP}
     * */

    public boolean registerPlayer(@NotNull UUID uuid) {
        if (PLAYER_MAP.containsKey(uuid)) return false;
        writeLock.lock();
        try {
            PLAYER_MAP.put(uuid, POSER_FACTORY.apply(uuid));
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } finally {
            writeLock.unlock();
        }
    }


    /**
     * Unregisters a player in ${@link #PLAYER_MAP}
     * */
    public boolean unregisterPlayer(@NotNull UUID uuid) {
        if (!PLAYER_MAP.containsKey(uuid)) return false;
        writeLock.lock();
        try {
            PLAYER_MAP.remove(uuid);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    public boolean unregisterAll() {
        writeLock.lock();
        try {
            PLAYER_MAP.values().forEach(Poser::stopPosing);
            PLAYER_MAP.clear();
            return true;
        } finally {
            writeLock.unlock();
        }
    }
}
