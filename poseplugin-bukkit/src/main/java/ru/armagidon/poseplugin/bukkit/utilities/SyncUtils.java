package ru.armagidon.poseplugin.bukkit.utilities;

import org.bukkit.Bukkit;
import ru.armagidon.poseplugin.PosePlugin;

public final class SyncUtils {

    private SyncUtils() {}

    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(PosePlugin.getPlugin(PosePlugin.class), runnable);
    }

    public static void delay(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(PosePlugin.getPlugin(PosePlugin.class), runnable, delay);
    }
}
