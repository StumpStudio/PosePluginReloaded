package ru.armagidon.poseplugin.bukkit;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.event.EventPublisher;
import ru.armagidon.poseplugin.api.event.PoseChangeEvent;
import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.pose.Pose;

import java.util.Optional;

public class BukkitPoser implements Poser<Player>
{

    private final Player handle;
    private Optional<Pose<Player>> POSE = Optional.empty();

    public BukkitPoser(Player handle) {
        Validate.notNull(handle);
        this.handle = handle;
    }

    @Override
    public @NotNull Player getHandle() {
        return handle;
    }

    @Override
    public @NotNull Optional<Pose<Player>> getCurrentPose() {
        return POSE;
    }


    //TODO add PoseStopEvent
    @Override
    public boolean stopPosing() {
        if (POSE.isEmpty())
            return false;
        else {
            POSE.get().stop(getHandle());
            POSE = Optional.empty();
        }
        return true;
    }

    @Override
    public boolean changePose(@NotNull Pose<Player> newPose) {
        try {
            PoseChangeEvent<Player> event = EventPublisher.call(new PoseChangeEvent<>(POSE, newPose, this));
            if (event.isCancelled())
                return false;
            stopPosing();
            POSE = Optional.of(newPose);
            POSE.ifPresent(pose -> pose.start(handle));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
