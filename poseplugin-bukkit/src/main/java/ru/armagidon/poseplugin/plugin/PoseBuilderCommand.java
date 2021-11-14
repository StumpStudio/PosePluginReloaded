package ru.armagidon.poseplugin.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.api.pose.Pose;
import ru.armagidon.poseplugin.api.pose.PoseBuilder;
import ru.armagidon.poseplugin.api.pose.batch.BatchBuilder;
import ru.armagidon.poseplugin.bukkit.BukkitBatchBuilder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PoseBuilderCommand implements CommandExecutor, TabCompleter
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        BatchBuilder<Player> builder = new BukkitBatchBuilder();

        Arrays.stream(args)
                .filter(s -> batchBuilderMethods()
                        .map(Method::getName)
                        .anyMatch(m -> m.equalsIgnoreCase(s)))
                .forEach(s -> {
                    switch (s.toLowerCase()) {
                        case "useseat" -> builder.useSeat(p -> !p.stopPosing());
                        case "useplayerhider" -> builder.usePlayerHider();
                        case "usedoppelganger" -> builder.useDoppelganger();
                    }
                });

        Pose<Player> pose = PoseBuilder.<Player>create().setup(builder.create()).build();
        PlayerMap.<Player>getInstance().getPlayer(player.getUniqueId()).changePose(pose);
        player.sendMessage("Pose was built");
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return batchBuilderMethods()
                .map(Method::getName)
                .filter(m -> Arrays.stream(args).noneMatch(a -> a.equalsIgnoreCase(m)))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .filter(m -> m.startsWith(args[args.length - 1]))
                .toList();
    }

    private static Stream<Method> batchBuilderMethods() {
        return Arrays.stream(BatchBuilder.class.getMethods())
                .filter(m -> m.getReturnType().equals(BatchBuilder.class));
    }
}
