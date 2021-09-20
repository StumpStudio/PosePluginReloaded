package ru.armagidon.poseplugin.api.event;

import lombok.SneakyThrows;

import java.lang.reflect.Method;

public class MethodEventSubscriber<E extends Event> implements EventSubscriber<E>
{

    private final Priority priority;
    private final Method method;
    private final MultiSubscriber source;

    public MethodEventSubscriber(Priority priority, Method method, MultiSubscriber source) {
        this.priority = priority;
        this.method = method;
        this.source = source;
    }

    @Override
    @SneakyThrows
    public void call(E event) {
        method.invoke(source, event);
    }

    @Override
    public Priority priority() {
        return priority;
    }
}
