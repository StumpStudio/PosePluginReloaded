package ru.armagidon.poseplugin.api.utility.batch;

import com.google.common.collect.ImmutableList;

import java.util.function.Consumer;

public class BatchBuilder<T> {

    protected final ImmutableList.Builder<Consumer<T>> instructions;

    public BatchBuilder(ImmutableList.Builder<Consumer<T>> source) {
        this.instructions = source;
    }

    public BatchBuilder() {
        this(ImmutableList.builder());
    }

    public final Batch<T> create() {
        return new Batch<>(instructions.build());
    }

    public BatchBuilder<T> add(Consumer<T> command) {
        instructions.add(command);
        return this;
    }
}
