package ru.armagidon.poseplugin.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.armagidon.poseplugin.api.player.PlayerMap;

import java.util.List;
import java.util.Optional;

public class PoseStopCommand implements CommandExecutor, TabCompleter
{

    @Override
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(s instanceof Player sender)) return true;
        Optional.ofNullable(PlayerMap.getPlayer(sender.getUniqueId(), Player.class))
                .ifPresent(p -> p.stopPosingAsync().thenAccept((state) -> {
                    if (state) {
                        sender.sendMessage("Pose stopped");
                    } else {
                        sender.sendMessage("You you're not posing");
                    }
                }));
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
