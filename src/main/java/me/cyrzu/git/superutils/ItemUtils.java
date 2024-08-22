package me.cyrzu.git.superutils;

import com.google.gson.JsonArray;
import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils.helper.ItemNbt;
import me.cyrzu.git.superutils.helper.JsonReader;
import me.cyrzu.git.superutils.helper.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

@UtilityClass
public class ItemUtils {

    private static final UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String url = "http://textures.minecraft.net/texture/";

    @Nullable
    public static String serialize(@NotNull ItemStack stack) {
        return ItemUtils.serialize(stack, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static String serialize(@Nullable ItemStack item, @Nullable String def) {
        if(item == null) {
            return def;
        }

        String compress = ItemNbt.compress(item);
        return compress != null ? compress : def;
    }

    @NotNull
    public static String serializeArray(@NotNull ItemStack[] stacks) {
        JsonArray list = JsonWriter.createArray(Arrays.asList(stacks), (array, stack) ->
                array.add(ItemUtils.serialize(stack, "")));
        return list.toString();
    }

    @Nullable
    public static ItemStack deserialize(@Nullable String var0) {
        return ItemUtils.deserialize(var0, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static ItemStack deserialize(@Nullable String var0, @Nullable ItemStack def) {
        if(var0 == null) {
            return def;
        }

        ItemStack decompress = ItemNbt.decompress(var0);
        return decompress != null ? decompress : def;
    }


    @NotNull
    public static ItemStack[] deserializeArray(@NotNull String var0) {
        return JsonReader.parseJsonArrayString(var0).stream()
                .map(item -> ItemUtils.deserialize(item, new ItemStack(Material.AIR)))
                .toArray(ItemStack[]::new);
    }

    @NotNull
    public static ItemStack getCustomHead(@NotNull String value) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        try {
            PlayerProfile playerProfile = Bukkit.createPlayerProfile(uuid);
            PlayerTextures textures = playerProfile.getTextures();

            URL url = new URL(value.startsWith(ItemUtils.url) ? value : ItemUtils.url + value);
            textures.setSkin(url);
            playerProfile.setTextures(textures);

            if(head.getItemMeta() instanceof SkullMeta skullMeta) {
                skullMeta.setOwnerProfile(playerProfile);
                head.setItemMeta(skullMeta);
            }

            return head;
        } catch (Exception ignore) {
            return head;
        }
    }

    @NotNull
    public static ItemStack getPlayerHead(@NotNull UUID uuid) {
        return ItemUtils.getPlayerHead(Bukkit.getOfflinePlayer(uuid));
    }

    @NotNull
    public static ItemStack getPlayerHead(@NotNull OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        if(head.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
            head.setItemMeta(skullMeta);
        }

        return head;
    }

    @NotNull
    public static String getDisplayName(@NotNull ItemStack stack) {
        ItemMeta itemMeta = stack.getItemMeta();
        return itemMeta != null && itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : EnumUtils.capitalize(stack.getType());
    }

    @NotNull
    public static List<String> getLore(@NotNull ItemStack stack) {
        ItemMeta itemMeta = stack.getItemMeta();
        return itemMeta != null && itemMeta.getLore() != null ? itemMeta.getLore() : Collections.emptyList();
    }

    public static void setItemMeta(@NotNull ItemStack stack, @NotNull Consumer<@NotNull ItemMeta> function) {
        if(stack.getType().isAir()) {
            return;
        }

        ItemMeta itemMeta = stack.getItemMeta();
        if(itemMeta != null) {
            function.accept(itemMeta);
            stack.setItemMeta(itemMeta);
        }
    }



}