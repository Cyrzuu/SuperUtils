package me.cyrzu.git.superutils.helper;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BukkitNBT {

    @NotNull
    private final static Map<String, NamespacedKey> CACHE = new HashMap<>();

    @NotNull
    private final Plugin plugin;

    public BukkitNBT(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public <P, C> void set(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key, @NotNull C value) {
        NamespacedKey namespacedKey = CACHE.get(key);
        if(namespacedKey == null) {
            namespacedKey = new NamespacedKey(plugin, key);
            CACHE.put(key, namespacedKey);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) {
            return;
        }

        itemMeta.getPersistentDataContainer().set(namespacedKey, type, value);
        itemStack.setItemMeta(itemMeta);
    }

    @Nullable
    public <P, C> C get(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key) {
        return this.get(itemStack, type, key, null);
    }

    @Nullable
    @Contract("_, _, _, !null -> !null")
    public <P, C> C get(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key, @Nullable C def) {
        NamespacedKey namespacedKey = CACHE.get(key);
        if(namespacedKey == null) {
            namespacedKey = new NamespacedKey(plugin, key);
            CACHE.put(key, namespacedKey);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) {
            return null;
        }

        C c = itemMeta.getPersistentDataContainer().get(namespacedKey, type);
        return c != null ? c : def;
    }

    public boolean has(@NotNull ItemStack itemStack, @NotNull String key) {
        NamespacedKey namespacedKey = CACHE.get(key);
        if(namespacedKey == null) {
            namespacedKey = new NamespacedKey(plugin, key);
            CACHE.put(key, namespacedKey);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) {
            return false;
        }

        return itemMeta.getPersistentDataContainer().has(namespacedKey);
    }

    public <P, C> boolean has(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key) {
        NamespacedKey namespacedKey = CACHE.get(key);
        if(namespacedKey == null) {
            namespacedKey = new NamespacedKey(plugin, key);
            CACHE.put(key, namespacedKey);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) {
            return false;
        }

        return itemMeta.getPersistentDataContainer().has(namespacedKey, type);
    }

}
