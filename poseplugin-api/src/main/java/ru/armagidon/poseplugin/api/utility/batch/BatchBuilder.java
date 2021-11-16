package ru.armagidon.poseplugin.api.utility.batch;

import com.google.common.collect.ImmutableList;

import java.util.function.Consumer;

public abstract class BatchBuilder<T> {

    protected final ImmutableList.Builder<Consumer<T>> instructions = ImmutableList.builder();

    public final Batch<T> create() {
        return new Batch<>(instructions.build());
    }

    public ImmutableList.Builder<Consumer<T>> instructions() {
        return instructions;
    }
}
