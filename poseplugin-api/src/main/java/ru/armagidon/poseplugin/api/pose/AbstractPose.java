package ru.armagidon.poseplugin.api.pose;

import ru.armagidon.poseplugin.api.utility.batch.Batch;
import ru.armagidon.poseplugin.api.utility.property.PropertyMap;

public class AbstractPose<P> implements Pose<P>
{

    private final PropertyMap propertyMap = new PropertyMap();
    private final Batch<P> setupBatch;
    private final Batch<P> endBatch;

    public AbstractPose(Batch<P> setupBatch, Batch<P> endBatch) {
        if (setupBatch.commandsCount() != endBatch.commandsCount())
            throw new IllegalArgumentException("Command counts of setup and end batches are not equal.");
        this.setupBatch = setupBatch;
        this.endBatch = endBatch;
    }

    @Override
    public synchronized void start(P player) {
        setupBatch.run(player);
    }

    @Override
    public synchronized void stop(P player) {
        endBatch.run(player);
    }

    @Override
    public PropertyMap getProperties() {
        return propertyMap;
    }
}
