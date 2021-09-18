package ru.armagidon.poseplugin.bukkit.utility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.Pose;
import ru.armagidon.poseplugin.api.PosingPlayer;

//TODO implement bukkit PosePluginPlayer
public final class BukkitPosePluginPlayer implements PosingPlayer<Player> {
    @NotNull
    @Override
    public Player getPlayer() {
        return null;
    }

    @NotNull
    @Override
    public Pose<Player> getPose() {
        return null;
    }

    @Override
    public boolean apply(@NotNull Pose<Player> pose) {
        return false;
    }

    @Override
    public boolean cancel() {
        return false;
    }
}