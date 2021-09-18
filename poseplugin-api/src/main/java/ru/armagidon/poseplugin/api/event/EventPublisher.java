package ru.armagidon.poseplugin.api.event;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.UnaryOperator;

public class EventPublisher
{

    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends Event>, Set<UnaryOperator>> subscribers = new HashMap<>();

    public static <E extends Event> void subscribe(@NotNull Class<E> clazz, @NotNull UnaryOperator<E> callback) {
        subscribers.computeIfAbsent(clazz, (c) -> new LinkedHashSet<>()).add(callback);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Event> E call(E event) {
        return (E) Optional.ofNullable(subscribers.get(event.getClass())).map(subs -> {
            subs.forEach(s -> s.apply(event));
            return (Event) event;
        }).orElse(event);
    }
}
