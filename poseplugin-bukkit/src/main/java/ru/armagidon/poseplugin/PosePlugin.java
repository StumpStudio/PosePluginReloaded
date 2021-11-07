package ru.armagidon.poseplugin;

import org.bukkit.plugin.java.JavaPlugin;
import ru.armagidon.poseplugin.api.PosePluginAPI;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.bukkit.BukkitPlayerMap;
import ru.armagidon.poseplugin.bukkit.BukkitPosePluginAPI;
import ru.armagidon.poseplugin.bukkit.BukkitSeat;

public class PosePlugin extends JavaPlugin
{

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        //Init API
        getLogger().info("Initializing PosePluginAPI....");
        PosePluginAPI.init(new BukkitPosePluginAPI());
        getLogger().info("PosePluginAPI initialized");
        //Create player map
        getLogger().info("Creating PlayerMap....");
        PlayerMap.init(new BukkitPlayerMap(this));
        getLogger().info("PlayerMap created");
        //Start seat daemon
        getLogger().info("Starting seat daemon....");
        BukkitSeat.SeatDaemon.start(this);
        getLogger().info("Seat daemon started");
    }

    @Override
    public void onDisable() {

    }
}
