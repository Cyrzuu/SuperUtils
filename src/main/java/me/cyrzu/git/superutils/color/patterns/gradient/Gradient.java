package me.cyrzu.git.superutils.color.patterns.gradient;

import me.cyrzu.git.superutils.color.patterns.ColorPattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gradient implements ColorPattern {

    @NotNull
    private final Pattern pattern;

    @NotNull
    private final Pattern match;

    @NotNull
    private final TextComponent reset;

    public Gradient() {
        this.pattern = Pattern.compile("<(gd|gradient) (#[A-Fa-f\\d]{6}(?:,#[A-Fa-f\\d]{6})+) (.*?)(?=>)>");
        this.match = Pattern.compile("&\\w|.");
        this.reset = new TextComponent(ChatColor.RESET.toString());
    }

    @Override
    public @NotNull String parseText(@NotNull String text) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String colorGroup = matcher.group(2);
            String content = matcher.group(3);

            matcher.appendReplacement(result, applyColor(content, GradientUtils.parseColors(colorGroup)));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    public String applyColor(String text, List<Color> colors) {
        GradientBuilder gradient = new GradientBuilder(colors, text.length());
        TextComponent textComponent = new TextComponent();
        List<String> split = match.matcher(text).results().map(MatchResult::group).toList();
        boolean[] formatFlags = new boolean[5];

        for (String s : split) {
            if (s.startsWith("&")) {
                char formatCode = s.charAt(1);
                GradientUtils.updateFormatting(formatFlags, formatCode);
            } else {
                TextComponent comp = new TextComponent(s);
                GradientUtils.applyFormatting(comp, formatFlags, gradient.next());
                textComponent.addExtra(comp);
            }
        }

        textComponent.addExtra(reset);
        return textComponent.toLegacyText();
    }

}
