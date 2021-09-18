package ru.armagidon.poseplugin.api;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Представление позирующего игрока.
 */
public interface PosingPlayer<P> {

    /**
     * Получить игрока, который позирует.
     * @return игрок
     */
    @NotNull P getPlayer();

    /**
     * Получить экземпляр применённой позы.
     * @return поза
     */
    @NotNull Pose<P> getPose();

    /**
     * Попытаться применить новую позу.
     * @param pose поза
     * @return {@code true}, если поза успешно применена, иначе - {@code false}.
     */
    boolean apply(final @NotNull Pose<P> pose);

    /**
     * Попытаться применить новую позу асинхронно.
     * @param pose поза
     * @return {@code true}, если поза успешно применена, иначе - {@code false}.
     */
    default CompletableFuture<Boolean> applyAsync(final @NotNull Pose<P> pose) {
        return CompletableFuture.supplyAsync(() -> apply(pose));
    }

    /**
     * Попытаться прервать позирование.
     * @return {@code true}, если позирование успешно прервано, иначе - {@code false}.
     */
    boolean cancel();

    /**
     * Попытаться прервать позирование асинхронно.
     * @return {@code true}, если позирование успешно прервано, иначе - {@code false}.
     */
    default CompletableFuture<Boolean> cancelAsync() {
        return CompletableFuture.supplyAsync(this::cancel);
    }

}