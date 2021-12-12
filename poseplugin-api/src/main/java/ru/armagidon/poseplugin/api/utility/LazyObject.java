package ru.armagidon.poseplugin.api.utility;

import java.util.function.Supplier;

public class LazyObject<T>
{
    private final Class<T> type;
    private T object;
    private final Supplier<T> supplier;

    private LazyObject(Supplier<T> supplier, Class<T> type) {
        this.supplier = supplier;
        this.type = type;
    }

    public synchronized T get() {
        if (object == null) {
            object = supplier.get();
            if (object == null)
                throw new IllegalArgumentException("Provided factory produces null objects");
        }
        return object;
    }

    public static <T> LazyObject<T> lazy(Supplier<T> factory, Class<T> type) {
        return new LazyObject<>(factory, type);
    }

    public Class<T> getType() {
        return type;
    }
}
