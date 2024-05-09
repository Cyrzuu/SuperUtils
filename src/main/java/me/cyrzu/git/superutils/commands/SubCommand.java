package me.cyrzu.git.superutils.commands;

import me.cyrzu.git.superutils.commands.annotations.CommandName;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public interface SubCommand {

    @NotNull
    default String getName() {
        if(this.getClass().isAnnotationPresent(CommandName.class)) {
            CommandName value = this.getClass().getAnnotation(CommandName.class);
            return value.value();
        }

        return this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
    }

    default void execute(@NotNull CommandSender commandSender, @NotNull CommandContext context) { }

    default void execute(@NotNull ConsoleCommandSender console, @NotNull CommandContext context) { }

    default void execute(@NotNull Player player, @NotNull CommandContext context) { }

    @NotNull
    default List<String> tabComplete(@NotNull Player player, @NotNull CommandContext context) {
        return new ArrayList<>();
    }

    @NotNull
    default List<String> tabComplete(@NotNull CommandSender sender, @NotNull CommandContext context) {
        return new ArrayList<>();
    }

    default void usage(@NotNull CommandSender sender) { }

    default boolean hasPermission(@NotNull CommandSender sender) {
        return true;
    }

    @NotNull
    default String permissionMessage() {
        return "";
    }

}
