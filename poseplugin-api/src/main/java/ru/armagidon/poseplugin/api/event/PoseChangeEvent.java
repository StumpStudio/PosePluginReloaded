package ru.armagidon.poseplugin.api.event;

import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.Pose;
import ru.armagidon.poseplugin.api.PosingPlayer;

/**
 * Представление события смены позы.
 */
public interface PoseChangeEvent<P> {

    /**
     * Новая поза.
     * @return поза
     */
    @NotNull Pose<P> getNew();

    /**
     * Предыдущая поза.
     * @return поза
     */
    @NotNull Pose<P> getPrevious();

    /**
     * Получить игрока, который позирует.
     * @return позирующий игрок
     */
    @NotNull PosingPlayer<P> getPosingPlayer();

    /**
     * Отменёно ли событие?
     * @return {@code true}, если да, иначе - {@code false}.
     */
    boolean isCancelled();

    /**
     * Отменить/Возобновить событие.
     */
    void setCancelled(final boolean cancelled);

}