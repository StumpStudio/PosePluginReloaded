package ru.armagidon.poseplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.armagidon.poseplugin.api.PosePluginAPI;
import ru.armagidon.poseplugin.api.player.P3Map;
import ru.armagidon.poseplugin.bukkit.BukkitNameTagHider;
import ru.armagidon.poseplugin.bukkit.BukkitPlayerHider;
import ru.armagidon.poseplugin.bukkit.BukkitPosePluginPlayer;
import ru.armagidon.poseplugin.bukkit.MainEventListener;

public class PosePlugin extends JavaPlugin
{

    @Override
    public void onLoad() {
        getLogger().info("Loading API....");
        try
        {
            PosePluginAPI.<Player>builder()
                    .nameTagHider(new BukkitNameTagHider())
                    .playerHider(new BukkitPlayerHider())
                    .playerMap(new P3Map<>(uuid ->
                            new BukkitPosePluginPlayer(Bukkit.getPlayer(uuid)))).build();
        } catch (IllegalArgumentException e) {
            getLogger().severe("Loading failed!");
            setEnabled(false);
        }
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new MainEventListener(), this);
    }

    @Override
    public void onDisable() {

    }
}
