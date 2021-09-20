package ru.armagidon.poseplugin.api.event;

@FunctionalInterface
public interface EventSubscriber<E extends Event>
{
    void call(E event);

    default Priority priority() {
        return Priority.NORMAL;
    }
}
