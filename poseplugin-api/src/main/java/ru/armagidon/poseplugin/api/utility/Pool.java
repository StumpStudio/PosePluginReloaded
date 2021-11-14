package ru.armagidon.poseplugin.api.utility;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import static ru.armagidon.poseplugin.api.utility.Optex.optex;

public class Pool<K, V>
{
    protected final Map<K, V> storage = new ConcurrentHashMap<>();
    protected final Function<K, V> factory;

    public Pool(Function<K, V> factory) {
        this.factory = factory;
    }

    public K create(K key, Consumer<V> action) {
        return optex(factory.apply(key))
                .ifPresent(action)
                .ifPresent(v -> storage.put(key, v))
                .map(v -> key).get();
    }

    public void access(K key, Consumer<V> action) {
        Objects.requireNonNull(action);
        Optional.ofNullable(storage.get(key)).ifPresent(action);
    }

    public void flush(K key) {
        if (!storage.containsKey(key)) return;
        storage.remove(key);
    }

    public void flush(K key, Consumer<V> preRemovalCallback) {
        Optional.ofNullable(storage.get(key)).ifPresent(preRemovalCallback);
        flush(key);
    }

}


