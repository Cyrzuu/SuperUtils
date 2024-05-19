package me.cyrzu.git.superutils.helper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Scheduler {

    @NotNull
    private final Plugin plugin;

    @NotNull
    private final BukkitScheduler scheduler;

    public Scheduler(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = Bukkit.getScheduler();
    }

    public void asyncIf(@NotNull Runnable task, boolean async) {
        if(async) {
            this.async(task);
        } else {
            task.run();
        }
    }

    public void sync(@NotNull Runnable task) {
        scheduler.runTask(plugin, task);
    }
    
    public void async(@NotNull Runnable task) {
        scheduler.runTaskAsynchronously(plugin, task);
    }

    public void later(@NotNull Runnable task, int delay) {
        scheduler.runTaskLater(plugin, task, delay);
    }

    public void laterAsync(@NotNull Runnable task, int delay) {
        scheduler.runTaskLaterAsynchronously(plugin, task, delay);
    }

    public BukkitTask timer(@NotNull Runnable task, int perioud) {
        return scheduler.runTaskTimer(plugin, task, 0, perioud);
    }

    public void timer(@NotNull Consumer<BukkitTask> task, int perioud) {
        scheduler.runTaskTimer(plugin, task, 0, perioud);
    }

    public BukkitTask timer(@NotNull Runnable task, int delay, int perioud) {
        return scheduler.runTaskTimer(plugin, task, delay, perioud);
    }

    public void timer(@NotNull Consumer<BukkitTask> task, int perioud, int delay) {
        scheduler.runTaskTimer(plugin, task, delay, perioud);
    }

    public BukkitTask timerAsync(@NotNull Runnable task, int perioud) {
        return scheduler.runTaskTimerAsynchronously(plugin, task, 0, perioud);
    }
    
    public void timerAsync(@NotNull Consumer<BukkitTask> task, int perioud) {
        scheduler.runTaskTimerAsynchronously(plugin, task, 0, perioud);
    }

    public BukkitTask timerAsync(@NotNull Runnable task, int delay, int perioud) {
        return scheduler.runTaskTimerAsynchronously(plugin, task, delay, perioud);
    }

    public void timerAsync(@NotNull Consumer<BukkitTask> task, int delay, int perioud) {
        scheduler.runTaskTimerAsynchronously(plugin, task, delay, perioud);
    }

}
