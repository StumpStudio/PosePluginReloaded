package ru.armagidon.poseplugin.api.subsystems;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class NameTagHider<PlayerHandle>
{

    protected final Set<PlayerHandle> HIDDEN_PLAYERS = new HashSet<>();

    public final synchronized boolean hideNameTag(@NotNull PlayerHandle player) {
        if (HIDDEN_PLAYERS.contains(player)) return false;
        HIDDEN_PLAYERS.add(player);
        return handleHideNameTag(player);
    }

    public final synchronized boolean showNameTag(@NotNull PlayerHandle player) {
        if (!HIDDEN_PLAYERS.contains(player)) return false;
        HIDDEN_PLAYERS.add(player);
        return handleShowNameTag(player);
    }

    protected abstract boolean handleHideNameTag(PlayerHandle player);

    protected abstract boolean handleShowNameTag(PlayerHandle player);
}
