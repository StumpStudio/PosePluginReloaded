package ru.armagidon.poseplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.api.subsystems.PlayerHider;
import ru.armagidon.poseplugin.api.subsystems.doppelganger.NPCTracker;
import ru.armagidon.poseplugin.api.utility.Pair;
import ru.armagidon.poseplugin.bukkit.BukkitPlayerHider;
import ru.armagidon.poseplugin.bukkit.BukkitPlayerMap;
import ru.armagidon.poseplugin.bukkit.doppelganger.BukkitNPCTracker;
import ru.armagidon.poseplugin.bukkit.seat.SeatDaemon;
import ru.armagidon.poseplugin.plugin.PoseBuilderCommand;
import ru.armagidon.poseplugin.plugin.PoseStopCommand;

import static ru.armagidon.poseplugin.api.utility.Optex.optex;

public class PosePlugin extends JavaPlugin
{

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        //Create player map

        initSubsystem("Creating PlayerMap....","PlayerMap created",
                () -> PlayerMap.init(new BukkitPlayerMap(this)));

        //Register players
        Bukkit.getOnlinePlayers().forEach(p -> PlayerMap.getInstance().registerPlayer(p.getUniqueId()));
        //Start seat daemon
        initSubsystem("Starting seat daemon....", "Seat daemon started",
                () -> SeatDaemon.start(this));
        //Start player hider
        initSubsystem("Starting player hider....", "Player hider started",
                () -> PlayerHider.init(new BukkitPlayerHider(this)));
        //Start npc tracker
        initSubsystem("Starting npc tracker...", "npc tracker started",
                () -> NPCTracker.init(new BukkitNPCTracker(this)));


        optex(getCommand("posebuilder"))
                .map(c -> Pair.of(c, new PoseBuilderCommand()))
                .ifPresent(p -> p.first().setExecutor(p.second()))
                .ifPresent(p -> p.first().setTabCompleter(p.second()));
        optex(getCommand("posestop"))
                .map(c -> Pair.of(c, new PoseStopCommand()))
                .ifPresent(p -> p.first().setExecutor(p.second()))
                .ifPresent(p -> p.first().setTabCompleter(p.second()));

    }

    @Override
    public void onDisable() {
        PlayerMap.getInstance().unregisterAll();
    }

    private void initSubsystem(String start, String end, Runnable initializer) {
        getLogger().info(start);
        initializer.run();
        getLogger().info(end);
    }
}
