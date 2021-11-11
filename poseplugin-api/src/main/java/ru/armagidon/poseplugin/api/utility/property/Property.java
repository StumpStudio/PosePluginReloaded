package ru.armagidon.poseplugin.api.utility.property;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Property<T>
{
    private final String id;
    private final Consumer<T> initializer;
    private final Consumer<T> setter;
    private final Supplier<T> getter;

    private boolean initialized = false;

    public Property(String id, Supplier<T> getter, Consumer<T> setter, Consumer<T> initializer) {
        this.id = id;
        this.setter = setter;
        this.getter = getter;
        this.initializer = initializer;
    }

    public Property(String id, Supplier<T> getter, Consumer<T> setter){
        this(id, getter,setter,setter);
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

    public String getId() {
        return id;
    }

    public boolean isInitialized() {
        return this.initialized;
    }
}
