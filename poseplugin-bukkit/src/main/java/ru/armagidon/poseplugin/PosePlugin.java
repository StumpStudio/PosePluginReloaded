package ru.armagidon.poseplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.api.pose.Pose;
import ru.armagidon.poseplugin.api.pose.PoseBatchBuilder;
import ru.armagidon.poseplugin.api.pose.PoseBuilder;
import ru.armagidon.poseplugin.api.utility.datastructures.Pair;
import ru.armagidon.poseplugin.api.utility.enums.Direction;
import ru.armagidon.poseplugin.bukkit.BukkitPoseBatchBuilder;
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
                () -> {
                    //Register players
                    Bukkit.getOnlinePlayers().forEach(p -> PlayerMap.getInstance().registerPlayer(p.getUniqueId()));
                });
        //Start seat daemon
        initSubsystem("Starting seat daemon....", "Seat daemon started",
                () -> SeatDaemon.start(this));


        optex(getCommand("posebuilder"))
                .map(c -> Pair.of(c, new PoseBuilderCommand()))
                .ifPresent(p -> p.first().setExecutor(p.second()))
                .ifPresent(p -> p.first().setTabCompleter(p.second()));
        optex(getCommand("posestop"))
                .map(c -> Pair.of(c, new PoseStopCommand()))
                .ifPresent(p -> p.first().setExecutor(p.second()))
                .ifPresent(p -> p.first().setTabCompleter(p.second()));
        optex(getCommand("lay"))
                .map(c -> Pair.of(c, (CommandExecutor) (sender, cmd, s, args) -> {
                    if (!(sender instanceof Player player)) return true;
                    PoseBatchBuilder<Player> setupBatchBuilder = new BukkitPoseBatchBuilder();
                    PoseBatchBuilder<Player> endBatchBuilder = new BukkitPoseBatchBuilder();
                    setupBatchBuilder.usePlayerHider().useSeat(p -> !p.stopPosing()).useDoppelganger(executor ->
                            executor.lay(Direction.yawToDirection(player.getLocation().getYaw()).getOpposite()));

                    endBatchBuilder.removeDoppelganger().removeSeat().turnOffPlayerHiding();
                    Pose<Player> pose = PoseBuilder.<Player>create().setup(setupBatchBuilder.create()).end(endBatchBuilder.create()).build();
                    PlayerMap.<Player>getInstance().getPlayer(player.getUniqueId()).changePose(pose);
                    return true;
                })).ifPresent(p -> p.first().setExecutor(p.second()));
        optex(getCommand("sit"))
                .map(c -> Pair.of(c, (CommandExecutor) (sender, cmd, s, args) -> {
                    if (!(sender instanceof Player player)) return true;
                    PoseBatchBuilder<Player> setupBatchBuilder = new BukkitPoseBatchBuilder();
                    PoseBatchBuilder<Player> endBatchBuilder = new BukkitPoseBatchBuilder();
                    setupBatchBuilder.useSeat(p -> !p.stopPosing());

                    endBatchBuilder.removeSeat();
                    Pose<Player> pose = PoseBuilder.<Player>create().setup(setupBatchBuilder.create()).end(endBatchBuilder.create()).build();
                    PlayerMap.<Player>getInstance().getPlayer(player.getUniqueId()).changePose(pose);
                    return true;
                })).ifPresent(p -> p.first().setExecutor(p.second()));

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
