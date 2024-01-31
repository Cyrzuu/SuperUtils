package me.cyrzu.git.superutils.color;

import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils.color.patterns.ColorPattern;
import me.cyrzu.git.superutils.color.patterns.Hexadecimal;
import me.cyrzu.git.superutils.color.patterns.Minecraft;
import me.cyrzu.git.superutils.color.patterns.gradient.Gradient;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

@UtilityClass
public class ColorUtils {

    @NotNull
    public static ChatColor ERROR_COLOR = ChatColor.of(new Color(204, 0, 0));

    @NotNull
    public static ChatColor SUCCESS_COLOR = ChatColor.of(new Color(0, 204, 0));

    @NotNull
    private final static List<ColorPattern> patterns = List.of(
            new Gradient(),
            new Minecraft(),
            new Hexadecimal()
    );

    @NotNull
    public static String parseText(@NotNull String text) {
        for (ColorPattern pattern : patterns) {
            text = pattern.parseText(text);
        }

        return text;
    }

    @NotNull
    public static String stripColor(@NotNull String text) {
        return ChatColor.stripColor(parseText(text));
    }

}
