package ru.armagidon.poseplugin.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.Pose;
import ru.armagidon.poseplugin.api.PosingPlayer;
import ru.armagidon.poseplugin.api.event.PoseChangeEvent;

/**
 * Реализация {@link PoseChangeEvent<Player>} для Bukkit.
 */
public final class BukkitPoseChangeEvent extends Event implements PoseChangeEvent<Player>, Cancellable {
    /* константы */
    private final static HandlerList HANDLER_LIST = new HandlerList();

    /* экземпляр */
    private final Pose<Player> newPose;
    private final Pose<Player> previousPose;
    private final PosingPlayer<Player> player;

    /* отменено ли событие? */
    private boolean cancelled;

    public BukkitPoseChangeEvent(@NotNull Pose<Player> newPose, @NotNull Pose<Player> previousPose, @NotNull PosingPlayer<Player> player) {
        this.newPose = newPose;
        this.previousPose = previousPose;

        this.player = player;
    }

    @Override
    public @NotNull Pose<Player> getNew() {
        return this.newPose;
    }

    @Override
    public @NotNull Pose<Player> getPrevious() {
        return this.previousPose;
    }

    @Override
    public @NotNull PosingPlayer<Player> getPosingPlayer() {
        return this.player;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return BukkitPoseChangeEvent.HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return BukkitPoseChangeEvent.HANDLER_LIST;
    }
}