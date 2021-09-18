package ru.armagidon.poseplugin.bukkit;

import org.bukkit.entity.Player;
import ru.armagidon.poseplugin.api.utility.NameTagHider;

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
