package ru.armagidon.poseplugin.ru.armagidon.poseplugin.implementation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.armagidon.poseplugin.api.PosePluginAPI;

public class MainEventListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        PosePluginAPI.<Player>getAPI().ifPresent(api -> api.getPlayerMap().registerPlayer(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        PosePluginAPI.<Player>getAPI().ifPresent(api -> api.getPlayerMap().unregisterPlayer(event.getPlayer().getUniqueId()));
    }
}
