package ru.armagidon.poseplugin.bukkit;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.event.EventPublisher;
import ru.armagidon.poseplugin.api.event.PoseChangeEvent;
import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.pose.Pose;

public class BukkitPoser implements Poser<Player>
{

    private final Player handle;
    @SuppressWarnings("unchecked")
    private Pose<Player> POSE = (Pose<Player>) Pose.STANDING;

    public BukkitPoser(Player handle) {
        Validate.notNull(handle);
        this.handle = handle;
    }

    @Override
    public @NotNull Player getHandle() {
        return handle;
    }

    @Override
    public @NotNull Pose<Player> getCurrentPose() {
        return POSE;
    }


    //TODO add PoseStopEvent
    @Override
    @SuppressWarnings("unchecked")
    public boolean stopPosing() {
        POSE.stop(handle);
        POSE = (Pose<Player>) Pose.STANDING;
        return true;
    }

    @Override
    public boolean changePose(@NotNull Pose<Player> newPose) {
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
