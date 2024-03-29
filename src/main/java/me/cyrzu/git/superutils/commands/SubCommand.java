package me.cyrzu.git.superutils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {

    @Getter
    @NotNull
    private final String name;

    public SubCommand(@NotNull String name) {
        this.name = name;
    }

    public abstract void execute(@NotNull Player player, @NotNull CommandContext context);

    public void execute(@NotNull ConsoleCommandSender console, @NotNull CommandContext context) { }

    public void execute(@NotNull CommandSender sender, @NotNull CommandContext context) { }

    @NotNull
    public List<String> tabComplete(@NotNull Player player, @NotNull CommandContext context) {
        return new ArrayList<>();
    }

    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull CommandContext context) {
        return new ArrayList<>();
    }

    public final void run(@NotNull CommandSender sender, @NotNull String... args) {
        CommandContext context = new CommandContext(args);

        if(sender instanceof Player player) {
            execute(player, context);
        } else if(sender instanceof ConsoleCommandSender console) {
            execute(console, context);
        }

        execute(sender, context);
    }

}
