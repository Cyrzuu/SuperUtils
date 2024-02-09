package me.cyrzu.git.superutils.color.patterns;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hexadecimal implements ColorPattern {

    private final Pattern pattern;

    public Hexadecimal() {
        this.pattern = Pattern.compile("&#[a-fA-F\\d]{6}");
    }

    @Override
    public @NotNull String parseText(@NotNull String text) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group();
            text = text.replace(group, ChatColor.of(group.substring(1)).toString());
        }

        return text;
    }

}
