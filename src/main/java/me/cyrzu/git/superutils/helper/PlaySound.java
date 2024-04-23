package me.cyrzu.git.superutils.helper;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record PlaySound(@NotNull Sound sound, double volume, double pitch) {

    public static PlaySound ERROR = new PlaySound(Sound.BLOCK_ANVIL_LAND, 0.5, 1.85);

    public static PlaySound LEVEL_UP = new PlaySound(Sound.ENTITY_PLAYER_LEVELUP, 0.5, 1.25);

    public static PlaySound XP = new PlaySound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5, 1.25);

    public static PlaySound CLICK = new PlaySound(Sound.UI_BUTTON_CLICK, 0.3, 1.25);

    public static PlaySound CLICK_OFF = new PlaySound(Sound.UI_BUTTON_CLICK, 0.3, 0.75);

    public void play(@NotNull Player player) {
        player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
    }

}
