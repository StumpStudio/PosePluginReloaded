package ru.armagidon.poseplugin.api.player;

import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.pose.Pose;
import ru.armagidon.poseplugin.api.pose.PoseType;

import java.util.concurrent.CompletableFuture;

public interface PosePluginPlayer<PlayerHandle>
{

    /**
     * Original Player
     * */

    @NotNull PlayerHandle getHandle();

    boolean changePose(@NotNull Pose<PlayerHandle> poseType);

    default CompletableFuture<Boolean> changePoseAsync(@NotNull Pose<PlayerHandle> poseType) {
        return CompletableFuture.supplyAsync(() -> changePose(poseType));
    }

    default PoseType getCurrentPoseType() {
        return getCurrentPose().getPoseType();
    }

    @NotNull
    Pose<PlayerHandle> getCurrentPose();

    boolean stopPosing();

    default CompletableFuture<Boolean> stopPosingAsync() {
        return CompletableFuture.supplyAsync(this::stopPosing);
    }

    Class<PlayerHandle> getHandleClass();
}
