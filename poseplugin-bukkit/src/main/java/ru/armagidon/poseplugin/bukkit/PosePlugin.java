package ru.armagidon.poseplugin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import ru.armagidon.poseplugin.api.PoseAPI;
import ru.armagidon.poseplugin.bukkit.utility.MainEventListener;

public final class PosePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getServicesManager().register(PoseAPI.class, new BukkitPoseAPI(), this, ServicePriority.Normal);
        Bukkit.getPluginManager().registerEvents(new MainEventListener(), this);
    }

}