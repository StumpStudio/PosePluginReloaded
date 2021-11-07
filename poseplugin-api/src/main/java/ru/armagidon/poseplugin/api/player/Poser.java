package ru.armagidon.poseplugin.api.player;

import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.pose.Pose;

import java.util.concurrent.CompletableFuture;

public interface Poser<PlayerHandle>
{
        /**
         * Original Player
         * */

        @NotNull PlayerHandle getHandle();

        /**
         * Changes pose to given {@link Pose} object
         * @param pose new pose that will be set
         * @return whether the pose was changed
         * */

        boolean changePose(@NotNull Pose<PlayerHandle> pose);

        /**
         *  Asynchronous version of {@link #changePose(Pose)}
         *  @param pose new pose that will be set
         *  @return {@link CompletableFuture} indicating whether the pose was changed
         * */

        default CompletableFuture<Boolean> changePoseAsync(@NotNull Pose<PlayerHandle> pose) {
            return CompletableFuture.supplyAsync(() -> changePose(pose));
        }

        /**
         * @return Instance of current {@link Pose} object that is set
         * */

        @NotNull
        Pose<PlayerHandle> getCurrentPose();

        /**
         * Stops player's pose. Throwing the {@link PoseStopEvent}
         * @return Whether the pose was stopped
         * */

        boolean stopPosing();

        /**
         * Asynchronous version of {@link #stopPosing()}
         * @return {@link CompletableFuture} indicating whether the pose was stopped
         * */

        default CompletableFuture<Boolean> stopPosingAsync() {
            return CompletableFuture.supplyAsync(this::stopPosing);
        }
}

