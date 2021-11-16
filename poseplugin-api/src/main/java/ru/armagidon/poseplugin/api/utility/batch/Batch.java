package ru.armagidon.poseplugin.api.utility.batch;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Consumer;

//Batch API
public class Batch<T>
{
    private final List<Consumer<T>> commands;

    Batch(List<Consumer<T>> commands) {
        this.commands = ImmutableList.copyOf(commands);
    }

    public final void run(T input) {
        commands.forEach(cmd -> cmd.accept(input));
    }

    public final int commandsCount() {
        return commands.size();
    }
}
