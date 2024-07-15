package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils.color.ColorUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

@UtilityClass
public class MessageUtils {

    @Nullable
    private Logger logger;

    public void registerLogger(@NotNull Plugin plugin) {
        MessageUtils.logger = plugin.getLogger();
    }

    public void sendSuccess(@NotNull CommandSender sender, String message, @NotNull Object... format) {
        if(sender instanceof ConsoleCommandSender && logger != null) {
            logger.info(MessageUtils.parseStripMessage(message, format));
            return;
        }

        MessageUtils.send(sender, ColorUtils.SUCCESS_COLOR + message, format);
    }

    public void sendWarning(@NotNull CommandSender sender, String message, @NotNull Object... format) {
        if(sender instanceof ConsoleCommandSender && logger != null) {
            logger.severe(MessageUtils.parseStripMessage(message, format));
            return;
        }

        MessageUtils.send(sender, ColorUtils.ERROR_COLOR + message, format);
    }

    public void send(@NotNull CommandSender sender, String message, @NotNull Object... format) {
        if(sender instanceof ConsoleCommandSender && logger != null) {
            logger.info(MessageUtils.parseStripMessage(message, format));
            return;
        }

        sender.sendMessage(MessageUtils.parseMessage(message, format));
    }

    public void sendSuccess(@NotNull Player player, String message, @NotNull Object... format) {
        MessageUtils.send(player, ColorUtils.SUCCESS_COLOR + message, format);
    }

    public void sendWarning(@NotNull Player player, String message, @NotNull Object... format) {
        MessageUtils.send(player, ColorUtils.ERROR_COLOR + message, format);
    }

    public void send(@NotNull Player player, String message, @NotNull Object... format) {
        player.sendMessage(MessageUtils.parseMessage(message, format));
    }

    public void sendActionBar(@NotNull Player player, String message, @NotNull Object... format) {
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
        player.sendTitle(parseMessage(title), parseMessage(subTitle), fadeIn, fadeStay, fadeOut);
    }

    @NotNull
    public String parseStripMessage(String message, @NotNull Object... format) {
        if(format != null && format.length > 0) {
            try {
                message = message.formatted(format);
            } catch (Exception ignore) {
            }
        }

        return ColorUtils.stripColor(message);
    }

    @NotNull
    public String parseMessage(String message, @NotNull Object... format) {
        if(format != null && format.length > 0) {
            try {
                message = message.formatted(format);
            } catch (Exception ignore) {
            }
        }

        return ColorUtils.parseText(message);
    }

}
