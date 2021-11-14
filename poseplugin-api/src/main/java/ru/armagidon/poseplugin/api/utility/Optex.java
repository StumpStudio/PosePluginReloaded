package ru.armagidon.poseplugin.api.utility;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

//Optional extension
public class Optex<V,C>
{
    private Optex<C, C> saved;
    protected final Optional<V> data;

    private Optex(V value) {
        this(Optional.ofNullable(value));
    }

    private Optex(Optional<V> value) {
        this(value, null);
    }

    private Optex(Optional<V> value, Optex<C, C> saved) {
        this.data = value;
        this.saved = saved;
    }

    public Optex<V, C> filter(Predicate<? super V> predicate) {
        return new Optex<>(data.filter(predicate), saved);
    }

    public Optex<V, C> ifPresent(Consumer<? super V> action) {
        data.ifPresent(action);
        return this;
    }

    public Optex<V, C> ifPresentOrElse(Consumer<? super V> action, Runnable emptyAction) {
        data.ifPresentOrElse(action, emptyAction);
        return this;
    }

    public <U> Optex<U, V> map(Function<? super V, ? extends U> mapper) {
        return new Optex<>(data.map(mapper), new Optex<>(data));
    }

    public Optex<C, C> rollbackMapping() {
        if (saved == null)
            throw new NullPointerException("No optexes were saved");
        return saved;
    }

    public BooleanOptex<C> toBooleanOptex() {
        if (data.isEmpty() || !(data.get() instanceof Boolean)) {
            return new BooleanOptex<>(Optional.empty());
        }
        return new BooleanOptex<>((Optional<Boolean>) data);
    }

    public V get() {
        return data.get();
    }

    @NotNull
    public static <V> Optex<V, V> optex(V value) {
        return new Optex<>(value);
    }

    public static class BooleanOptex<C> extends Optex<Boolean, C> {

        private BooleanOptex(Optional<Boolean> value) {
            super(value);
        }

        public BooleanOptex<C> ifElse(Runnable trueStatement, Runnable falseStatement) {
            if (data.isEmpty()) return this;
            if (!data.get())
                falseStatement.run();
            else
                trueStatement.run();
            return this;
        }

    }
}
