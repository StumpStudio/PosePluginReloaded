package ru.armagidon.poseplugin.api.player;

import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.pose.IPluginPose;
import ru.armagidon.poseplugin.api.pose.PoseType;

import java.util.concurrent.CompletableFuture;

public interface PosePluginPlayer<PlayerHandle>
{

    /**
     * Original Player
     * */

    @NotNull PlayerHandle getHandle();

    boolean changePose(@NotNull IPluginPose<PlayerHandle> poseType);

    default CompletableFuture<Boolean> changePoseAsync(@NotNull IPluginPose<PlayerHandle> poseType) {
        return CompletableFuture.supplyAsync(() -> changePose(poseType));
    }

    default PoseType getCurrentPoseType() {
        return getCurrentPose().getPoseType();
    }

    @NotNull IPluginPose<PlayerHandle> getCurrentPose();

    boolean stopCurrentPose();

    default CompletableFuture<Boolean> stopCurrentPoseAsync() {
        return CompletableFuture.supplyAsync(this::stopCurrentPose);
    }

    Class<PlayerHandle> getHandleClass();
}
