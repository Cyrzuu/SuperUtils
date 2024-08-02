package me.cyrzu.git.superutils.color;

import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils.color.patterns.ColorPattern;
import me.cyrzu.git.superutils.color.patterns.Hexadecimal;
import me.cyrzu.git.superutils.color.patterns.Minecraft;
import me.cyrzu.git.superutils.color.patterns.gradient.Gradient;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class ColorUtils {

    @NotNull
    private final Pattern HEX_PATTERN = Pattern.compile("([a-fA-F0-9]{6}|[a-fA-F0-9]{3})");

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


    @Nullable
    public org.bukkit.Color getBukkitColor(@NotNull String hex) {
        return ColorUtils.getBukkitColor(hex, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public org.bukkit.Color getBukkitColor(@NotNull String hex, @Nullable org.bukkit.Color def) {
        Color color = ColorUtils.getColor(hex);
        return color != null ? org.bukkit.Color.fromRGB(color.getRGB()) : def;
    }

    @Nullable
    public Color getColor(@NotNull String hex) {
        return ColorUtils.getColor(hex, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Color getColor(@NotNull String hex, @Nullable Color def) {
        hex = hex.startsWith("#") ? hex.substring(1) : hex;
        if(!HEX_PATTERN.matcher(hex).find()) {
            return def;
        }

        final int length = hex.length();
        int r = length == 3 ? Integer.valueOf(hex.substring(0, 1).repeat(2), 16) : Integer.valueOf(hex.substring(0, 2), 16);
        int g = length == 3 ? Integer.valueOf(hex.substring(1, 2).repeat(2), 16) : Integer.valueOf(hex.substring(2, 4), 16);
        int b = length == 3 ? Integer.valueOf(hex.substring(2, 3).repeat(2), 16) : Integer.valueOf(hex.substring(4, 6), 16);

        return new Color(r, g, b);
    }

}
