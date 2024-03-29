package me.cyrzu.git.superutils;

import lombok.Getter;
import me.cyrzu.git.superutils.color.ColorUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StackBuilder {

    @NotNull
    private static final ItemMeta DEFAULT_META;

    static {
        ItemStack stack = new ItemStack(Material.STONE);
        ItemMeta meta = stack.getItemMeta();
        if(meta == null) {
            throw new RuntimeException("Meta is null");
        }

        DEFAULT_META = meta;
    }

    @Getter
    @NotNull
    private final Material material;

    @Getter
    @NotNull
    private final ItemMeta itemMeta;

    @Nullable
    private String displayName;

    @NotNull
    private final List<String> lore = new ArrayList<>();

    @Getter
    private boolean unbreakable = false;

    @Getter
    private int amount;

    @Getter
    private int customModelData = -1;

    @Getter
    private int damage = -1;

    private final Map<Enchantment, Integer> enchantments;

    private final Set<ItemFlag> flags;

    public StackBuilder(@NotNull Material material) {
        this(new ItemStack(material));
    }

    public StackBuilder(@NotNull ItemStack stack) {
        this(stack, stack.getItemMeta());
    }

    public StackBuilder(@NotNull ItemStack stack, @Nullable ItemMeta itemMeta) {
        if(itemMeta == null) {
            itemMeta = DEFAULT_META;
        }

        this.material = stack.getType();
        this.itemMeta = itemMeta;

        this.enchantments = new LinkedHashMap<>(stack.getEnchantments());
        this.flags = new HashSet<>(this.itemMeta.getItemFlags());
        this.amount = stack.getAmount();

        List<String> lore = itemMeta.getLore();
        if(lore != null) {
            this.lore.addAll(lore);
        }

        if(itemMeta instanceof Damageable damageable) {
            this.unbreakable = damageable.isUnbreakable();
            this.damage = damageable.getDamage();
        }

        if(itemMeta.hasCustomModelData()) {
            this.customModelData = itemMeta.getCustomModelData();
        }
    }

    public StackBuilder setName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public StackBuilder addLore(@NotNull String... lore) {
        return addLore(Arrays.asList(lore));
    }

    public StackBuilder addLore(@NotNull List<String> lore) {
        this.lore.addAll(lore);
        return this;
    }

    public StackBuilder clearLore() {
        this.lore.clear();
        return this;
    }

    public StackBuilder setLore(@NotNull List<String> lore) {
        this.lore.clear();
        this.lore.addAll(lore);
        return this;
    }

    public StackBuilder setLore(@NotNull String... lore) {
        this.lore.clear();
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }

    public StackBuilder setAmount(int amount) {
        this.amount = Math.max(1, amount);
        return this;
    }

    public StackBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public StackBuilder setCustomModelData(@Nullable Integer model) {
        this.customModelData = model == null || model < 0 ? -1 : model;
        return this;
    }

    public StackBuilder setDamage(@Nullable Integer damage) {
        this.damage = damage == null || damage < 0 ? -1 : damage;
        return this;
    }

    public StackBuilder addEnchantment(@NotNull String enchantment, int level) {
        Enchantment byName = Enchantment.getByKey(NamespacedKey.minecraft(enchantment.toLowerCase()));
        if(byName != null) {
            addEnchantment(byName, level);
        }

        return this;
    }

    public StackBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, Math.max(0, level));
        return this;
    }

    public StackBuilder clearEnchantments() {
        this.enchantments.clear();
        return this;
    }

    public StackBuilder setEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        this.enchantments.clear();
        this.enchantments.putAll(enchantments);
        return this;
    }

    public StackBuilder addFlag(@NotNull ItemFlag flag) {
        this.flags.add(flag);
        return this;
    }

    public StackBuilder clearFlags() {
        this.enchantments.clear();
        return this;
    }

    public StackBuilder setFlags(@NotNull String... flags) {
        if(Arrays.stream(flags).anyMatch(f -> f.equalsIgnoreCase("all"))) {
            setFlags(Arrays.asList(ItemFlag.values()));
            return this;
        }

        try {
            List<ItemFlag> list = Arrays.stream(flags)
                    .map(v -> ItemFlag.valueOf(v.toUpperCase()))
                    .toList();

            setFlags(list);
        } catch (Exception ignored) { }

        return this;
    }

    public StackBuilder setFlags(@NotNull Collection<ItemFlag> flags) {
        this.flags.clear();
        this.flags.addAll(flags);
        return this;
    }

    public <T, Z> StackBuilder addPersistentData(@NotNull JavaPlugin plugin, @NotNull String key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), type, value);
        return this;
    }

    @NotNull
    public ItemStack build() {
        if(material.isAir()) {
            return new ItemStack(Material.AIR);
        }

        itemMeta.setDisplayName(displayName == null ?
                null :
                ColorUtils.parseText(displayName));

        List<String> lore = this.lore.stream().map(ColorUtils::parseText).toList();
        itemMeta.setLore(lore);

        itemMeta.setCustomModelData(customModelData >= 0 ? customModelData : null);

        if(itemMeta instanceof Damageable damageable) {
            if(damage >= 0) {
                damageable.setDamage(damage);
            }

            damageable.setUnbreakable(unbreakable);
        }

        itemMeta.addItemFlags(flags.toArray(ItemFlag[]::new));

        ItemStack stack = new ItemStack(material);
        stack.setAmount(Math.min(amount, 64));
        stack.setItemMeta(itemMeta);
        stack.addUnsafeEnchantments(enchantments);

        return stack;
    }

    @NotNull
    public String getDisplayName() {
        return this.displayName != null ? this.displayName : material.name().replace("_", " ").toLowerCase();
    }

    public boolean hasDisplayName() {
        return this.displayName != null;
    }

    @NotNull
    public List<String> getLore() {
        return Collections.unmodifiableList(this.lore);
    }

    @NotNull
    public Map<Enchantment, Integer> getEnchantments() {
        return Collections.unmodifiableMap(this.enchantments);
    }

    @NotNull
    public Set<ItemFlag> getFlags() {
        return Collections.unmodifiableSet(this.flags);
    }

    public StackBuilder clone() {
        return new StackBuilder(build());
    }

    @NotNull
    public static StackBuilder parseString(@NotNull String text) {
        String[] s = text.split(" ");
        Material material = Material.matchMaterial(s[0].toUpperCase());
        if(s.length == 1) {
            return new StackBuilder(material == null || material.isAir() ? Material.STONE : material);
        }

        StackBuilder builder = new StackBuilder(material == null || material.isAir() ? Material.STONE : material);
        for (int i = 1; i < s.length; i++) {
            String[] split = s[i].split(":");

            if(split.length == 1) {
                continue;
            }

            String key = split[0].toLowerCase();
            String value = split[1];

            switch (key) {
                case "name","displayname" -> builder.setName(value.replace("_", " "));
                case "lore" -> builder.addLore(Arrays.stream(value.split(",")).map(v -> v.replace("_", " ")).toList());
                case "custommodeldata","cmd","model" -> builder.setCustomModelData(NumberUtils.parseInteger(value, 1));
                case "amount" -> builder.setAmount(NumberUtils.parseInteger(value, 1));
                case "damage","dmg" -> builder.setDamage(NumberUtils.parseInteger(value, 1));
                case "unbreakable","unbr","unb" -> builder.setUnbreakable(Boolean.parseBoolean(value));
                case "flag","flags" -> builder.setFlags(value.split(","));
                case "enchant","ench","enchantment" -> {
                    String[] ench = value.split(";");
                    if(ench.length == 1) {
                        continue;
                    }

                    builder.addEnchantment(ench[0], NumberUtils.parseInteger(ench[1]));
                }
            }
        }

        return builder;
    }

}
