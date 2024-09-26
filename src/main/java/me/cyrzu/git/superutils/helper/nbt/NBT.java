package me.cyrzu.git.superutils.helper.nbt;

import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NBT {

    <P, C> void set(@NotNull PersistentDataHolder dataHolder, @NotNull PersistentDataType<P, C> type, @NotNull String key, @NotNull C value);

    @Nullable
    <P, C> C get(@NotNull PersistentDataHolder dataHolder, @NotNull PersistentDataType<P, C> type, @NotNull String key);

    @Nullable
    <P, C> C get(@NotNull PersistentDataHolder dataHolder, @NotNull PersistentDataType<P, C> type, @NotNull String key, @Nullable C def);

    boolean has(@NotNull PersistentDataHolder dataHolder, @NotNull String key);

    <P, C> boolean has(@NotNull PersistentDataHolder dataHolder, @NotNull PersistentDataType<P, C> type, @NotNull String key);

}
