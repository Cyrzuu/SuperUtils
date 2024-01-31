package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.util.UUID;

@UtilityClass
public class ItemUtils {

    private static final Class<?> NBT_TAG_COMPOUND = ReflectionUtils.getClass("net.minecraft.nbt", "NBTTagCompound");
    private static final Class<?> CRAFT_ITEM_STACK = ReflectionUtils.getClass("org.bukkit.craftbukkit." + Version.getCurrent() + ".inventory", "CraftItemStack");
    private static final Class<?> NMS_ITEM_STACK = ReflectionUtils.getClass("net.minecraft.world.item", "ItemStack");
    private static final Class<?> NBT_IO = ReflectionUtils.getClass("net.minecraft.nbt", "NBTCompressedStreamTools");

    private static final Constructor<?> COMPOUND_CONSTRUCTOR = ReflectionUtils.getConstructor(NBT_TAG_COMPOUND);

    private static final Method AS_NMS_COPY = ReflectionUtils.getMethod(CRAFT_ITEM_STACK, "asNMSCopy", ItemStack.class);
    private static final Method SAVE = ReflectionUtils.getMethod(NMS_ITEM_STACK, "b", NBT_TAG_COMPOUND);
    private static final Method WRITE = ReflectionUtils.getMethod(NBT_IO, "a", NBT_TAG_COMPOUND, DataOutput.class);

    private static final Method READ = ReflectionUtils.getMethod(NBT_IO, "a", DataInput.class);
    private static final Method OF = ReflectionUtils.getMethod(NMS_ITEM_STACK, "a", NBT_TAG_COMPOUND);
    private static final Method AS_BUKKIT_COPY = ReflectionUtils.getMethod(CRAFT_ITEM_STACK, "asBukkitCopy", NMS_ITEM_STACK);

    @Nullable
    public static String serialize(@NotNull ItemStack stack) {
        if(AS_NMS_COPY == null || SAVE == null || WRITE == null) {
            throw new RuntimeException();
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutput = new DataOutputStream(outputStream);

            Object compoundTag = ReflectionUtils.invokeConstructor(COMPOUND_CONSTRUCTOR);
            Object nmsStack = AS_NMS_COPY.invoke(null, stack);
            SAVE.invoke(nmsStack, compoundTag);
            WRITE.invoke(null, compoundTag, dataOutput);

            return new BigInteger(1, outputStream.toByteArray()).toString(16);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    public static ItemStack deserialize(@NotNull String var0) {
        if(READ == null || OF == null || AS_BUKKIT_COPY == null) {
            throw new RuntimeException();
        }

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(var0, 16).toByteArray());

            Object tag = READ.invoke(null, new DataInputStream(inputStream));
            Object nmsStack = OF.invoke(null, tag);
            return (ItemStack) AS_BUKKIT_COPY.invoke(null, nmsStack);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static final UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String url = "http://textures.minecraft.net/texture/";

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
        return getPlayerHead(Bukkit.getOfflinePlayer(uuid));
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

}