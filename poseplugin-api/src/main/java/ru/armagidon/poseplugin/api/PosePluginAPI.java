package ru.armagidon.poseplugin.api;

import ru.armagidon.poseplugin.api.player.P3Map;
import ru.armagidon.poseplugin.api.utility.ArmorStandSeat;
import ru.armagidon.poseplugin.api.utility.NameTagHider;
import ru.armagidon.poseplugin.api.utility.PlayerHider;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;


//Yeeeaah, SINGLETONS!
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

    public static <P> Builder<P> builder() {
        return new Builder<>();
    }

    public P3Map<P> getPlayerMap() {
        return this.playerMap;
    }

    public PlayerHider<P> getPlayerHider() {
        return this.playerHider;
    }

    public NameTagHider<P> getNameTagHider() {
        return this.nameTagHider;
    }

    public ArmorStandSeat<P> getArmorStandSeat() {
        return this.armorStandSeat;
    }

    public static class Builder<P> {
        private P3Map<P> playerMap;
        private PlayerHider<P> playerHider;
        private NameTagHider<P> nameTagHider;
        private ArmorStandSeat<P> armorStandSeat;

        Builder() {
        }

        public Builder<P> playerMap(P3Map<P> playerMap) {
            this.playerMap = playerMap;
            return this;
        }

        public Builder<P> playerHider(PlayerHider<P> playerHider) {
            this.playerHider = playerHider;
            return this;
        }

        public Builder<P> nameTagHider(NameTagHider<P> nameTagHider) {
            this.nameTagHider = nameTagHider;
            return this;
        }

        public Builder<P> armorStandSeat(ArmorStandSeat<P> armorStandSeat) {
            this.armorStandSeat = armorStandSeat;
            return this;
        }

        public PosePluginAPI<P> build() {
            return new PosePluginAPI<>(playerMap, playerHider, nameTagHider, armorStandSeat);
        }

        public String toString() {
            return "PosePluginAPI.Builder(playerMap=" + this.playerMap + ", playerHider=" + this.playerHider + ", nameTagHider=" + this.nameTagHider + ", armorStandSeat=" + this.armorStandSeat + ")";
        }
    }
}
