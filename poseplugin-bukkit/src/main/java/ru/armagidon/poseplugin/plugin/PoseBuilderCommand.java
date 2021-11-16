package ru.armagidon.poseplugin.plugin;

import com.google.common.base.Joiner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.armagidon.poseplugin.api.player.PlayerMap;
import ru.armagidon.poseplugin.api.pose.Pose;
import ru.armagidon.poseplugin.api.pose.PoseBatchBuilder;
import ru.armagidon.poseplugin.api.pose.PoseBuilder;
import ru.armagidon.poseplugin.api.utility.batch.BatchBuilder;
import ru.armagidon.poseplugin.bukkit.BukkitPoseBatchBuilder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class PoseBuilderCommand implements CommandExecutor, TabCompleter
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) return true;

            PoseBatchBuilder<Player> setupBatchBuilder = new BukkitPoseBatchBuilder();
            PoseBatchBuilder<Player> endBatchBuilder = new BukkitPoseBatchBuilder();

            if (args.length == 0) return false;
            final var joinedArgs = Joiner.on(' ').join(args);
            final var typeSplitOptions = joinedArgs.split(":");

            if (typeSplitOptions.length != 2) return false;

            Function<String, Runnable> setupOptionMapper = (input) -> () -> {
                switch (input) {
                    case "useseat" -> setupBatchBuilder.useSeat(p -> !p.stopPosing());
                    case "useplayerhider" -> setupBatchBuilder.usePlayerHider();
                    case "usedoppelganger" -> setupBatchBuilder.useDoppelganger();
                }
            };

            Function<String, Runnable> endOptionMapper = (input) -> () -> {
                switch (input) {
                    case "removeseat" -> endBatchBuilder.removeSeat();
                    case "turnoffplayerhiding" -> endBatchBuilder.turnOffPlayerHiding();
                    case "removedoppelganger" -> endBatchBuilder.removeDoppelganger();
                }
            };

            Arrays.stream(typeSplitOptions[0].split(" "))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(s -> batchBuilderMethods().map(Method::getName).anyMatch(m -> m.equalsIgnoreCase(s)))
                    .map(setupOptionMapper)
                    .forEach(Runnable::run);


            Arrays.stream(typeSplitOptions[1].split(" "))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(s -> batchBuilderMethods().map(Method::getName).anyMatch(m -> m.equalsIgnoreCase(s)))
                    .map(endOptionMapper)
                    .forEach(Runnable::run);

            Pose<Player> pose = PoseBuilder.<Player>create().setup(setupBatchBuilder.create()).end(endBatchBuilder.create()).build();
            PlayerMap.<Player>getInstance().getPlayer(player.getUniqueId()).changePose(pose);
            player.sendMessage("Pose was built");
            return true;
        } catch (Exception e) {
            sender.sendMessage(e.getMessage());
            e.printStackTrace();
            return true;
        }
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
        return Arrays.stream(PoseBatchBuilder.class.getMethods())
                .filter(m -> m.getReturnType().equals(PoseBatchBuilder.class));
    }
}
