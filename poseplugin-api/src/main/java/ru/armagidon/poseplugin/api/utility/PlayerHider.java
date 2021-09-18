package ru.armagidon.poseplugin.api.utility;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class PlayerHider<PlayerHandle>
{
    protected final Set<PlayerHandle> HIDDEN_PLAYERS = new HashSet<>();

    public final synchronized boolean hidePlayer(@NotNull PlayerHandle player) {
        if (HIDDEN_PLAYERS.contains(player)) return false;
        HIDDEN_PLAYERS.add(player);
        return handleHidePlayer(player);
    }

    public final synchronized boolean showPlayer(@NotNull PlayerHandle player) {
        if (!HIDDEN_PLAYERS.contains(player)) return false;
        HIDDEN_PLAYERS.add(player);
        return handleShowPlayer(player);
    }

    protected abstract boolean handleHidePlayer(PlayerHandle player);

    protected abstract boolean handleShowPlayer(PlayerHandle player);
}
