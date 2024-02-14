package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils.color.ColorUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class MessageUtils {

    public static void sendSuccess(@NotNull Player player, String message, @NotNull Object... format) {
        send(player, ColorUtils.SUCCESS_COLOR + message, format);
    }

    public static void sendWarning(@NotNull Player player, String message, @NotNull Object... format) {
        send(player, ColorUtils.ERROR_COLOR + message, format);
    }

    public static void send(@NotNull Player player, String message, @NotNull Object... format) {
        player.sendMessage(parseMessage(message, format));
    }

    public static void sendActionBar(@NotNull Player player, String message, @NotNull Object... format) {
        TextComponent textComponent = new TextComponent(parseMessage(message, format));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
    }

    public static void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subTitle, int fadeStay) {
        sendTitle(player, title, subTitle, fadeStay, 10);
    }

    public static void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subTitle, int fadeStay, int fadeInOut) {
        sendTitle(player, title, subTitle, fadeInOut, fadeStay, fadeInOut);
    }

    public static void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subTitle, int fadeIn, int fadeStay, int fadeOut) {
        player.sendTitle(parseMessage(title), parseMessage(subTitle), fadeIn, fadeStay, fadeOut);
    }

    @NotNull
    public static String parseMessage(String message, @NotNull Object... format) {
        if(format != null && format.length > 0) {
            try {
                message = message.formatted(format);
            } catch (Exception ignore) {
            }
        }

        return ColorUtils.parseText(message);
    }

}
