package ru.armagidon.poseplugin.api.event;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.*;

public class EventPublisher
{

    private static final Map<Class<? extends Event>, List<EventSubscriber<? extends Event>>> subscribers = new HashMap<>();


    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean subscribe(MultiSubscriber multiSubscriber) {
        Objects.requireNonNull(multiSubscriber);
        Arrays.stream(multiSubscriber.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Subscribe.class))
                .filter(method -> method.getReturnType().equals(void.class) || method.getReturnType().equals(Void.class))
                .filter(method -> (method.getModifiers() & (Modifier.PRIVATE | Modifier.STATIC)) == 0)
                .filter(method -> method.getParameterTypes().length == 1)
                .filter(method -> Event.class.isAssignableFrom(method.getParameterTypes()[0]))
                .forEach(method -> {
                    Subscribe subscribe = method.getAnnotation(Subscribe.class);
                    MethodEventSubscriber<?> subscriber = new MethodEventSubscriber<>(subscribe.priority(), method, multiSubscriber);
                    subscribe((Class) method.getParameterTypes()[0], subscriber);
                });
        return true;
    }

    public static boolean unsubscribeAll(Class<? extends Event> eventClass) {
        Objects.requireNonNull(eventClass);
        return Optional.ofNullable(subscribers.get(eventClass)).map(list -> {
            list.clear();
            return true;
        }).orElse(false);
    }

    public static <E extends Event> boolean unsubscribe(Class<E> eventClass, EventSubscriber<E> subscriber) {
        Objects.requireNonNull(eventClass);
        Objects.requireNonNull(subscriber);
        return Optional.ofNullable(subscribers.get(eventClass)).map(subs -> subs.remove(subscriber)).orElse(false);
    }

    public static <E extends Event> boolean subscribe(@NotNull Class<E> eventClass, @NotNull EventSubscriber<E> subscriber) {
        List<EventSubscriber<? extends Event>> subscriberSet = subscribers.computeIfAbsent(eventClass, (c) -> new LinkedList<>());
        if (subscriberSet.contains(subscriber)) return false;
        subscriberSet.add(subscriber);
        subscriberSet.sort(Comparator.comparingInt(sub -> sub.priority().ordinal()));
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Event> E call(E event) {
        return Optional.ofNullable(subscribers.get(event.getClass())).map(subs -> {
           subs.forEach(sub -> ((EventSubscriber<E>) sub).call(event));
           return event;
        }).orElse(event);
    }
}
