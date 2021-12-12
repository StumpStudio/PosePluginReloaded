package ru.armagidon.poseplugin.api.subsystems.doppelganger;

import ru.armagidon.poseplugin.api.utility.datastructures.Pair;
import ru.armagidon.poseplugin.api.utility.datastructures.StorageWithVaryingMutability;
import ru.armagidon.poseplugin.api.utility.enums.Direction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class DoppelgangerCommandExecutor<P>
{

    public final Map<String, Pair<StorageWithVaryingMutability, Consumer<StorageWithVaryingMutability>>> startUpCommands = new LinkedHashMap<>();
    protected final Map<String, Pair<StorageWithVaryingMutability, Consumer<StorageWithVaryingMutability>>> endCommands = new LinkedHashMap<>();

    public abstract void lay(Direction direction);

    public void runStartup(P receiver) {
        synchronized (startUpCommands) {
            startUpCommands.values().forEach(c -> {
                c.first().putImmutableVariable("receiver", receiver);
                c.second().accept(c.first());
            });
        }
    }

    public void runStop(P receiver) {
        synchronized (endCommands) {
            endCommands.values().forEach(c -> {
                c.first().putImmutableVariable("receiver", receiver);
                c.second().accept(c.first());
            });
        }
    }
}
