package ru.armagidon.poseplugin.api.event;

import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.pose.Pose;

import java.util.Optional;

public class PoseChangeEvent<P> extends Event
{
    private boolean cancelled = false;
    private final Optional<Pose<P>> old;
    private Pose<P> current;
    private final Poser<P> player;

    public PoseChangeEvent(Optional<Pose<P>> old, Pose<P> current, Poser<P> player) {
        this.old = old;
        this.current = current;
        this.player = player;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public Optional<Pose<P>> getOld() {
        return old;
    }

    public Pose<P> getCurrent() {
        return this.current;
    }

    public Poser<P> getPlayer() {
        return this.player;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setCurrent(Pose<P> current) {
        this.current = current;
    }
}
