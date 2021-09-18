package ru.armagidon.poseplugin.bukkit.utility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

//TODO implement BukkitNameTagHider
public class BukkitNameTagHider {

    protected final Set<Player> HIDDEN_PLAYERS = new HashSet<>();

    public final synchronized boolean hideNameTag(@NotNull Player player) {
        if (HIDDEN_PLAYERS.contains(player)) return false;
        HIDDEN_PLAYERS.add(player);
        return handleHideNameTag(player);
    }

    public final synchronized boolean showNameTag(@NotNull Player player) {
        if (!HIDDEN_PLAYERS.contains(player)) return false;
        HIDDEN_PLAYERS.add(player);
        return handleShowNameTag(player);
    }

    protected boolean handleHideNameTag(Player player) {
        return false;
    }

    protected boolean handleShowNameTag(Player player) {
        return false;
    }
}