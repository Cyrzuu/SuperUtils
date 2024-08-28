package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils.color.ColorUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

@UtilityClass
public class MessageUtils {

    @Nullable
    private Logger LOGGER;

    @Nullable
    private String WARNING, INFO, SUCCESS;

    public void registerLogger(@NotNull Plugin plugin) {
        MessageUtils.LOGGER = plugin.getLogger();
    }

    public void registerPlayerMessages(@Nullable String warning, @Nullable String info, @Nullable String success) {
        MessageUtils.WARNING = warning != null ? ColorUtils.parseText(warning) : null;
        MessageUtils.INFO = info != null ? ColorUtils.parseText(info) : null;
        MessageUtils.SUCCESS = success != null ? ColorUtils.parseText(success) : null;
    }

    public void sendSuccess(@NotNull CommandSender sender, String message, @NotNull Object... format) {
        if(sender instanceof ConsoleCommandSender && LOGGER != null) {
            LOGGER.info(MessageUtils.parseStripMessage(message, format));
            return;
        }

        if(sender instanceof Player player) {
            MessageUtils.sendSuccess(player, message, format);
            return;
        }

        MessageUtils.send(sender, ColorUtils.SUCCESS_COLOR + message, format);
    }

    public void sendWarning(@NotNull CommandSender sender, String message, @NotNull Object... format) {
        if(sender instanceof ConsoleCommandSender && LOGGER != null) {
            LOGGER.severe(MessageUtils.parseStripMessage(message, format));
            return;
        }

        if(sender instanceof Player player) {
            MessageUtils.sendWarning(player, message, format);
            return;
        }

        MessageUtils.send(sender, ColorUtils.ERROR_COLOR + message, format);
    }

    public void sendInfo(@NotNull CommandSender sender, String message, @NotNull Object... format) {
        if(sender instanceof ConsoleCommandSender && LOGGER != null) {
            LOGGER.warning(MessageUtils.parseStripMessage(message, format));
            return;
        }

        if(sender instanceof Player player) {
            MessageUtils.sendInfo(player, message, format);
            return;
        }

        MessageUtils.send(sender, ColorUtils.ERROR_COLOR + message, format);
    }



    public void sendSuccess(@NotNull Player player, String message, Object @NotNull ... args) {
        if(args.length == 0) {
            player.sendMessage((SUCCESS == null ? ColorUtils.SUCCESS_COLOR : SUCCESS) + message);
            return;
        }

        player.sendMessage((SUCCESS == null ? ColorUtils.SUCCESS_COLOR : SUCCESS) + MessageUtils.format(message, args));
    }

    public void sendWarning(@NotNull Player player, String message, Object @NotNull ... args) {
        if(args.length == 0) {
            player.sendMessage((WARNING == null ? ColorUtils.ERROR_COLOR : WARNING) + message);
            return;
        }

        player.sendMessage((WARNING == null ? ColorUtils.ERROR_COLOR : WARNING) + MessageUtils.format(message, args));
    }

    public void sendInfo(@NotNull Player player, String message, Object @NotNull ... args) {
        if(args.length == 0) {
            player.sendMessage((INFO == null ? ColorUtils.INFO_COLOR : INFO) + message);
            return;
        }

        player.sendMessage((INFO == null ? ColorUtils.INFO_COLOR : INFO) + MessageUtils.format(message, args));
    }






    public void send(@NotNull CommandSender sender, String message, Object @NotNull ... format) {
        if(sender instanceof ConsoleCommandSender && LOGGER != null) {
            LOGGER.info(MessageUtils.parseStripMessage(message, format));
            return;
        }

        sender.sendMessage(MessageUtils.parseMessage(message, format));
    }

    public void send(@NotNull Player player, String message, Object @NotNull ... format) {
        player.sendMessage(MessageUtils.parseMessage(message, format));
    }



    public void sendActionBar(@NotNull Player player, String message, Object @NotNull ... format) {
        TextComponent textComponent = new TextComponent(MessageUtils.parseMessage(message, format));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
    }

    public void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subTitle, int fadeStay) {
        MessageUtils.sendTitle(player, title, subTitle, fadeStay, 10);
    }

    public void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subTitle, int fadeStay, int fadeInOut) {
        MessageUtils.sendTitle(player, title, subTitle, fadeInOut, fadeStay, fadeInOut);
    }

    public void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subTitle, int fadeIn, int fadeStay, int fadeOut) {
        player.sendTitle(MessageUtils.parseMessage(title), MessageUtils.parseMessage(subTitle), fadeIn, fadeStay, fadeOut);
    }

    @NotNull
    public String parseStripMessage(String message, Object @NotNull ... format) {
        if(format != null && format.length > 0) {
            try {
                message = message.formatted(format);
            } catch (Exception ignore) {
            }
        }

        return ColorUtils.stripColor(message);
    }

    @NotNull
    public String parseMessage(String message, Object @NotNull ... args) {
        if(args.length > 0) {
            message = MessageUtils.format(message, args);
        }

        return ColorUtils.parseText(message);
    }

    @NotNull
    public String format(@NotNull String message, @NotNull Object... args) {
        try {
            return String.format(message, args);
        } catch (Exception ignored) {}

        return message;
    }

}
