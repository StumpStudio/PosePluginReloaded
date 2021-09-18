package ru.armagidon.poseplugin.bukkit.utility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

//TODO implement BukkitPlayerHider
public final class BukkitPlayerHider {
    private final Set<Player> HIDDEN_PLAYERS = new HashSet<>();

    public final synchronized boolean hidePlayer(@NotNull Player player) {
        if (HIDDEN_PLAYERS.contains(player)) return false;
        HIDDEN_PLAYERS.add(player);
        return true; // ???
    }

    public final synchronized boolean showPlayer(@NotNull Player player) {
        if (!HIDDEN_PLAYERS.contains(player)) return false;
        HIDDEN_PLAYERS.add(player);
        return true; // ???
    }
}