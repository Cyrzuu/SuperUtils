package me.cyrzu.git.superutils.helper.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BukkitNBT implements NBT {

    @NotNull
    private final static Map<String, NamespacedKey> CACHE = new HashMap<>();

    @NotNull
    protected final Plugin plugin;

    public BukkitNBT(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <P, C> void set(@NotNull PersistentDataHolder dataHolder, @NotNull PersistentDataType<P, C> type, @NotNull String key, @NotNull C value) {
        NamespacedKey namespacedKey = CACHE.get(key);
        if(namespacedKey == null) {
            namespacedKey = new NamespacedKey(plugin, key);
            CACHE.put(key, namespacedKey);
        }

        dataHolder.getPersistentDataContainer().set(namespacedKey, type, value);
    }

    @Override
    public <P, C> @Nullable C get(@NotNull PersistentDataHolder dataHolder, @NotNull PersistentDataType<P, C> type, @NotNull String key) {
        return this.get(dataHolder, type, key, null);
    }

    @Override
    public <P, C> @Nullable C get(@NotNull PersistentDataHolder dataHolder, @NotNull PersistentDataType<P, C> type, @NotNull String key, @Nullable C def) {
        NamespacedKey namespacedKey = CACHE.get(key);
        if(namespacedKey == null) {
            namespacedKey = new NamespacedKey(plugin, key);
            CACHE.put(key, namespacedKey);
        }

        C c = dataHolder.getPersistentDataContainer().get(namespacedKey, type);
        return c != null ? c : def;
    }

    @Override
    public boolean has(@NotNull PersistentDataHolder dataHolder, @NotNull String key) {
        NamespacedKey namespacedKey = CACHE.get(key);
        if(namespacedKey == null) {
            namespacedKey = new NamespacedKey(plugin, key);
            CACHE.put(key, namespacedKey);
        }

        return dataHolder.getPersistentDataContainer().has(namespacedKey);
    }

    @Override
    public <P, C> boolean has(@NotNull PersistentDataHolder dataHolder, @NotNull PersistentDataType<P, C> type, @NotNull String key) {
        NamespacedKey namespacedKey = CACHE.get(key);
        if(namespacedKey == null) {
            namespacedKey = new NamespacedKey(plugin, key);
            CACHE.put(key, namespacedKey);
        }

        return dataHolder.getPersistentDataContainer().has(namespacedKey, type);
    }

}
