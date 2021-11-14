package ru.armagidon.poseplugin.api.subsystems.doppelganger;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class NPCTracker<P>
{

    private static NPCTracker INSTANCE;

    private final Map<Doppelganger<P, ?, ?>, Set<P>> trackers = new ConcurrentHashMap<>();

    @Getter private volatile int viewDistance = 20;

    public void registerNPC(Doppelganger<P, ?, ?> doppelganger) {
        if (trackers.containsKey(doppelganger)) return;
        trackers.put(doppelganger, ConcurrentHashMap.newKeySet());
    }

    public void unregisterNPC(Doppelganger<P, ?, ?> doppelganger) {
        if (!trackers.containsKey(doppelganger)) return;
        trackers.remove(doppelganger);
    }

    public final Set<P> getTrackersFor(Doppelganger<P, ?, ?> doppelganger) {
        return Optional.ofNullable(trackers.get(doppelganger))
                .map(t -> t.stream().filter(Objects::nonNull)
                        .collect(Collectors.toUnmodifiableSet()))
                .orElse(ImmutableSet.of());
    }

    protected final Map<Doppelganger<P, ?, ?>, Set<P>> getTrackers() {
        return trackers;
    }

    public synchronized void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    public boolean isTrackerOf(Doppelganger<P, ?, ?> doppelganger, P player) {
        return Optional.ofNullable(trackers.get(doppelganger))
                .map(trackers -> trackers.contains(player)).orElse(false);
    }

    public boolean isTracked(Doppelganger<P, ?, ?> doppelganger) {
        return trackers.containsKey(doppelganger);
    }

    public static void init(NPCTracker<?> npcTracker) {
        synchronized (NPCTracker.class) {
            if (INSTANCE == null)
                INSTANCE = npcTracker;
        }
    }

    public static <P> NPCTracker<P> getInstance() {
        return INSTANCE;
    }
}
