package ru.armagidon.poseplugin.api.pose;

import ru.armagidon.poseplugin.api.pose.batch.Batch;
import ru.armagidon.poseplugin.api.utility.property.PropertyMap;

public class AbstractPose<P> implements Pose<P>
{

    private final PropertyMap propertyMap = new PropertyMap();
    private final Batch<P> runtimeBatch;
    private P user;

    public AbstractPose(Batch<P> runtimeBatch) {
        this.runtimeBatch = runtimeBatch;
    }

    @Override
    public synchronized void start(P player) {
        this.user = player;
        runtimeBatch.runInit(player);
    }

    @Override
    public synchronized void stop() {
        runtimeBatch.runDestruct();
        this.user = null;
    }

    @Override
    public PropertyMap getProperties() {
        return propertyMap;
    }

    @Override
    public P getCurrentUser() {
        return user;
    }
}
