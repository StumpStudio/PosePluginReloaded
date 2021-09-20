package ru.armagidon.poseplugin.api.event;

import ru.armagidon.poseplugin.api.player.PosePluginPlayer;
import ru.armagidon.poseplugin.api.pose.Pose;

public class PoseChangeEvent<P> extends Event
{
    private boolean cancelled = false;
    private final Pose<P> old;
    private Pose<P> current;
    private final PosePluginPlayer<P> player;

    public PoseChangeEvent(Pose<P> old, Pose<P> current, PosePluginPlayer<P> player) {
        this.old = old;
        this.current = current;
        this.player = player;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public Pose<P> getOld() {
        return this.old;
    }

    public Pose<P> getCurrent() {
        return this.current;
    }

    public PosePluginPlayer<P> getPlayer() {
        return this.player;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setCurrent(Pose<P> current) {
        this.current = current;
    }
}
