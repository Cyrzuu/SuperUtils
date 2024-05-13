package me.cyrzu.git.superutils.commands;

import me.cyrzu.git.superutils.commands.annotations.CommandName;
import me.cyrzu.git.superutils.commands.annotations.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SubCommand {

    @Nullable
    public org.bukkit.permissions.Permission permission;

    public SubCommand() {
        String permission = this.getPermission();
        this.permission = permission == null ? null : new org.bukkit.permissions.Permission(permission);
    }

    @NotNull
    public String getName() {
        if(this.getClass().isAnnotationPresent(CommandName.class)) {
            CommandName value = this.getClass().getAnnotation(CommandName.class);
            return value.value();
        }

        return this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
    }

    @Nullable
    public String getPermission() {
        if(this.getClass().isAnnotationPresent(Permission.class)) {
            Permission value = this.getClass().getAnnotation(Permission.class);
            return value.value();
        }

        return null;
    }

    public void execute(@NotNull CommandSender commandSender, @NotNull CommandContext context) { }

    public void execute(@NotNull ConsoleCommandSender console, @NotNull CommandContext context) { }

    public void execute(@NotNull Player player, @NotNull CommandContext context) { }

    @NotNull
    public List<String> tabComplete(@NotNull Player player, @NotNull CommandContext context) {
        return new ArrayList<>();
    }

    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull CommandContext context) {
        return new ArrayList<>();
    }

    public void usage(@NotNull CommandSender sender) { }

    public boolean hasPermission(CommandSender sender) {
        return this.permission == null || sender.hasPermission(this.permission);
    }

    @NotNull
    public String permissionMessage() {
        return "";
    }

}
