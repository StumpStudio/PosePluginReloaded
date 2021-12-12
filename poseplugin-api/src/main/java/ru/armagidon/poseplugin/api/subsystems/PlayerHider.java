package ru.armagidon.poseplugin.api.subsystems;

import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.api.subsystems.implefine.Implefine;

import java.util.HashSet;
import java.util.Set;

public abstract class PlayerHider<PlayerHandle>
{

    protected final Set<PlayerHandle> HIDDEN_PLAYERS = new HashSet<>();



    public final synchronized boolean hidePlayer(@NotNull PlayerHandle player) {
        if (HIDDEN_PLAYERS.contains(player)) return false;
        return handleHidePlayer(player) && HIDDEN_PLAYERS.add(player);
    }

    public final synchronized boolean showPlayer(@NotNull PlayerHandle player) {
        if (!HIDDEN_PLAYERS.contains(player)) return false;
        return handleShowPlayer(player) && HIDDEN_PLAYERS.remove(player);
    }

    protected abstract boolean handleHidePlayer(PlayerHandle player);

    protected abstract boolean handleShowPlayer(PlayerHandle player);

    @SuppressWarnings("unchecked")
    public static <P> PlayerHider<P> getInstance() {
        return Implefine.get(PlayerHider.class);
    }
}
