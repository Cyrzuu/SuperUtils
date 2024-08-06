package me.cyrzu.git.superutils.commands;

import me.cyrzu.git.superutils.commands.annotations.CommandName;
import me.cyrzu.git.superutils.commands.annotations.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface PluginCommand {

    @NotNull
    default String getName() {
        if(this.getClass().isAnnotationPresent(CommandName.class)) {
            CommandName value = this.getClass().getAnnotation(CommandName.class);
            return value.value();
        }

        return this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
    }

    default void execute(@NotNull CommandSender sender, @NotNull CommandContext context) { }

    default void execute(@NotNull ConsoleCommandSender console, @NotNull CommandContext context) { }

    default void execute(@NotNull Player player, @NotNull CommandContext context) { }

    @Nullable
    default Collection<String> tabComplete(@NotNull Player player, @NotNull CommandContext context) {
        return Collections.emptyList();
    }

    @Nullable
    default Collection<String> tabComplete(@NotNull CommandSender sender, @NotNull CommandContext context) {
        return Collections.emptyList();
    }

    default void help(@NotNull CommandSender sender) { }

    @NotNull
    default List<SubCommand> getSubCommands() {
        return new ArrayList<>();
    }

}
