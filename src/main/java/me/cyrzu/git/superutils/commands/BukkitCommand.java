package me.cyrzu.git.superutils.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

class BukkitCommand extends Command {

    @NotNull
    private final PluginCommand pluginCommand;

    @NotNull
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    @NotNull
    private final Map<UUID, Long> cooldownMap = new ConcurrentHashMap<>();

    private long cooldown = 0;

    @NotNull
    private String cooldownMessage = "";

    protected BukkitCommand(@NotNull String name, @NotNull PluginCommand pluginCommand) {
        super(name);
        this.pluginCommand = pluginCommand;
    }

    public void setSubCommands(@NotNull Map<String, SubCommand> subCommands) {
        this.subCommands.clear();
        this.subCommands.putAll(subCommands);
    }

    public void setCooldown(long time, @Nullable String message) {
        this.cooldown = Math.max(0, time);
        this.cooldownMessage = message != null ? message : "";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(cooldown > 0 && sender instanceof Player player && !player.isOp()) {
            long current = System.currentTimeMillis();
            UUID uuid = player.getUniqueId();
            long cooldownExTime = cooldownMap.getOrDefault(uuid, 0L);

            if(current < cooldownExTime) {
                if(cooldownMessage.isEmpty()) {
                    return false;
                }

                long remaing = TimeUnit.MILLISECONDS.toSeconds(cooldownExTime - current);
                player.sendMessage(cooldownMessage.replace("%sec", String.valueOf(remaing)));
                return false;
            }

            cooldownMap.put(uuid, current + cooldown);
        }

        if(subCommands.isEmpty() || args.length == 0) {
            execute(sender, args);
            return true;
        }

        String subString = args[0].toLowerCase(Locale.ENGLISH);
        SubCommand subCommand = subCommands.get(subString);
        if(subCommand == null) {
            pluginCommand.help(sender);
            return true;
        }

        if(!subCommand.hasPermission(sender)) {
            String message = subCommand.permissionMessage();
            if(message.isEmpty()) {
                return true;
            }

            sender.sendMessage(message);
            return true;
        }

        this.execute(sender, subCommand, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    private void execute(@NotNull CommandSender sender, @NotNull String... args) {
        CommandContext context = new CommandContext(args);
        if(sender instanceof Player player) {
            pluginCommand.execute(player, context);
        } else if(sender instanceof ConsoleCommandSender console) {
            pluginCommand.execute(console, context);
        }

        pluginCommand.execute(sender, context);
    }

    private void execute(@NotNull CommandSender sender, @NotNull SubCommand subCommand, @NotNull String... args) {
        CommandContext context = new CommandContext(args);
        if(sender instanceof Player player) {
            subCommand.execute(player, context);
        } else if(sender instanceof ConsoleCommandSender console) {
            subCommand.execute(console, context);
        }

        subCommand.execute(sender, context);
    }

    @NotNull
    @Override
    public final List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        if(subCommands.isEmpty()) {
            return this.tabComplete(sender, args);
        }

        if(args.length <= 1) {
            return subCommands.values().stream()
                .filter(sub -> sub.hasPermission(sender))
                .map(SubCommand::getName)
                .filter(name -> args[0].isEmpty() || name.toLowerCase().startsWith(args[0]))
                .toList();
        }

        String subString = args[0].toLowerCase(Locale.ENGLISH);
        SubCommand subCommand = subCommands.get(subString);
        return subCommand != null ? this.tabComplete(sender, subCommand, args) : new ArrayList<>();
    }

    private List<String> tabComplete(@NotNull CommandSender sender, @NotNull String... args) {
        CommandContext context = new CommandContext(args);

        List<String> tabs =  sender instanceof Player player ?
            pluginCommand.tabComplete(player, context) :
            pluginCommand.tabComplete(sender, context);

        String arg = context.get(context.size() - 1, "");
        return tabs.stream().filter(tab -> arg.isEmpty() || tab.toLowerCase().startsWith(arg.toLowerCase())).toList();
    }

    private List<String> tabComplete(@NotNull CommandSender sender, @NotNull SubCommand subCommand, @NotNull String... args) {
        CommandContext context = new CommandContext(Arrays.copyOfRange(args, 1, args.length));

        List<String> tabs = sender instanceof Player player ?
            subCommand.tabComplete(player, context) :
            subCommand.tabComplete(sender, context);

        String arg = context.get(context.size() - 1, "");
        return tabs.stream().filter(tab -> arg.isEmpty() || tab.toLowerCase().startsWith(arg.toLowerCase())).toList();
    }

}
