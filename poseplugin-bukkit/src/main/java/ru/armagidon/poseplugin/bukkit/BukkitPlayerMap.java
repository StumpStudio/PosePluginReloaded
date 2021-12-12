package ru.armagidon.poseplugin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.armagidon.poseplugin.PosePlugin;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.api.subsystems.implefine.Implementation;
import ru.armagidon.poseplugin.api.subsystems.implefine.UseImplementationFor;
import ru.armagidon.poseplugin.api.utility.LazyObject;

import static ru.armagidon.poseplugin.api.utility.LazyObject.lazy;

@UseImplementationFor(parent = PlayerMap.class)
public class BukkitPlayerMap extends PlayerMap<Player> implements Listener
{
    @Implementation
    private static final LazyObject<BukkitPlayerMap> PLAYER_MAP_INSTANCE = lazy(() -> new BukkitPlayerMap(PosePlugin.getPlugin(PosePlugin.class)), BukkitPlayerMap.class);

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
