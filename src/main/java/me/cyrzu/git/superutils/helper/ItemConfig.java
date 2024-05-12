package me.cyrzu.git.superutils.helper;

import lombok.Getter;
import me.cyrzu.git.superutils.EnumUtils;
import me.cyrzu.git.superutils.ItemUtils;
import me.cyrzu.git.superutils.StackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class ItemConfig {

    @NotNull
    private final ItemStack item;

    public ItemConfig(@NotNull JsonReader reader) {
        String headTexture = reader.getString("head_texture");
        StackBuilder builder = headTexture != null ? new StackBuilder(ItemUtils.getCustomHead(headTexture)) :
            new StackBuilder(EnumUtils.getEnum(reader.getString("type", " ").replace(" ", "_"), Material.class, Material.STONE));

        builder.setName(reader.getString("name"));
        builder.setLore(reader.getListString("lore"));
        builder.setCustomModelData(reader.getInt("custommodeldata", -1));
        builder.setAmount(reader.getInt("amount", 1));
        builder.setUnbreakable(reader.getBoolean("unbreakable", false));
        builder.setFlags(reader.getListString("flags").toArray(String[]::new));

        JsonReader enchants = reader.getReader("enchants");
        if(enchants != null) {
            for (String enchant : enchants.keySet()) {
                builder.addEnchantment(enchant, enchants.getInt(enchant, 1));
            }
        }

        this.item = builder.build();
    }

}
