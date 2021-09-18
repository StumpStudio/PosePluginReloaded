package ru.armagidon.poseplugin.api.player;

import ru.armagidon.poseplugin.api.pose.PoseType;

public interface PosePluginPlayer<PlayerHandle>
{
    PlayerHandle getHandle();

    void changePose(PoseType poseType);

    PoseType getCurrentPose();

    void stopCurrentPose();

    Class<PlayerHandle> getHandleClass();
}
