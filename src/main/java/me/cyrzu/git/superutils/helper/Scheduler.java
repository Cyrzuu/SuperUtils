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

    public void async(@NotNull Runnable fun) {
        scheduler.runTaskAsynchronously(plugin, fun);
    }

    public void later(int delay, @NotNull Runnable fun) {
        scheduler.runTaskLater(plugin, fun, delay);
    }

    public void laterAsync(int delay, @NotNull Runnable fun) {
        scheduler.runTaskLaterAsynchronously(plugin, fun, delay);
    }

    public void timer(int perioud, @NotNull Consumer<BukkitTask> fun) {
        scheduler.runTaskTimer(plugin, fun, 0, perioud);
    }

    public void timer(int perioud, int delay, @NotNull Consumer<BukkitTask> fun) {
        scheduler.runTaskTimer(plugin, fun, delay, perioud);
    }

    public void timerAsync(int perioud, @NotNull Consumer<BukkitTask> fun) {
        scheduler.runTaskTimerAsynchronously(plugin, fun, 0, perioud);
    }

    public void timerAsync(int perioud, int delay, @NotNull Consumer<BukkitTask> fun) {
        scheduler.runTaskTimerAsynchronously(plugin, fun, delay, perioud);
    }

}
