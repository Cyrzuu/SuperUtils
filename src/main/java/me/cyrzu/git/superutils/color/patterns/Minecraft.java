package me.cyrzu.git.superutils.color.patterns;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

public class Minecraft implements ColorPattern {

    @Override
    public @NotNull String parseText(@NotNull String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
