package ru.armagidon.poseplugin.api.events;

import lombok.Getter;
import lombok.Setter;
import ru.armagidon.poseplugin.api.player.PosePluginPlayer;
import ru.armagidon.poseplugin.api.pose.IPluginPose;

@Getter
public class PoseChangeEvent<P> extends Event
{
    @Setter private boolean cancelled = false;
    private final IPluginPose<P> old;
    private final IPluginPose<P> current;
    private final PosePluginPlayer<P> player;

    public PoseChangeEvent(IPluginPose<P> old, IPluginPose<P> current, PosePluginPlayer<P> player) {
        this.old = old;
        this.current = current;
        this.player = player;
    }
}
