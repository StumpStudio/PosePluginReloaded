package ru.armagidon.poseplugin.api;

import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.utility.PropertyMap;

/**
 * Представление позы.
 */
public interface Pose<P> {

    /**
     * Применить позу к игроку.
     * @param player игрок
     */
    void apply(final @NotNull P player);

    /**
     * Завершить позу игрока.
     * @param player игрок
     */
    void cancel(final @NotNull P player);

    /**
     * Получить карту настроек позы.
     * @return настройки
     */
    @NotNull PropertyMap getPropertyMap();

}