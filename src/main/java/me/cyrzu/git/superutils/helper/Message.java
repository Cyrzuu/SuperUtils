package me.cyrzu.git.superutils.helper;

import lombok.AllArgsConstructor;
import me.cyrzu.git.superutils.color.ColorUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class Message {

    @NotNull
    private final String[] messages;

    @Nullable
    private final String actionbar;

    @Nullable
    private final Title title;

    @Nullable
    private final PlaySound sound;

    private Message() {
        this.messages = new String[0];
        this.actionbar = null;
        this.title = null;
        this.sound = null;
    }

    public Message(@NotNull JsonReader reader) {
        String message = ColorUtils.parseText(reader.getString("message", ""));
        String[] messages = reader.getListString("messages").stream()
                .map(ColorUtils::parseText).toArray(String[]::new);
        this.messages = message.isEmpty() ? messages : new String[]{message};

        String actionbar = reader.getString("actionbar");
        this.actionbar = actionbar == null ? null : ColorUtils.parseText(actionbar);

        JsonReader title = reader.getReader("title");
        this.title = title == null ? null : new Title(title);

        JsonReader sound = reader.getReader("sound");
        this.sound = sound == null ? null : new PlaySound(reader.getEnum("name", Sound.class, Sound.UI_BUTTON_CLICK),
                reader.getDouble("volume", 1D),
                reader.getDouble("pitch", 1D));
    }

    public void send(@NotNull Player player) {
        if(messages.length != 0) {
            player.sendMessage(messages);
        }

        if(actionbar != null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
        }

        if(title != null) {
            title.send(player);
        }

        if(sound != null) {
            sound.play(player);
        }
    }

    public void send(@NotNull Player player, @NotNull ReplaceBuilder replacer, @NotNull Object... objects) {
        for (String message : messages) {
            player.sendMessage(replacer.replaceMessage(message, objects));
        }

        if(actionbar != null) {
            String actionbar = replacer.replaceMessage(this.actionbar, objects);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
        }

        if(title != null) {
            title.send(player, replacer, objects);
        }

        if(sound != null) {
            sound.play(player);
        }
    }

    private static class Title {

        @NotNull
        private final String title;

        @NotNull
        private final String subtitle;

        int fadeIn, stay, fadeOut;

        public Title(@NotNull JsonReader reader) {
            this.title = ColorUtils.parseText(reader.getString("title", ""));
            this.subtitle = ColorUtils.parseText(reader.getString("subtitle", ""));
            this.fadeIn = reader.getInt("fadeIn", 10);
            this.stay = reader.getInt("stay", 20);
            this.fadeOut = reader.getInt("fadeOut", 10);
        }

        public void send(@NotNull Player player, @NotNull ReplaceBuilder replacer, @NotNull Object... objects) {
            player.sendTitle(replacer.replaceMessage(title, objects), replacer.replaceMessage(subtitle, objects), fadeIn, stay, fadeOut);
        }

        public void send(@NotNull Player player) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }

    }

    @NotNull
    public final static Message EMPTY_MESSAGE = new Message();

    @NotNull
    public static Message getEmptyMessage() {
        return Message.EMPTY_MESSAGE;
    }

    @NotNull
    public static Message of(@NotNull String message) {
        return new Message(new String[]{message}, null, null, null);
    }

}
