package me.cyrzu.git.superutils;

import org.jetbrains.annotations.NotNull;

public enum Rarity {
    /**
     * White item name.
     */
    COMMON,
    /**
     * Yellow item name.
     */
    UNCOMMON,
    /**
     * Aqua item name.
     */
    RARE,
    /**
     * Light purple item name.
     */
    EPIC;

    public org.bukkit.inventory.ItemRarity getRarity() {
        return switch (this) {
            case COMMON -> org.bukkit.inventory.ItemRarity.COMMON;
            case UNCOMMON -> org.bukkit.inventory.ItemRarity.UNCOMMON;
            case RARE -> org.bukkit.inventory.ItemRarity.RARE;
            case EPIC -> org.bukkit.inventory.ItemRarity.EPIC;
        };
    }

    public static Rarity getRarity(@NotNull org.bukkit.inventory.ItemRarity rarity) {
        return switch (rarity) {
            case COMMON -> COMMON;
            case UNCOMMON -> UNCOMMON;
            case RARE -> RARE;
            case EPIC -> EPIC;
        };
    }

}