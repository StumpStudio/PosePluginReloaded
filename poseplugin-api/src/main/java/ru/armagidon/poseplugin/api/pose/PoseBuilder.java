package ru.armagidon.poseplugin.api.pose;

import ru.armagidon.poseplugin.api.pose.batch.Batch;
import ru.armagidon.poseplugin.api.utility.property.Property;

import java.util.Arrays;

public class PoseBuilder<P>
{

    private Property<?>[] properties = new Property[0];
    private Batch<P> runtimeBatch;

    public synchronized final PoseBuilder<P> properties(Property<?>... properties) {
        this.properties = properties;
        return this;
    }

    public synchronized final PoseBuilder<P> setup(Batch<P> batch) {
        this.runtimeBatch = batch;
        return this;
    }

    public final Pose<P> build() {
        AbstractPose<P> pose = new AbstractPose<>(runtimeBatch);
        Arrays.stream(properties).forEach(p -> pose.getProperties().registerProperty(p.getId(), p));
        pose.getProperties().register();
        return pose;
    }

    public static <P>PoseBuilder<P> create() {
        return new PoseBuilder<>();
    }
}
