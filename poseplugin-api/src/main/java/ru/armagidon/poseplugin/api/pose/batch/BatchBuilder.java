package ru.armagidon.poseplugin.api.pose.batch;

import com.google.common.collect.ImmutableList;
import ru.armagidon.poseplugin.api.player.Poser;
import ru.armagidon.poseplugin.api.utility.Pair;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BatchBuilder<P> {


    protected final ImmutableList.Builder<Pair<Consumer<P>, Runnable>> instructions = ImmutableList.builder();

    public abstract BatchBuilder<P> useSeat(Function<Poser<P>, Boolean> dismountCallback);

    public final Batch<P> create() {
        return new Batch<>(instructions.build());
    }

    public ImmutableList.Builder<Pair<Consumer<P>, Runnable>> instructions() {
        return instructions;
    }
}
