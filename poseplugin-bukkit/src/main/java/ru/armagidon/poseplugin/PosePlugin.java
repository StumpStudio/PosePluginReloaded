package ru.armagidon.poseplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.PosePluginAPI;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.api.pose.Pose;
import ru.armagidon.poseplugin.api.pose.PoseBuilder;
import ru.armagidon.poseplugin.bukkit.BukkitBatchBuilder;
import ru.armagidon.poseplugin.bukkit.BukkitPlayerMap;
import ru.armagidon.poseplugin.bukkit.BukkitPosePluginAPI;
import ru.armagidon.poseplugin.bukkit.BukkitSeat;

public class PosePlugin extends JavaPlugin
{

    Pose<Player> pose;

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
        //Register players
        Bukkit.getOnlinePlayers().forEach(p -> PlayerMap.getInstance().registerPlayer(p.getUniqueId()));
        //Start seat daemon
        getLogger().info("Starting seat daemon....");
        BukkitSeat.SeatDaemon.start(this);
        getLogger().info("Seat daemon started");

        getCommand("sit").setExecutor(this);

        pose = PoseBuilder.<Player>create().setup(new BukkitBatchBuilder().useSeat(p -> !p.stopPosing()).create()).build();
    }

    @Override
    public void onDisable() {
        PlayerMap.getInstance().unregisterAll();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        PlayerMap.<Player>getInstance().getPlayer(player.getUniqueId()).changePose(pose);
        return true;
    }
}
