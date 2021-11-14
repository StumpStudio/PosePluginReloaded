package ru.armagidon.poseplugin.api.pose.batch;

import com.google.common.collect.ImmutableList;
import ru.armagidon.poseplugin.api.utility.Pair;

import java.util.List;
import java.util.function.Consumer;

public final class Batch<P>
{
    private final List<Pair<Consumer<P>, Consumer<P>>> instructions;

    Batch(List<Pair<Consumer<P>, Consumer<P>>> instructions) {
        this.instructions = ImmutableList.copyOf(instructions);
    }

    public void runInit(P player) {
        instructions.forEach(pair -> pair.first().accept(player));
    }

    public void runDestruct(P player) {
        instructions.forEach(pair -> pair.second().accept(player));
    }

    public List<Consumer<P>> initializers() {
        return instructions.stream().map(Pair::first).toList();
    }

    public List<Consumer<P>> destructors() {
        return instructions.stream().map(Pair::second).toList();
    }
}
