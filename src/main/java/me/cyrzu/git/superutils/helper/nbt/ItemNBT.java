package me.cyrzu.git.superutils.helper.nbt;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemNBT {

    <P, C> void set(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key, @NotNull C value);

    @Nullable
    <P, C> C get(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key);

    @Nullable
    <P, C> C get(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key, @Nullable C def);

    boolean has(@NotNull ItemStack itemStack, @NotNull String key);

    <P, C> boolean has(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key);

}
