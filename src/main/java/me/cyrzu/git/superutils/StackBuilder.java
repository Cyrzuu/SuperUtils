package me.cyrzu.git.superutils;

import lombok.Getter;
import me.cyrzu.git.superutils.color.ColorUtils;
import me.cyrzu.git.superutils.helper.nbt.BukkitNBT;
import me.cyrzu.git.superutils.helper.Pair;
import me.cyrzu.git.superutils.helper.ReplaceBuilder;
import me.cyrzu.git.superutils.helper.nbt.ItemBukkitNBT;
import me.cyrzu.git.superutils.helper.nbt.ItemMinecraftNBT;
import me.cyrzu.git.superutils.helper.nbt.ItemNBT;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;
import java.util.function.Function;

public class StackBuilder implements Cloneable {

    @NotNull
    private static ItemNBT BUKKIT_NBT = new ItemMinecraftNBT();

    public static void setBukkitNBT(@NotNull Plugin plugin) {
        StackBuilder.BUKKIT_NBT = new ItemBukkitNBT(plugin);
    }

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
    @Nullable
    private Integer damage;

    @Getter
    private int dyeColor = -1;

    @Getter
    @Nullable
    private Integer maxStackSize;

    private final Map<Enchantment, Integer> enchantments;

    private final Set<ItemFlag> flags;

    @Nullable
    private PlayerProfile headTexture;

    @Nullable
    private Rarity rarity;

    @NotNull
    private final List<Pair<String, String>> persistentData = new ArrayList<>();

    @Nullable
    private Boolean hideToolTip;

    private boolean glow;

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

        this.displayName = itemMeta.getDisplayName();
        this.amount = stack.getAmount();

        this.enchantments = new LinkedHashMap<>(stack.getEnchantments());
        this.flags = new HashSet<>(this.itemMeta.getItemFlags());

        List<String> lore = itemMeta.getLore();
        if(lore != null) {
            this.lore.addAll(lore);
        }

        if(itemMeta instanceof Damageable damageable) {
            this.unbreakable = damageable.isUnbreakable();

            int damage = damageable.getDamage();
            if(damage > 0) {
                this.damage = damageable.getDamage();
            }
        }

        if(itemMeta.hasCustomModelData()) {
            this.customModelData = itemMeta.getCustomModelData();
        }

        if(Version.isAtLeast(Version.v1_20_R4)) {
            if(itemMeta.hasRarity()) {
                this.rarity = Rarity.getRarity(itemMeta.getRarity());
            }

            if(itemMeta.hasMaxStackSize()) {
                this.maxStackSize = itemMeta.getMaxStackSize();
            }

            if(itemMeta.isHideTooltip()) {
                this.hideToolTip = itemMeta.isHideTooltip();
            }
        }

    }

    public StackBuilder setName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public StackBuilder addLore(@NotNull String... lore) {
        return this.addLore(Arrays.asList(lore));
    }

    public StackBuilder addLore(@NotNull List<String> lore) {
        List<String> tempList = new ArrayList<>();
        for (String s : lore) {
            if(!s.contains("\n")) {
                tempList.add(ColorUtils.parseText(s));
                continue;
            }

            tempList.addAll(Arrays.stream(s.split("\n")).map(ColorUtils::parseText).toList());
        }

        this.lore.addAll(tempList);
        return this;
    }

    public StackBuilder clearLore() {
        this.lore.clear();
        return this;
    }

    public StackBuilder setLore(@NotNull List<String> lore) {
        this.lore.clear();
        this.addLore(lore);
        return this;
    }

    public StackBuilder setLore(@NotNull String... lore) {
        this.lore.clear();
        this.addLore(lore);
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

    public StackBuilder setDyeColor(@Nullable java.awt.Color color) {
        return this.setDyeColor(color != null ? color.getRGB() : null);
    }

    public StackBuilder setDyeColor(@Nullable Color color) {
        return this.setDyeColor(color != null ? color.asRGB() : null);
    }

    public StackBuilder setDyeColor(@Nullable Integer rgb) {
        this.dyeColor = rgb != null ? rgb : -1;
        return this;
    }

    public StackBuilder addEnchantment(@NotNull String enchantment, int level) {
        Enchantment byName = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantment.toLowerCase()));
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

    public StackBuilder allFlags() {
        this.flags.addAll(Arrays.asList(ItemFlag.values()));
        return this;
    }

    public StackBuilder setFlags(@NotNull String... flags) {
        if(Arrays.stream(flags).anyMatch(f -> f.equalsIgnoreCase("all"))) {
            this.setFlags(Arrays.asList(ItemFlag.values()));
            return this;
        }

        try {
            List<ItemFlag> list = Arrays.stream(flags)
                    .map(v -> ItemFlag.valueOf(v.toUpperCase()))
                    .toList();

            this.setFlags(list);
        } catch (Exception ignored) { }

        return this;
    }

    public StackBuilder setFlags(@NotNull Collection<ItemFlag> flags) {
        this.flags.addAll(flags);
        return this;
    }

    public StackBuilder setFlags(@NotNull ItemFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public StackBuilder addNBT(@NotNull String key, @NotNull String value) {
        persistentData.add(new Pair<>(key, value));
        return this;
    }

    public <T, Z> StackBuilder addPersistentData(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {
        itemMeta.getPersistentDataContainer().set(key, type, value);
        return this;
    }

    public <T, Z> StackBuilder addPersistentData(@NotNull JavaPlugin plugin, @NotNull String key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), type, value);
        return this;
    }

    public StackBuilder setHeadTexture(@NotNull String texture) {
        this.headTexture = ProfileUtils.getProfileTexture(texture);
        return this;
    }

    public StackBuilder setMaxStackSize(@Nullable Integer maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    public StackBuilder setRarity(@Nullable Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public StackBuilder setHideTooltip(boolean hideTooltip) {
        this.hideToolTip = hideTooltip;
        return this;
    }

    public StackBuilder setGlow(boolean glow) {
        this.glow = glow;
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

        itemMeta.setLore(this.lore);
        itemMeta.setCustomModelData(customModelData >= 0 ? customModelData : null);

        if(itemMeta instanceof Damageable damageable) {
            if(damage != null && damage >= 0) {
                damageable.setDamage(damage);
            }

            damageable.setUnbreakable(unbreakable);
        }


        if(this.dyeColor != -1) {
            if(Version.isAtLeast(Version.v1_20_R1) && itemMeta instanceof org.bukkit.inventory.meta.ColorableArmorMeta colorable) {
                colorable.setColor(Color.fromRGB(this.dyeColor));
            } else if(itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(Color.fromRGB(this.dyeColor));
            }
        }

        itemMeta.addItemFlags(flags.toArray(ItemFlag[]::new));

        if(Version.isAtLeast(Version.v1_20_R4)) {
            if(this.rarity != null) {
                itemMeta.setRarity(rarity.getRarity());
            }

            if(this.maxStackSize != null && this.maxStackSize > 0) {
                itemMeta.setMaxStackSize(Math.min(99, this.maxStackSize));
            }

            if(this.hideToolTip != null) {
                itemMeta.setHideTooltip(this.hideToolTip);
            }
        }

        if(this.glow && !itemMeta.hasEnchant(Enchantment.UNBREAKING)) {
            itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        ItemStack stack = new ItemStack(material);
        stack.setAmount(amount);
        stack.setItemMeta(itemMeta);
        stack.addUnsafeEnchantments(enchantments);

        if(headTexture != null && itemMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwnerProfile(headTexture);
            stack.setItemMeta(skullMeta);
        }

        if(!persistentData.isEmpty()) {
            persistentData.forEach(pair -> BUKKIT_NBT.set(stack, PersistentDataType.STRING, pair.key(), pair.value()));
        }

        return stack;
    }

    @NotNull
    public ItemStack build(@NotNull ReplaceBuilder replace, Object... objects) {
        Material material = objects[objects.length - 1] instanceof Material mat ? mat : this.material;

        if(material == Material.AIR || !material.isItem()) {
            return new ItemStack(Material.AIR);
        }

        itemMeta.setDisplayName(displayName == null ?
                null :
                ColorUtils.parseText(replace.replaceMessage(displayName, objects)));

        List<String> lore = new ArrayList<>(this.lore.stream()
                .filter(line -> line.isBlank() || !replace.replaceMessage(line, objects).isEmpty())
                .map(line -> replace.replaceMessage(line, objects))
                .toList());

        for (String s : List.copyOf(lore)) {
            if(!s.contains("\n")) {
                continue;
            }

            int i = lore.indexOf(s);
            String[] split = s.split("\n");
            if(i == -1) {
                continue;
            }

            lore.remove(i);
            lore.addAll(i, Arrays.asList(split));
        }

        itemMeta.setLore(lore);

        itemMeta.setCustomModelData(customModelData >= 0 ? customModelData : null);

        if(itemMeta instanceof Damageable damageable) {
            if(damage != null && damage >= 0) {
                damageable.setDamage(damage);
            }

            damageable.setUnbreakable(unbreakable);
        }

        itemMeta.addItemFlags(flags.toArray(ItemFlag[]::new));

        if(this.dyeColor != -1) {
            if(Version.isAtLeast(Version.v1_20_R1) && itemMeta instanceof org.bukkit.inventory.meta.ColorableArmorMeta colorable) {
                colorable.setColor(Color.fromRGB(this.dyeColor));
            } else if(itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(Color.fromRGB(this.dyeColor));
            }
        }

        if(Version.isAtLeast(Version.v1_20_R4)) {
            if(this.rarity != null) {
                itemMeta.setRarity(rarity.getRarity());
            }

            if(this.maxStackSize != null && this.maxStackSize > 0) {
                itemMeta.setMaxStackSize(Math.min(99, this.maxStackSize));
            }

            if(this.hideToolTip != null) {
                itemMeta.setHideTooltip(this.hideToolTip);
            }
        }

        ItemStack stack = new ItemStack(material);
        stack.setAmount(Math.min(amount, 64));
        stack.setItemMeta(itemMeta);
        stack.addUnsafeEnchantments(enchantments);

        if(headTexture != null && itemMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwnerProfile(headTexture);
            stack.setItemMeta(skullMeta);
        }

        if(!persistentData.isEmpty()) {
            persistentData.forEach(pair -> BUKKIT_NBT.set(stack, PersistentDataType.STRING, pair.key(), replace.replaceMessage(pair.value(), objects)));
        }

        return stack;
    }

    @NotNull
    public ItemStack build(@NotNull Function<String, String> function) {
        Material material = this.material;

        if(material == Material.AIR || !material.isItem()) {
            return new ItemStack(Material.AIR);
        }

        itemMeta.setDisplayName(displayName == null ?
                null :
                ColorUtils.parseText(function.apply(displayName)));

        List<String> lore = new ArrayList<>(this.lore.stream()
                .filter(line -> line.isBlank() || !function.apply(line).isEmpty())
                .map(function).toList());

        for (String s : List.copyOf(lore)) {
            if(!s.contains("\n")) {
                continue;
            }

            int i = lore.indexOf(s);
            String[] split = s.split("\n");
            if(i == -1) {
                continue;
            }

            lore.remove(i);
            lore.addAll(i, Arrays.asList(split));
        }

        itemMeta.setLore(lore);

        itemMeta.setCustomModelData(customModelData >= 0 ? customModelData : null);

        if(itemMeta instanceof Damageable damageable) {
            if(damage != null && damage >= 0) {
                damageable.setDamage(damage);
            }

            damageable.setUnbreakable(unbreakable);
        }

        itemMeta.addItemFlags(flags.toArray(ItemFlag[]::new));

        if(this.dyeColor != -1) {
            if(Version.isAtLeast(Version.v1_20_R1) && itemMeta instanceof org.bukkit.inventory.meta.ColorableArmorMeta colorable) {
                colorable.setColor(Color.fromRGB(this.dyeColor));
            } else if(itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(Color.fromRGB(this.dyeColor));
            }
        }

        if(Version.isAtLeast(Version.v1_20_R4)) {
            if(this.rarity != null) {
                itemMeta.setRarity(rarity.getRarity());
            }

            if(this.maxStackSize != null && this.maxStackSize > 0) {
                itemMeta.setMaxStackSize(Math.min(99, this.maxStackSize));
            }

            if(this.hideToolTip != null) {
                itemMeta.setHideTooltip(this.hideToolTip);
            }
        }

        ItemStack stack = new ItemStack(material);
        stack.setAmount(Math.min(amount, 64));
        stack.setItemMeta(itemMeta);
        stack.addUnsafeEnchantments(enchantments);

        if(headTexture != null && itemMeta instanceof SkullMeta skullMeta) {
            skullMeta.setOwnerProfile(headTexture);
            stack.setItemMeta(skullMeta);
        }

        if(!persistentData.isEmpty()) {
            persistentData.forEach(pair -> BUKKIT_NBT.set(stack, PersistentDataType.STRING, pair.key(), function.apply(pair.value())));
        }

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
        try {
            return (StackBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    @NotNull
    public static StackBuilder parseString(@NotNull String text) {
        return StackBuilder.parseString(text, new StackBuilder(Material.STONE));
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static StackBuilder parseString(@NotNull String text, @Nullable StackBuilder def) {
        String[] s = text.split(" ");
        if(s.length == 0) {
            return def;
        }

        Material material = EnumUtils.getEnum(s[0], Material.class);
        if(s.length == 1 || material == null || material.isAir()) {
            return material != null && !material.isAir() ? new StackBuilder(material) : def;
        }

        StackBuilder builder = new StackBuilder(material.isAir() ? Material.STONE : material);
        for (int i = 1; i < s.length; i++) {
            String[] split = s[i].split(":");

            if(split.length < 2) {
                continue;
            }

            String key = split[0].toLowerCase();
            String value = split[1];

            switch (key) {
                case "name", "displayname","n" -> builder.setName(value.replace("_", " "));
                case "lore","l" -> builder.addLore(Arrays.stream(value.split(",")).map(v -> v.replace("_", " ")).toList());
                case "custommodeldata", "cmd", "model","c" -> builder.setCustomModelData(NumberUtils.parseInteger(value, 1));
                case "amount","a" -> builder.setAmount(NumberUtils.parseInteger(value, 1));
                case "damage", "dmg","d" -> builder.setDamage(NumberUtils.parseInteger(value, 1));
                case "unbreakable", "unbr", "unb","u" -> builder.setUnbreakable(Boolean.parseBoolean(value));
                case "flag", "flags","f" -> builder.setFlags(value.split(","));
                case "head", "head_texture","skull","h" -> builder.setHeadTexture(value);
                case "maxstack", "maxstacksize" -> builder.setMaxStackSize(NumberUtils.parseInteger(value, material.getMaxStackSize()));
                case "rarity", "rare" -> builder.setRarity(EnumUtils.getEnum(value, Rarity.class));
                case "enchant", "ench", "enchantment", "e" -> {
                    String[] ench = value.split(";");
                    if (ench.length < 2) {
                        continue;
                    }

                    builder.addEnchantment(ench[0], NumberUtils.parseInteger(ench[1]));
                }
                case "nbt", "tag", "data" -> {
                    String[] nbt = value.split(";");
                    if (nbt.length < 2) {
                        continue;
                    }

                    builder.addNBT(nbt[0], nbt[1]);
                }
                case "hidetooltip", "htt" -> builder.setHideTooltip(Boolean.parseBoolean(value));
                case "glow", "glowing" -> builder.setGlow(Boolean.parseBoolean(value));
            }
        }

        return builder;
    }

}
