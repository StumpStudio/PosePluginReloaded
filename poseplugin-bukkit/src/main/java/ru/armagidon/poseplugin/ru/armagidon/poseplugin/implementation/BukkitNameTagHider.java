package ru.armagidon.poseplugin.ru.armagidon.poseplugin.implementation;

import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.ru.armagidon.poseplugin.utils.NameTagHider;

//TODO implement BukkitNameTagHider
public class BukkitNameTagHider extends NameTagHider<Player> {

    @Override
    protected boolean handleHideNameTag(Player player) {
        return false;
    }

    @Override
    protected boolean handleShowNameTag(Player player) {
        return false;
    }
}
