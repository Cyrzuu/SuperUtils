package me.cyrzu.git.superutils.commands;

import lombok.Getter;
import lombok.SneakyThrows;
import me.cyrzu.git.superutils.color.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandManager implements Listener {

    @NotNull
    private final Plugin plugin;

    private CommandManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @NotNull
    private final Map<String, BukkitCommand> commands = new ConcurrentHashMap<>();

    @SneakyThrows
    public void register(@NotNull CommandBuilder builder) {
        String name = builder.getPluginCommand().getName().toLowerCase();

        BukkitCommand bukkitCommand = new BukkitCommand(plugin, name, builder.getPluginCommand());
        bukkitCommand.setSubCommands(builder.getSubCommands());
        bukkitCommand.setAliases(builder.getAliases());
        bukkitCommand.setPermission(builder.getPermission());
        bukkitCommand.setUsage(builder.getUsage());
        bukkitCommand.setDescription(builder.getDescription());
        bukkitCommand.setCooldown(builder.getCooldown(), ColorUtils.parseText(builder.getCooldownMessage()));

        this.register(bukkitCommand);
    }

    @SneakyThrows
    public void register(@NotNull PluginCommand pluginCommand) {
        String name = pluginCommand.getName().toLowerCase();
        BukkitCommand bukkitCommand = new BukkitCommand(plugin, name, pluginCommand, pluginCommand.getSubCommands());

        this.register(bukkitCommand);
    }

    @SneakyThrows
    private void register(@NotNull BukkitCommand bukkitCommand) {
        Method method = Bukkit.getServer().getClass().getMethod("getCommandMap");
        method.setAccessible(true);

        CommandMap commandMap = (CommandMap) method.invoke(Bukkit.getServer());
        method.setAccessible(false);

        commandMap.register(plugin.getName().toLowerCase(), bukkitCommand);
        commands.put(bukkitCommand.getName(), bukkitCommand);
    }

    public void unregisterCommands() {
        commands.values().forEach(this::unregister);
    }

    @SneakyThrows
    public void unregister(@NotNull PluginCommand command) {
        this.unregister(command.getName());
    }

    @SneakyThrows
    public void unregister(@NotNull String command) {
        BukkitCommand bukkitCommand = this.commands.get(command);
        if(bukkitCommand == null) {
            return;
        }

        unregister(bukkitCommand);
    }

    @SneakyThrows
    private void unregister(@NotNull BukkitCommand bukkitCommand) {
        final Map<String, Command> commands = getKnownCommands();
        final String name = plugin.getName().toLowerCase();

        for (final String alias : bukkitCommand.getAliases()) {
            commands.remove(name + ":" + alias);
            commands.remove(alias);
        }

        final Method method = Bukkit.getServer().getClass().getMethod("syncCommands");
        method.setAccessible(true);

        method.invoke(Bukkit.getServer());
        method.setAccessible(false);

        String commandName = bukkitCommand.getName();
        commands.remove(commandName);
        this.commands.remove(commandName);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private Map<String, Command> getKnownCommands() {

        final Method method = Bukkit.getServer().getClass().getMethod("getCommandMap");
        method.setAccessible(true);

        final CommandMap commandMap = (CommandMap) method.invoke(Bukkit.getServer());
        method.setAccessible(false);
        final Method method1 = commandMap.getClass().getMethod("getKnownCommands");

        final Map<String, Command> commands = (Map<String, Command>) method1.invoke(commandMap);

        method1.setAccessible(false);
        return commands;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPluginDisable(PluginDisableEvent event) {
        if(!event.getPlugin().equals(plugin)) {
            return;
        }

        unregisterCommands();
    }

    @Getter
    public static class CommandBuilder {

        @NotNull
        private final PluginCommand pluginCommand;

        @NotNull
        private final Map<String, SubCommand> subCommands = new HashMap<>();

        @NotNull
        private final List<String> aliases = new ArrayList<>();

        @Nullable
        private String permission;

        @NotNull
        private String usage = "";

        @NotNull
        private String description = "";

        private long cooldown = 0;

        @NotNull
        private String cooldownMessage = "";

        private CommandBuilder(@NotNull PluginCommand pluginCommand) {
            this.pluginCommand = pluginCommand;
        }

        public CommandBuilder addSubCommand(@NotNull SubCommand subCommand) {
            this.subCommands.put(subCommand.getName().toLowerCase(Locale.ENGLISH), subCommand);
            return this;
        }

        public CommandBuilder addAliases(@NotNull String... aliases) {
            this.aliases.addAll(Arrays.stream(aliases).map(String::toLowerCase).toList());
            return this;
        }

        public CommandBuilder setPermission(@NotNull String permission) {
            this.permission = permission;
            return this;
        }

        public CommandBuilder setUsage(@NotNull String usage) {
            this.usage = usage;
            return this;
        }

        public CommandBuilder setDescription(@NotNull String description) {
            this.description = description;
            return this;
        }

        public CommandBuilder setCooldown(long time, @NotNull TimeUnit timeUnit, @Nullable String message) {
            this.cooldown = timeUnit.toMillis(time);
            this.cooldownMessage = message != null ? message : "";
            return this;
        }

    }

    @Nullable
    private static CommandManager commandManager;

    @NotNull
    public static CommandManager get(@NotNull Plugin plugin) {
        if(CommandManager.commandManager != null) {
            return CommandManager.commandManager;

        }

        CommandManager.commandManager = new CommandManager(plugin);
        return CommandManager.commandManager;
    }

    public static CommandBuilder builderOf(@NotNull PluginCommand pluginCommand) {
        return new CommandBuilder(pluginCommand);
    }

}
