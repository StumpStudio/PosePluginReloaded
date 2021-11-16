package ru.armagidon.poseplugin.api.pose;

import ru.armagidon.poseplugin.api.utility.batch.Batch;
import ru.armagidon.poseplugin.api.utility.property.Property;

import java.util.Arrays;
import java.util.Objects;

public class PoseBuilder<P>
{

    private Property<?>[] properties = new Property[0];
    private Batch<P> setupBatch;
    private Batch<P> endBatch;

    public synchronized final PoseBuilder<P> properties(Property<?>... properties) {
        this.properties = properties;
        return this;
    }

    public synchronized final PoseBuilder<P> setup(Batch<P> batch) {
        this.setupBatch = batch;
        return this;
    }

    public synchronized final PoseBuilder<P> end(Batch<P> batch) {
        this.endBatch = batch;
        return this;
    }

    public final Pose<P> build() {
        Objects.requireNonNull(setupBatch, "Setup batch was not set up");
        Objects.requireNonNull(endBatch, "End batch was not set up");
        AbstractPose<P> pose = new AbstractPose<>(setupBatch, endBatch);
        Arrays.stream(properties).forEach(p -> pose.getProperties().registerProperty(p.getId(), p));
        pose.getProperties().register();
        return pose;
    }

    public static <P>PoseBuilder<P> create() {
        return new PoseBuilder<>();
    }
}
