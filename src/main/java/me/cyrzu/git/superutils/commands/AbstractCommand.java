package me.cyrzu.git.superutils.commands;

import lombok.SneakyThrows;
import me.cyrzu.git.superutils.color.ColorUtils;
import me.cyrzu.git.superutils.commands.annotations.Aliases;
import me.cyrzu.git.superutils.commands.annotations.Permission;
import me.cyrzu.git.superutils.commands.annotations.PermissionMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractCommand extends Command {

    @Nullable
    private static Plugin plugin;

    private final static Map<String, AbstractCommand> commands = new HashMap<>();

    public static Collection<AbstractCommand> getCommands() {
        return commands.values();
    }

    @NotNull
    private final Map<String, SubCommand> subCommands;

    public AbstractCommand(@NotNull String command) {
        super(command);
        this.subCommands = new HashMap<>();

        if(this.getClass().isAnnotationPresent(Permission.class)) {
            Permission value = this.getClass().getAnnotation(Permission.class);
            setPermission(value.value());
        }

        if(this.getClass().isAnnotationPresent(PermissionMessage.class)) {
            PermissionMessage value = this.getClass().getAnnotation(PermissionMessage.class);
            setPermission(value.value());
        }

        if(this.getClass().isAnnotationPresent(Aliases.class)) {
            Aliases value = this.getClass().getAnnotation(Aliases.class);
            setAliases(Arrays.asList(value.value()));
        }
    }

    public final AbstractCommand registerSubCommands(@NotNull SubCommand... subCommands) {
        for (SubCommand subCommand : subCommands) {
            this.subCommands.put(subCommand.getName().toLowerCase(), subCommand);
        }

        return this;
    }

    public abstract void execute(@NotNull Player player, @NotNull CommandContext context);

    public void execute(@NotNull ConsoleCommandSender console, @NotNull CommandContext context) { }

    public void execute(@NotNull CommandSender sender, @NotNull CommandContext context) { }

    public List<String> tabComplete(@NotNull Player player, @NotNull CommandContext context) {
        return new ArrayList<>();
    }

    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull CommandContext context) {
        return new ArrayList<>();
    }

    public void invalidSubcommand(@NotNull Player player, @NotNull String arg) { }

    public void invalidSubcommand(@NotNull CommandSender sender, @NotNull String arg) { }

    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(subCommands.isEmpty() || args.length == 0) {
            runCommand(sender, args);
            return true;
        }

        String sub = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(sub);
        if(subCommand == null) {
            if(sender instanceof Player player) {
                invalidSubcommand(player, sub);
            }

            invalidSubcommand(sender, sub);
            return true;
        }

        subCommand.run(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @NotNull
    @Override
    public final List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        if(subCommands.isEmpty()) {
            return runTabComplete(sender, args);
        }

        if(args.length <= 1)
            return subCommands.values().stream()
                    .map(SubCommand::getName)
                    .filter(arg -> args[0].isEmpty() || arg.toLowerCase().startsWith(args[0]))
                    .toList();

        String sub = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(sub);
        if(subCommand != null) {
            CommandContext context = new CommandContext(Arrays.copyOfRange(args, 1, args.length));

            List<String> tabs = sender instanceof Player player ?
                    subCommand.tabComplete(player, context) :
                    subCommand.tabComplete(sender, context);

            String arg = context.get(context.size() - 1, "");
            return tabs.stream().filter(tab -> arg.isEmpty() || tab.toLowerCase().startsWith(arg.toLowerCase())).toList();
        }

        return new ArrayList<>();
    }

    @NotNull
    @Override
    public final List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return tabComplete(sender, alias, args, null);
    }

    public final void runCommand(@NotNull CommandSender sender, @NotNull String... args) {
        CommandContext context = new CommandContext(args);

        if(sender instanceof Player player) {
            execute(player, context);
        } else if(sender instanceof ConsoleCommandSender console) {
            execute(console, context);
        }

        execute(sender, context);
    }

    public final List<String> runTabComplete(@NotNull CommandSender sender, @NotNull String... args) {
        CommandContext context = new CommandContext(args);

        List<String> tabs =  sender instanceof Player player ?
                tabComplete(player, context) :
                tabComplete(sender, context);

        String arg = context.get(context.size() - 1, "");
        return tabs.stream().filter(tab -> arg.isEmpty() || tab.toLowerCase().startsWith(arg.toLowerCase())).toList();
    }

    @SneakyThrows
    public final void registerCommand(@NotNull Plugin plugin) {
        if(AbstractCommand.plugin == null) {
            AbstractCommand.plugin = plugin;
        }

        final Method method = Bukkit.getServer().getClass().getMethod("getCommandMap");
        method.setAccessible(true);

        final CommandMap commandMap = (CommandMap) method.invoke(Bukkit.getServer());
        method.setAccessible(false);

        commandMap.register(plugin.getName().toLowerCase(), this);

        commands.put(getName(), this);
    }

    @SneakyThrows
    public final void unregisterCommand() {
        if(plugin == null) {
            return;
        }

        final Map<String, Command> commands = getKnownCommands();
        final String name = plugin.getName().toLowerCase();

        for (final String alias : getAliases()) {
            commands.remove(name + ":" + alias);
            commands.remove(alias);
        }

        final Method method = Bukkit.getServer().getClass().getMethod("syncCommands");
        method.setAccessible(true);

        method.invoke(Bukkit.getServer());
        method.setAccessible(false);

        commands.remove(getName());
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



    @NotNull
    @Override
    public final String getName() {
        return super.getName();
    }

    @Override
    public final boolean setName(@NotNull String name) {
        return super.setName(name);
    }

    @Nullable
    @Override
    public final String getPermission() {
        return super.getPermission();
    }

    @Override
    public final void setPermission(@Nullable String permission) {
        super.setPermission(permission);
    }

    @Override
    public final boolean testPermission(@NotNull CommandSender target) {
        return super.testPermission(target);
    }

    @Override
    public final boolean testPermissionSilent(@NotNull CommandSender target) {
        return super.testPermissionSilent(target);
    }

    @NotNull
    @Override
    public final String getLabel() {
        return super.getLabel();
    }

    @Override
    public final boolean setLabel(@NotNull String name) {
        return super.setLabel(name);
    }

    @Override
    public final boolean register(@NotNull CommandMap commandMap) {
        return super.register(commandMap);
    }

    @Override
    public final boolean unregister(@NotNull CommandMap commandMap) {
        return super.unregister(commandMap);
    }

    @Override
    public final boolean isRegistered() {
        return super.isRegistered();
    }

    @NotNull
    @Override
    public final List<String> getAliases() {
        return super.getAliases();
    }

    @Nullable
    @Override
    public final String getPermissionMessage() {
        String message = super.getPermissionMessage();
        return message != null ? ColorUtils.parseText(message) : null;
    }

    @NotNull
    @Override
    public final String getDescription() {
        return super.getDescription();
    }

    @NotNull
    @Override
    public final String getUsage() {
        return super.getUsage();
    }

    @NotNull
    @Override
    public final Command setAliases(@NotNull List<String> aliases) {
        return super.setAliases(aliases);
    }

    @NotNull
    @Override
    public final Command setDescription(@NotNull String description) {
        return super.setDescription(description);
    }

    @NotNull
    @Override
    public final Command setPermissionMessage(@Nullable String permissionMessage) {
        return super.setPermissionMessage(permissionMessage);
    }

    @NotNull
    @Override
    public final Command setUsage(@NotNull String usage) {
        return super.setUsage(usage);
    }


    @Override
    public final String toString() {
        return super.toString();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
    }
}