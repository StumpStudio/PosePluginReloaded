package ru.armagidon.poseplugin.api.utility.datastructures;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class StorageWithVaryingMutability
{

    private final Map<String, Variable<?>> variables = new ConcurrentHashMap<>();

    public void putImmutableVariable(String name, @NotNull Object value) {
        if (variables.containsKey(name))
            throw new UnsupportedOperationException("Overwriting existing variable");
        variables.put(name, new Variable<>(Mutability.IMMUTABLE, value));
    }

    public void putMutableVariable(String name, @NotNull Object value) {
        if (variables.containsKey(name))
            throw new UnsupportedOperationException("Overwriting existing variable");
        variables.put(name, new Variable<>(Mutability.MUTABLE, value));
    }

    @SuppressWarnings("unchecked")
    public boolean set(String name, Object value) {
        if (variables.containsKey(name))
                return false;
        final var mutability = variables.get(name).mutability;
        if (mutability.equals(Mutability.IMMUTABLE))
            throw new UnsupportedOperationException("Modifying immutable variable");
        ((Variable<Object>) variables.get(name)).setValue(value);
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        if (!variables.containsKey(name)) return null;
        else return (T) variables.get(name).getValue();
    }


    private static final class Variable<T> {

        private final Mutability mutability;
        private volatile T value;

        private Variable(@NotNull Mutability mutability, @NotNull T value) {
            this.mutability = mutability;
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            if (mutability.equals(Mutability.MUTABLE))
                this.value = value;
            else
                throw new UnsupportedOperationException("Variable is immutable");
        }

        public Mutability getAccessLevel() {
            return mutability;
        }
    }

    private enum Mutability {
        MUTABLE, IMMUTABLE
    }
}
