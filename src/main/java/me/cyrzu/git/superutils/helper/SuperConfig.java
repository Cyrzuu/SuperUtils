package me.cyrzu.git.superutils.helper;

import com.google.common.base.Charsets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
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
    public final static Pattern MESSAGE_PATTERN = Pattern.compile("messages?:\\s*\\w+");

    @NotNull
    public final static Pattern ITEM_PATTERN = Pattern.compile("item:\\w+");


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

    public SuperConfig(@NotNull Plugin plugin, @NotNull String resource) {
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
        Object val = this.get(path, def);
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

        this.saveResource(this.config, this.resourceConfig);
        this.config.save(this.file);
        this.loadConfiguration(this.config, "");
    }

    private void saveResource(@NotNull ConfigurationSection config, @NotNull ConfigurationSection resourceConfig) {
        for (String key : resourceConfig.getKeys(false)) {
            Object object = resourceConfig.get(key);
            if(object == null) {
                continue;
            }

            if(object instanceof ConfigurationSection resourceSection) {
                ConfigurationSection configSection = config.getConfigurationSection(key);
                configSection = configSection == null ? config.createSection(key) : configSection;

                this.saveResource(configSection, resourceSection);
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
            this.data.put(key, location);
            return;
        }

        SuperConfig.createDefaultMessageFile(new File(plugin.getDataFolder(), "message.json"));
        Matcher messagePattern = SuperConfig.MESSAGE_PATTERN.matcher(string);
        if(messagePattern.matches()) {
            Message message = this.parseMessage(string.split(":")[1]);
            if(message != null) {
                this.data.put(key, message);
                return;
            }
        }

        StackBuilder builder = StackBuilder.parseString(string, null);
        if(builder != null) {
            SuperConfig.createDefaultItemFile(new File(plugin.getDataFolder(), "item.json"));
            this.data.put(key + ".item", builder.build());
        }

        Matcher itemPattern = SuperConfig.ITEM_PATTERN.matcher(string);
        if(itemPattern.matches()) {
            ItemConfig item = this.parseItem(string.split(":")[1]);
            if(item != null) {
                this.data.put(key + ".item", item.getItem());
            }
        }

        this.data.put(key, ColorUtils.parseText(string));
    }

    @Nullable
    private Message parseMessage(@NotNull String id) {
        File messageJson = new File(plugin.getDataFolder(), "message.json");
        String json = FileUtils.readFileToString(messageJson);
        JsonReader reader = JsonReader.parseString(json);

        JsonReader messageReader = reader == null ? null : reader.getReader(id);
        return messageReader != null ? new Message(messageReader) : null;
    }

    @Nullable
    private ItemConfig parseItem(@NotNull String id) {
        File messageJson = new File(plugin.getDataFolder(), "item.json");
        if(!messageJson.exists()) {
            SuperConfig.createDefaultItemFile(messageJson);
        }

        String json = FileUtils.readFileToString(messageJson);
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
