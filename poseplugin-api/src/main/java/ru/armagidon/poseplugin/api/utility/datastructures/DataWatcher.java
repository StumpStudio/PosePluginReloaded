package ru.armagidon.poseplugin.api.utility.datastructures;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataWatcher<KeyType, ValueType>
{

    private final Map<KeyType, DataObject<KeyType, ValueType>> table = new ConcurrentHashMap<>();
    private boolean dirty = false;

    public synchronized void define(@NotNull KeyType key, @NotNull ValueType defaultValue) {
        if (table.containsKey(key))
            throw new IllegalArgumentException("This key is already defined in this table");
        DataObject<KeyType, ValueType> object = new DataObject<>(key, defaultValue);
        table.put(key, object);
    }

    public synchronized void set(@NotNull KeyType key, @NotNull ValueType value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        if (!table.containsKey(key))
            throw new IllegalArgumentException("This key is not defined in the table");

        DataObject<KeyType, ValueType> object = table.get(key);
        object.setValue(value);

        dirty = true;
    }

    @Nullable
    public synchronized ValueType get(@NotNull KeyType key) {
        Objects.requireNonNull(key);
        if (!table.containsKey(key))
            return null;
        return table.get(key).getValue();
    }

    public synchronized int dirtyCount() {
        return (int) table.values().stream().
                filter(DataObject::isDirty).count();
    }

    @NotNull
    public synchronized Collection<DataObject<KeyType, ValueType>> getAll() {
        return table.values();
    }

    @NotNull
    public synchronized Set<DataObject<KeyType, ValueType>> getModified() {
        if (!dirty) return Collections.emptySet();
        dirty = false;
        return getAll().stream()
                .filter(DataObject::isDirty)
                .peek(DataObject::clean)
                .collect(Collectors.toUnmodifiableSet());
    }


    private static class DataObject<K, V> {
        private V value;
        private final K key;
        private boolean dirty = false; //Changed?

        public DataObject(@NotNull K key, @NotNull V value) {
            this.key = key;
            this.value = value;
        }

        public void setValue(@NotNull V value) {
            if (value.equals(this.value)) return;
            this.value = value;
            this.dirty = true;
        }

        public void clean() {
            this.dirty = false;
        }

        @Override
        public String toString() {
            return "DataObject{" +
                    "key=" + key +
                    ", value=" + value +
                    ", dirty=" + dirty +
                    '}';
        }

        public V getValue() {
            return this.value;
        }

        public K getKey() {
            return this.key;
        }

        public boolean isDirty() {
            return this.dirty;
        }
    }
}
