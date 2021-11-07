package ru.armagidon.poseplugin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.armagidon.poseplugin.api.player.PlayerMap;

public class BukkitPlayerMap extends PlayerMap<Player> implements Listener
{

    public BukkitPlayerMap(Plugin plugin) {
        super((uuid) -> new BukkitPoser(Bukkit.getPlayer(uuid)));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        registerPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        unregisterPlayer(event.getPlayer().getUniqueId());
    }

}
