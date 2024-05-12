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
            this.sync(task);
        }
    }

    public void sync(@NotNull Runnable task) {
        scheduler.runTask(plugin, task);
    }
    
    public void async(@NotNull Runnable task) {
        scheduler.runTaskAsynchronously(plugin, task);
    }

    public void later(int delay, @NotNull Runnable task) {
        scheduler.runTaskLater(plugin, task, delay);
    }

    public void laterAsync(int delay, @NotNull Runnable task) {
        scheduler.runTaskLaterAsynchronously(plugin, task, delay);
    }

    public void timer(int perioud, @NotNull Runnable task) {
        this.timer(perioud, t -> task.run());
    }
    
    public void timer(int perioud, @NotNull Consumer<BukkitTask> task) {
        scheduler.runTaskTimer(plugin, task, 0, perioud);
    }

    public void timer(int perioud, int delay, @NotNull Runnable task) {
        this.timer(perioud, delay, t -> task.run());
    }
    
    public void timer(int perioud, int delay, @NotNull Consumer<BukkitTask> task) {
        scheduler.runTaskTimer(plugin, task, delay, perioud);
    }

    public void timerAsync(int perioud, @NotNull Runnable task) {
        this.timerAsync(perioud, t -> task.run());
    }
    
    public void timerAsync(int perioud, @NotNull Consumer<BukkitTask> task) {
        scheduler.runTaskTimerAsynchronously(plugin, task, 0, perioud);
    }

    public void timerAsync(int perioud, int delay, @NotNull Runnable task) {
        this.timerAsync(perioud, delay, t -> task.run());
    }
    
    public void timerAsync(int perioud, int delay, @NotNull Consumer<BukkitTask> task) {
        scheduler.runTaskTimerAsynchronously(plugin, task, delay, perioud);
    }

}
