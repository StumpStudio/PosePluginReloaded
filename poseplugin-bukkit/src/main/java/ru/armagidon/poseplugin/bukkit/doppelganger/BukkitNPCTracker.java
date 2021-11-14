package ru.armagidon.poseplugin.bukkit.doppelganger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.Doppelganger;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.NPCTracker;

import java.util.concurrent.CompletableFuture;

public class BukkitNPCTracker extends NPCTracker<Player> implements Listener
{
    public BukkitNPCTracker(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @Override
    public void registerNPC(Doppelganger<Player, ?> doppelganger) {
        super.registerNPC(doppelganger);
        final var location = doppelganger.getOriginal().getLocation().clone();
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> !isTrackerOf(doppelganger, p))
                .filter(p -> p.getLocation().distance(location) < getViewDistance()).forEach(p -> {
                    doppelganger.render(p);
                    getTrackers().get(doppelganger).add(p);
                });
    }

    @Override
    public void unregisterNPC(Doppelganger<Player, ?> doppelganger) {
        doppelganger.despawn();
        super.unregisterNPC(doppelganger);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        final var from = e.getFrom();
        final var to = e.getTo();
        if (to == null) return;
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;
        final var mover = e.getPlayer();
        CompletableFuture.runAsync(() -> recalculateTrackerList(to, mover));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        final var from = e.getFrom();
        final var to = e.getTo();
        if (to == null) return;
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;
        final var mover = e.getPlayer();
        CompletableFuture.runAsync(() -> recalculateTrackerList(to, mover));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final var mover = event.getPlayer();
        final var currentLocation = mover.getLocation();
        CompletableFuture.runAsync(() -> {
            //Add mover if he is within a view distance
            getTrackers().entrySet().stream()
                    .filter(entry -> entry.getKey().distance(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ()) < getViewDistance())
                    .forEach(s -> {
                        s.getKey().render(mover);
                        s.getValue().add(mover);
                    });
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final var mover = event.getPlayer();
        CompletableFuture.runAsync(() ->
                getTrackers().entrySet().stream()
                .filter(entry -> isTrackerOf(entry.getKey(), mover))
                .forEach(s -> s.getValue().remove(mover)));
    }

    private void recalculateTrackerList(final Location current, final Player mover) {
        //Remove mover if them is no longer within a view distance
        getTrackers().entrySet().stream()
                .filter(entry -> isTrackerOf(entry.getKey(), mover))
                .filter(entry -> entry.getKey().distance(current.getX(), current.getY(), current.getZ()) > getViewDistance())
                .forEach(s -> {
                    s.getKey().unrender(mover);
                    s.getValue().remove(mover);
                });

        //Add mover if he is within a view distance
        getTrackers().entrySet().stream()
                .filter(entry -> !isTrackerOf(entry.getKey(), mover))
                .filter(entry -> entry.getKey().distance(current.getX(), current.getY(), current.getZ()) < getViewDistance())
                .forEach(s -> {
                    s.getKey().render(mover);
                    s.getValue().add(mover);
                });
    }
}
