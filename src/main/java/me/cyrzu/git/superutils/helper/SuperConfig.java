package me.cyrzu.git.superutils.helper;

import com.google.common.base.Charsets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import me.cyrzu.git.superutils.EnumUtils;
import me.cyrzu.git.superutils.FileUtils;
import me.cyrzu.git.superutils.LocationUtils;
import me.cyrzu.git.superutils.StackBuilder;
import me.cyrzu.git.superutils.color.ColorUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class SuperConfig {

    @NotNull
    public final static Pattern MESSAGE_PATTERN = Pattern.compile("messages?:.*");

    @NotNull
    public final static Pattern ITEM_PATTERN = Pattern.compile("item:.*");


    @NotNull
    private final Plugin plugin;

    @NotNull
    private final String resource;

    @NotNull
    private final File file;

    @Getter
    private @NotNull FileConfiguration config;

    @Getter
    private @NotNull FileConfiguration resourceConfig;

    @NotNull
    private final Map<String, Object> data;

    @NotNull
    private final Set<String> disabledPaths;

    public final boolean append;

    public SuperConfig(@NotNull Plugin plugin) {
        this(plugin, "config.yml", new String[0]);
    }

    public SuperConfig(@NotNull Plugin plugin, @NotNull String resource) {
        this(plugin, resource, new String[0]);
    }

    public SuperConfig(@NotNull Plugin plugin, @NotNull String resource, @NotNull String... disabledPaths) {
        this(plugin, resource, true, disabledPaths);
    }

    public SuperConfig(@NotNull Plugin plugin, @NotNull String resource, boolean append) {
        this(plugin, resource, append, new String[0]);
    }

    public SuperConfig(@NotNull Plugin plugin, @NotNull String resource, boolean append, @NotNull String... disabledPaths) {
        this.disabledPaths = new HashSet<>(Arrays.asList(disabledPaths));
        this.append = append;
        this.resource = resource.endsWith(".yml") ? resource : resource + ".yml";
        this.file = new File(plugin.getDataFolder(), this.resource);
        if(!file.exists()) {
            plugin.saveResource(this.resource, false);
        }

        this.plugin = plugin;
        this.config = new YamlConfiguration();
        this.resourceConfig = new YamlConfiguration();
        this.data = new ConcurrentHashMap<>();

        this.reloadConfig();
    }

    @Nullable
    public String getString(@NotNull String path) {
        return this.getString(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String getString(@NotNull String path, @Nullable String def) {
        Object val = this.get(path, def);
        return val != null ? val.toString() : null;
    }

    public int getInt(@NotNull String path) {
        return this.getInt(path, 0);
    }

    public int getInt(@NotNull String path, int def) {
        Object val = this.get(path, def);
        return (val instanceof Number number) ? number.intValue() : def;
    }

    public boolean getBoolean(@NotNull String path) {
        return this.getBoolean(path, false);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        Object val = this.get(path, def);
        return (val instanceof Boolean) ? (Boolean) val : def;
    }

    public double getDouble(@NotNull String path) {
        return this.getDouble(path, 0D);
    }

    public double getDouble(@NotNull String path, double def) {
        Object val = this.get(path, def);
        return (val instanceof Number number) ? number.doubleValue() : def;
    }

    public long getLong(@NotNull String path) {
        return this.getInt(path, 0);
    }

    public long getLong(@NotNull String path, int def) {
        Object val = this.get(path, def);
        return (val instanceof Number number) ? number.longValue() : def;
    }

    @Nullable
    public List<?> getList(@NotNull String path) {
        return this.getList(path, Collections.emptyList());
    }

    @Nullable
    @Contract("_, !null -> !null")
    public List<?> getList(@NotNull String path, @Nullable List<?> def) {
        Object val = this.get(path, def);
        return (List<?>) ((val instanceof List) ? val : def);
    }

    @NotNull
    public List<String> getStringList(@NotNull String path) {
        List<?> list = this.getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<String> result = new ArrayList<>();

        for (Object object : list) {
            if ((object instanceof String) || this.isPrimitiveWrapper(object)) {
                result.add(String.valueOf(object));
            }
        }

        return result;
    }

    @NotNull
    public List<Location> getLocationList(@NotNull String path) {
        List<String> list = this.getStringList(path);
        return list.stream()
            .map(LocationUtils::deserialize)
            .filter(Objects::nonNull)
            .toList();
    }

    @NotNull
    public List<BridgeResult> getListBridge(@NotNull String path) {
        List<Map<?, ?>> mapList = config.getMapList(path);
        return mapList.stream().map(BridgeResult::new).toList();
    }

    @Nullable
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz) {
        return this.getEnum(path, clazz, null);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        return EnumUtils.getEnum(this.getString(path, ""), clazz, def);
    }

    @NotNull
    public Message getMessageOrEmpty(@NotNull String path) {
        return this.getMessage(path, Message.EMPTY_MESSAGE);
    }

    @Nullable
    public Message getMessage(@NotNull String path) {
        return this.getMessage(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Message getMessage(@NotNull String path, @Nullable Message def) {
        Object val = this.get(path + ".message", def);
        if(val instanceof String string) {
            return Message.of(string);
        }

        return val instanceof Message message ? message : def;
    }

    @Nullable
    public ItemStack getItemStack(@NotNull String path) {
        return this.getItemStack(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public ItemStack getItemStack(@NotNull String path, @Nullable ItemStack def) {
        Object val = this.get(path + ".item", def);
        if(!(val instanceof ItemStack item)) {
            return def;
        }

        return item;
    }

    @Nullable
    public ItemStack getItemStack(@NotNull String path, @NotNull ReplaceBuilder replace, @NotNull Object... values) {
        return this.getItemStack(path, null, replace, values);
    }

    @Nullable
    @Contract("_, !null, _, _ -> !null; _, null, _, _ -> _")
    public ItemStack getItemStack(@NotNull String path, @Nullable ItemStack def, @NotNull ReplaceBuilder replace, @NotNull Object... values) {
        Object val = this.get(path + ".itembuilder", def);
        if(!(val instanceof StackBuilder item)) {
            return def;
        }

        return item.build(replace, values);
    }

    @Nullable
    public Location getLocation(@NotNull String path) {
        return this.getLocation(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Location getLocation(@NotNull String path, @Nullable Location def) {
        Object val = this.get(path + ".location", def);
        if(!(val instanceof Location location)) {
            return def;
        }

        return location;
    }

    @NotNull
    public List<String> getKeys(@NotNull String path) {
        return this.getKeys(path, false);
    }

    @NotNull
    public List<String> getKeys(@NotNull String path, boolean deep) {
        ConfigurationSection section = this.config.getConfigurationSection(path);
        return section == null ? Collections.emptyList() :
            List.copyOf(section.getKeys(deep));
    }

    @NotNull
    public Map<String, @NotNull ConfigurationSection> getSections(@NotNull String path) {
        Map<String, @NotNull ConfigurationSection> map = new LinkedHashMap<>();

        for (String key : this.getKeys(path)) {
            ConfigurationSection section = this.config.getConfigurationSection(path + "." + key);
            if(section != null) {
                map.put(key, section);
            }
        }

        return map;
    }

    @Nullable
    public Object get(@NotNull String path) {
        return this.get(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Object get(@NotNull String path, @Nullable Object def) {
        Object object = this.data.get(path);
        return object != null ? object : def;
    }

    @SneakyThrows
    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
        InputStream resourceStream = plugin.getResource(resource);
        if(resourceStream == null) {
            this.resourceConfig = new YamlConfiguration();
        } else {
            InputStreamReader resourceReader = new InputStreamReader(resourceStream, Charsets.UTF_8);
            this.resourceConfig = YamlConfiguration.loadConfiguration(resourceReader);
        }

        if(append) {
            this.saveResource(this.config, this.resourceConfig, "");
        }

        this.config.save(this.file);
        this.loadConfiguration(this.config, "");
    }

    private void saveResource(@NotNull ConfigurationSection config, @NotNull ConfigurationSection resourceConfig, @NotNull String path) {
        for (String key : resourceConfig.getKeys(false)) {
            String newPath = path.isEmpty() ? key : path + "." + key;
            Object object = resourceConfig.get(key);
            if(object == null) {
                continue;
            }

            if(object instanceof ConfigurationSection resourceSection) {
                ConfigurationSection configSection = config.getConfigurationSection(key);
                configSection = configSection == null ? config.createSection(key) : configSection;

                if(this.disabledPaths.contains(newPath)) {
                    continue;
                }

                this.saveResource(configSection, resourceSection, newPath);
                continue;
            }

            if(!config.isSet(key)) {
                config.set(key, object);
            }
        }
    }

    private void loadConfiguration(@NotNull ConfigurationSection config, @NotNull String path) {
        for (String key : config.getKeys(false)) {
            String newPath = path.isEmpty() ? key : path + "." + key;
            Object object = config.get(key);
            if(object == null) {
                continue;
            }

            if(object instanceof ConfigurationSection section) {
                this.loadConfiguration(section, newPath);
                continue;
            }

            this.putData(newPath, object);
        }
    }

    private void putData(@NotNull String key, @NotNull Object object) {
        if(!(object instanceof String string)) {
            this.data.put(key, object);
            return;
        }

        Location location = LocationUtils.deserialize(string);
        if(location != null) {
            this.data.put(key + ".location", location);
        }

        String parseText = ColorUtils.parseText(string);
        this.data.put(key + ".message", string.isEmpty() ? Message.getEmptyMessage() : new Message(parseText));

        SuperConfig.createDefaultMessageFile(new File(plugin.getDataFolder(), "message.json"));
        Matcher messagePattern = SuperConfig.MESSAGE_PATTERN.matcher(string);
        if(messagePattern.matches()) {
            Message message = this.parseMessage(string.split(":", 2)[1]);
            if(message != null) {
                this.data.put(key + ".message", message);
            }
        }

        StackBuilder builder = StackBuilder.parseString(string, null);
        if(builder != null) {
            SuperConfig.createDefaultItemFile(new File(plugin.getDataFolder(), "item.json"));
            this.data.put(key + ".itembuilder", builder);
            this.data.put(key + ".item", builder.build());
        }

        Matcher itemPattern = SuperConfig.ITEM_PATTERN.matcher(string);
        if(itemPattern.matches()) {
            ItemConfig item = this.parseItem(string.split(":")[1]);
            if(item != null) {
                this.data.put(key + ".itembuilder", item.getBuilder());
                this.data.put(key + ".item", item.getItem());
            }
        }

        this.data.put(key, parseText);
    }

    @Nullable
    private Message parseMessage(@NotNull String id) {
        File messageJson = new File(plugin.getDataFolder(), "message.json");
        String json = FileUtils.readFileToString(messageJson, "{}");
        JsonReader reader = JsonReader.parseString(json);

        JsonReader messageReader = reader == null ? null : reader.getReader(id);
        if((id.equalsIgnoreCase("empty") || id.equalsIgnoreCase("null")) && messageReader == null) {
            return Message.EMPTY_MESSAGE;
        }

        return messageReader != null ? new Message(messageReader) : null;
    }

    @Nullable
    private ItemConfig parseItem(@NotNull String id) {
        File messageJson = new File(plugin.getDataFolder(), "item.json");
        if(!messageJson.exists()) {
            SuperConfig.createDefaultItemFile(messageJson);
        }

        String json = FileUtils.readFileToString(messageJson, "{}");
        JsonReader reader = JsonReader.parseString(json);

        JsonReader itemReader = reader == null ? null : reader.getReader(id);
        return itemReader != null ? new ItemConfig(itemReader) : null;
    }

    private boolean isPrimitiveWrapper(@Nullable Object input) {
        return input instanceof Integer || input instanceof Boolean
                || input instanceof Character || input instanceof Byte
                || input instanceof Short || input instanceof Double
                || input instanceof Long || input instanceof Float;
    }

    private static void createDefaultMessageFile(@NotNull File file) {
        if(file.exists()) {
            return;
        }

        FileUtils.createFile(file);
        try(FileWriter writer = new FileWriter(file)) {
            writer.append("""
                    {
                      "example1": {
                        "message": "&7Example message :D",
                        "sound": {
                          "name": "ENTITY_EXPERIENCE_ORB_PICKUP",
                          "volume": 0.5,
                          "pitch": 1.25
                        }
                      },
                      "example2": {
                        "messages": [
                          "&7First message",
                          "&7Second message"
                        ],
                        "sound": {
                          "name": "UI_BUTTON_CLICK"
                        },
                        "actionbar": "&7Cool acitonbar",
                        "title": {
                          "title": "First line",
                          "subtitle": "Second line",
                          "fadeIn": 0,
                          "stay": 30,
                          "fadeOut": 10
                        }
                      }
                    }
                    """);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createDefaultItemFile(@NotNull File file) {
        if(file.exists()) {
            return;
        }

        FileUtils.createFile(file);
        try(FileWriter writer = new FileWriter(file)) {
            writer.append("""
                    {
                      "example1": {
                        "type": "diamond sword",
                        "name": "&7Displayed Name",
                        "lore": [
                          "&3First limne",
                          "&3Second line"
                        ],
                        "custommodeldata": 1,
                        "amount": 1,
                        "damage": 20,
                        "unbreakable": true,
                        "flags": [
                          "HIDE_ENCHANTS",
                          "HIDE_UNBREAKABLE"
                        ],
                        "enchants": {
                          "sharpness": 6,
                          "unbreaking": 4
                        }
                      },
                      "example2": {
                        "type": "player head",
                        "head_texture": "95f7fa5de933e26bdc36800099f752f65bce135a003cb050b1537b75026f816c",
                        "rarity": "epic",
                        "maxstacksize": 2,
                        "flags": [
                          "ALL"
                        ]
                      }
                    }
                    """);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
