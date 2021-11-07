package ru.armagidon.poseplugin.api.pose.batch;

import com.google.common.collect.ImmutableList;

public abstract class BatchBuilder<P> {

    private final ImmutableList.Builder<Runnable> instructions = ImmutableList.builder();

    public abstract BatchBuilder<P> seatPlayer();

    public final Batch create() {
        return new Batch(instructions.build());
    }

    protected ImmutableList.Builder<Runnable> instructions() {
        return instructions;
    }
}
