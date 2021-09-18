package ru.armagidon.poseplugin.ru.armagidon.poseplugin.implementation;

import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.ru.armagidon.poseplugin.utils.PlayerHider;

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
