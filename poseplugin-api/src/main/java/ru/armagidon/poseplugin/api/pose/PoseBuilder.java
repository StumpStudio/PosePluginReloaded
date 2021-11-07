package ru.armagidon.poseplugin.api.pose;

import ru.armagidon.poseplugin.api.pose.batch.Batch;

//TODO implement builder. P.s monad way
public abstract class PoseBuilder<Player>
{
    public abstract PoseBuilder<Player> play(Batch batch);

    public abstract Pose<Player> build();
}
