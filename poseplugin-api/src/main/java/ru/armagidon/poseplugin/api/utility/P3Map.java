package ru.armagidon.poseplugin.api.utility;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.armagidon.poseplugin.api.PosingPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

@ApiStatus.Internal
public final class P3Map<PlayerHandle> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private final Map<UUID, PosingPlayer<PlayerHandle>> PLAYER_MAP = new HashMap<>();

    private final Function<UUID, PosingPlayer<PlayerHandle>> FACTORY;

    public P3Map(Function<UUID, PosingPlayer<PlayerHandle>> playerFactory) {
        this.FACTORY = playerFactory;
    }

    /**
     * Retrieves instance of ${@link PosingPlayer} from ${@link #PLAYER_MAP}
     * @param uuid UUID of a player.
     * @param playerClazz Class of a player's handle.
     * @return Instance of ${@link PosingPlayer} with given UUID or null when such player with given UUID does not exist.
     * @throws ClassCastException when given playerClazz and player's actual handle class does not match.
     * */
    @Nullable
    public PosingPlayer<PlayerHandle> getPlayer(@NotNull UUID uuid, @NotNull Class<PlayerHandle> playerClazz) throws IllegalArgumentException, ClassCastException {
        readLock.lock();
        try {
            PosingPlayer<PlayerHandle> player = PLAYER_MAP.get(uuid);
            if (player == null)
                return null;
            //if (!player.getHandleClass().equals(playerClazz))
            //    throw new ClassCastException("Handle's class of given player's uuid does not match with give player class");
            return player;
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
            PLAYER_MAP.put(uuid, FACTORY.apply(uuid));
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
}
