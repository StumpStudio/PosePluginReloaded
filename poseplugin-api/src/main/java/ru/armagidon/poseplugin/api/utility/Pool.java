package ru.armagidon.poseplugin.api.utility;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Pool<V>
{
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final Map<Integer, V> storage = new ConcurrentHashMap<>();
    private final Supplier<V> factory;

    public Pool(Supplier<V> factory) {
        this.factory = factory;
    }

    public int create(Consumer<V> action) {
        Objects.requireNonNull(action);
        var id = 0;
        while (storage.containsKey(id)) id = random.nextInt();
        final var value = factory.get();
        action.accept(value);
        storage.put(id, value);
        return id;
    }

    public void access(int id, Consumer<V> action) {
        Objects.requireNonNull(action);
        if (!storage.containsKey(id)) {
            return;
        }
        action.accept(storage.get(id));
    }

    public void flush(int id) {
        if (!storage.containsKey(id)) return;
        storage.remove(id);
    }
}


