package me.cyrzu.git.superutils.color.patterns.gradient;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class GradientUtils {

    @NotNull
    public static List<Color> parseColors(@NotNull String text) {
        String[] split = text.replace("#", "").split(",");
        return Arrays.stream(split)
                .map(GradientUtils::parseColor)
                .filter(Objects::nonNull)
                .toList();
    }

    @Nullable
    public static Color parseColor(@NotNull String text) {
        try {
            return new Color(Integer.parseInt(text, 16));
        } catch (Exception ignore) {
            return null;
        }
    }

    public static void updateFormatting(boolean[] formatFlags, char formatCode) {
        if (formatCode == 'r') {
            Arrays.fill(formatFlags, false);
        } else {
            int index = "lmnok".indexOf(formatCode);
            if (index != -1) {
                formatFlags[index] = true;
            }
        }
    }

    public static void applyFormatting(TextComponent component, boolean[] formatFlags, Color color) {
        component.setBold(formatFlags[0]);
        component.setStrikethrough(formatFlags[1]);
        component.setUnderlined(formatFlags[2]);
        component.setObfuscated(formatFlags[3]);
        component.setItalic(formatFlags[4]);
        component.setColor(ChatColor.of(color));
    }

}
