package me.cyrzu.git.superutils.helper.nbt;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemBukkitNBT extends BukkitNBT implements ItemNBT {

    public ItemBukkitNBT(@NotNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public <P, C> void set(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key, @NotNull C value) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) {
            return;
        }

        this.set(itemMeta, type, key, value);
        itemStack.setItemMeta(itemMeta);
    }

    @Nullable
    @Override
    public <P, C> C get(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key) {
        return this.get(itemStack, type, key, null);
    }

    @Nullable
    @Override
    public <P, C> C get(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key, @Nullable C def) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null ? this.get(itemMeta, type, key, def) : def;
    }

    @Override
    public boolean has(@NotNull ItemStack itemStack, @NotNull String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && this.has(itemMeta, key);
    }

    @Override
    public <P, C> boolean has(@NotNull ItemStack itemStack, @NotNull PersistentDataType<P, C> type, @NotNull String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && this.has(itemStack, type, key);
    }

}
