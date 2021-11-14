package ru.armagidon.poseplugin.api.utility;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

//Optional extension
public final class Optex<V,C>
{
    private Optex<C, C> saved;
    private final Optional<V> data;

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

    public Optex<V,C> filter(Predicate<? super V> predicate) {
        data.filter(predicate);
        return this;
    }

    public Optex<V,C> ifPresent(Consumer<? super V> action) {
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

    public Optex<C, C> rollback() {
        if (saved == null)
            throw new NullPointerException("No optexes were saved");
        return saved;
    }

 /*   public <U> Optex<V, C> mappedBranch(Function<V, ? extends U> mapper, Consumer<U> action) {
        data.map(mapper).ifPresent(action);
        return this;
    }*/

    @NotNull
    public static <V> Optex<V, V> optex(V value) {
        return new Optex<>(value);
    }
}
