package ru.armagidon.poseplugin.api.utility;

import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class Property<T> {
    private final Consumer<T> initializer;
    private final Consumer<T> setter;
    private final Supplier<T> getter;

    private @Getter boolean initialized = false;

    public Property(Supplier<T> getter, Consumer<T> setter, Consumer<T> initializer) {
        this.setter = setter;
        this.getter = getter;
        this.initializer = initializer;
    }

    public Property(Supplier<T> getter, Consumer<T> setter){
        this(getter,setter,setter);
    }

    public T getValue() {
        return getter.get();
    }

    public Class<?> getValueClass(){
        return getter.get().getClass();
    }

    public void setValue(T value) {
        if(value == this.getter.get()) return;
        setter.accept(value);
    }

    public void initialize(T value){
        if(!initialized) {
            initializer.accept(value);
            initialized = true;
        }
    }
}
