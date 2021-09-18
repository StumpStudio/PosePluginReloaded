package ru.armagidon.poseplugin.bukkit;

import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.utility.PlayerHider;

//TODO implement BukkitPlayerHider
public class BukkitPlayerHider extends PlayerHider<Player>
{

    @Override
    protected boolean handleHidePlayer(Player player) {
        return false;
    }

    @Override
    protected boolean handleShowPlayer(Player player) {
        return false;
    }
}
