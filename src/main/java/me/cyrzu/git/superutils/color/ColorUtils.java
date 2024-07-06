package me.cyrzu.git.superutils.color;

import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils.color.patterns.ColorPattern;
import me.cyrzu.git.superutils.color.patterns.Hexadecimal;
import me.cyrzu.git.superutils.color.patterns.Minecraft;
import me.cyrzu.git.superutils.color.patterns.gradient.Gradient;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class ColorUtils {

    @NotNull
    private final Gradient GRADIENT = new Gradient();

    @NotNull
    public static ChatColor ERROR_COLOR = ChatColor.of(new Color(204, 0, 0));

    @NotNull
    public static ChatColor SUCCESS_COLOR = ChatColor.of(new Color(0, 204, 0));

    @NotNull
    private final static List<ColorPattern> patterns = List.of(
            GRADIENT,
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

    public String parseGradient(@NotNull String text, @NotNull Color... gradients) {
        return GRADIENT.applyColor(text, Arrays.asList(gradients));
    }

    public String parseGradient(@NotNull String text, @NotNull Collection<Color> gradients) {
        return GRADIENT.applyColor(text, List.copyOf(gradients));
    }

    @NotNull
    public static String stripColor(@NotNull String text) {
        return ChatColor.stripColor(parseText(text));
    }

}
