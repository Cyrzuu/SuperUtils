package me.cyrzu.git.superutils.helper;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record PlaySound(@NotNull Sound sound, float volume, float pitch) {

    public static PlaySound ERROR_1 = new PlaySound(Sound.BLOCK_ANVIL_LAND, 0.5, 1.85);

    public static PlaySound ERROR_2 = new PlaySound(Sound.BLOCK_CHEST_OPEN, 0.5, 2);

    public static PlaySound ERROR_3 = new PlaySound(Sound.ENTITY_BLAZE_SHOOT, 0.5, 2);

    public static PlaySound ERROR_4 = new PlaySound(Sound.ENTITY_VILLAGER_NO, 0.5, 0.75);

    public static PlaySound ERROR_5 = new PlaySound(Sound.ENTITY_BAT_HURT, 0.5, 0.85);

    public static PlaySound LEVEL_UP = new PlaySound(Sound.ENTITY_PLAYER_LEVELUP, 0, 1.25);

    public static PlaySound XP = new PlaySound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5, 1.25);

    public static PlaySound CLICK = new PlaySound(Sound.UI_BUTTON_CLICK, 0.3, 1.25);

    public static PlaySound CLICK_OFF = new PlaySound(Sound.UI_BUTTON_CLICK, 0.3, 0.75);

    public PlaySound(@NotNull Sound sound, @NotNull Number volume, @NotNull Number pitch) {
        this(sound, volume.floatValue(), pitch.floatValue());
    }

    public void play(@NotNull Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void play(@NotNull Player player,  @NotNull Number volume) {
        player.playSound(player.getLocation(), sound, volume.floatValue(), pitch);
    }

    public void play(@NotNull Player player,  @NotNull Number volume, @NotNull Number pitch) {
        player.playSound(player.getLocation(), sound, volume.floatValue(), pitch.floatValue());
    }

}
