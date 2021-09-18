package ru.armagidon.poseplugin.bukkit;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.event.EventPublisher;
import ru.armagidon.poseplugin.api.event.PoseChangeEvent;
import ru.armagidon.poseplugin.api.player.PosePluginPlayer;
import ru.armagidon.poseplugin.api.pose.IPluginPose;

//TODO implement bukkit PosePluginPlayer
public class BukkitPosePluginPlayer implements PosePluginPlayer<Player>
{

    private final Player handle;
    @SuppressWarnings("unchecked")
    private IPluginPose<Player> POSE = (IPluginPose<Player>) IPluginPose.STANDING;

    public BukkitPosePluginPlayer(Player handle) {
        Validate.notNull(handle);
        this.handle = handle;
    }

    @Override
    public @NotNull Player getHandle() {
        return handle;
    }

    @Override
    public @NotNull IPluginPose<Player> getCurrentPose() {
        return POSE;
    }

    @Override
    public Class<Player> getHandleClass() {
        return Player.class;
    }


    //TODO add PoseStopEvent
    @Override
    @SuppressWarnings("unchecked")
    public boolean stopCurrentPose() {
        POSE.stop(handle);
        POSE = (IPluginPose<Player>) IPluginPose.STANDING;
        return true;
    }

    @Override
    public boolean changePose(@NotNull IPluginPose<Player> newPose) {
        PoseChangeEvent<Player> event = EventPublisher.call(new PoseChangeEvent<>(POSE, newPose, this));
        if (event.isCancelled()) {
            return false;
        }
        POSE.stop(handle);
        POSE = newPose;
        POSE.start(handle);
        return true;
    }
}
