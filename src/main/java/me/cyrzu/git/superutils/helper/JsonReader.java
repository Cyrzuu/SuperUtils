package me.cyrzu.git.superutils.helper;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import me.cyrzu.git.superutils.EnumUtils;
import me.cyrzu.git.superutils.FileUtils;
import me.cyrzu.git.superutils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class JsonReader {

    @NotNull
    private final JsonObject jsonObject;

    private JsonReader(@NotNull JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @NotNull
    public JsonWriter toJsonWriter() {
        return new JsonWriter(this.jsonObject.deepCopy());
    }

    public boolean isSet(@NotNull String path) {
        return this.get(path) != null;
    }

    @NotNull
    public Set<String> keySet() {
        return jsonObject.keySet();
    }

    @NotNull
    public Map<String, JsonReader> getKeysWithReader(@NotNull String path) {
        JsonReader reader = this.getReader(path);
        if(reader == null) {
            return Collections.emptyMap();
        }

        return reader.getKeysWithReader();
    }

    @NotNull
    public Map<String, JsonReader> getKeysWithReader() {
        Map<String, JsonReader> reader = new HashMap<>();

        Set<String> keys = this.keySet();
        for (String key : keys) {
            JsonElement jsonElement = this.get(key);
            if(!(jsonElement instanceof JsonObject object)) {
                continue;
            }

            reader.put(key, new JsonReader(object));
        }

        return ImmutableMap.copyOf(reader);
    }

    @NotNull
    public Map<String, JsonPrimitive> getKeysWithValue(@NotNull String path) {
        JsonReader reader = this.getReader(path);
        if(reader == null) {
            return Collections.emptyMap();
        }

        return reader.getKeysWithValue();
    }

    @NotNull
    public Map<String, JsonPrimitive> getKeysWithValue() {
        Map<String, JsonPrimitive> primatives = new HashMap<>();

        for (String key: this.keySet()) {
            JsonElement jsonElement = this.get(key);
            if(!(jsonElement instanceof JsonPrimitive object)) {
                continue;
            }

            primatives.put(key, object);
        }

        return primatives;
    }

    @Nullable
    public String getString( @NotNull String path) {
        return this.getString(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String getString(@NotNull String path, @Nullable String def) {
        try {
            JsonElement element = this.get(path);
            return element == null ? def : element.getAsString();
        } catch (Exception e) {
            return def;
        }
    }

    public int getInt(@NotNull String path) {
        return getInt(path, 0);
    }

    public int getInt(@NotNull String path, int def) {
        try {
            JsonElement element = get(path);
            return element == null ? def : element.getAsInt();
        } catch (Exception e) {
            return def;
        }
    }

    public boolean getBoolean(@NotNull JsonObject object, @NotNull String path) {
        return getBoolean(path, false);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        try {
            JsonElement element = get(path);
            return element == null ? def : element.getAsBoolean();
        } catch (Exception e) {
            return def;
        }
    }

    public double getDouble(@NotNull String path) {
        return getDouble(path, 0);
    }

    public double getDouble(@NotNull String path, double def) {
        try {
            JsonElement element = get(path);
            return element == null ? def : element.getAsDouble();
        } catch (Exception e) {
            return def;
        }
    }

    public long getLong(@NotNull String path) {
        return getLong(path, 0);
    }

    public long getLong(@NotNull String path, long def) {
        try {
            JsonElement element = get(path);
            return element == null ? def : element.getAsLong();
        } catch (Exception e) {
            return def;
        }
    }

    @Nullable
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz) {
        return getEnum(path, clazz, null);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        return EnumUtils.getEnum(getString(path, ""), clazz, def);
    }

    @Nullable
    public Location getLocation(@NotNull String path) {
        return this.getLocation(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Location getLocation(@NotNull String path, @Nullable Location def) {
        JsonReader reader = this.getReader(path);
        if(reader == null || (!reader.isSet("x") || !reader.isSet("y") || !reader.isSet("z"))) {
            return def;
        }

        World world = Bukkit.getWorld(reader.getString("world", ""));
        if(world == null) {
            return def;
        }

        return new Location(world, reader.getDouble("x"), reader.getDouble("y"), reader.getDouble("z"), (float) reader.getDouble("yaw"), (float) reader.getDouble("pitch"));
    }

    @Nullable
    public Location getLocationBlock(@NotNull String path) {
        return this.getLocationBlock(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Location getLocationBlock(@NotNull String path, @Nullable Location def) {
        JsonReader reader = this.getReader(path);
        if(reader == null || (!reader.isSet("x") || !reader.isSet("y") || !reader.isSet("z"))) {
            return def;
        }

        World world = Bukkit.getWorld(reader.getString("world", ""));
        if(world == null) {
            return def;
        }

        return new Location(world, reader.getDouble("x"), reader.getDouble("y"), reader.getDouble("z"));
    }

    @Nullable
    public Vector getVector(@NotNull String path) {
        return this.getVector(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Vector getVector(@NotNull String path, @Nullable Vector def) {
        JsonReader reader = this.getReader(path);
        if(reader == null || (!reader.isSet("x") || !reader.isSet("y") || !reader.isSet("z"))) {
            return def;
        }

        return new Vector(reader.getDouble("x"), reader.getDouble("y"), reader.getDouble("z"));
    }

    @Nullable
    public Bound getBound(@NotNull String path) {
        return this.getBound(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Bound getBound(@NotNull String path, @Nullable Bound def) {
        JsonReader reader = this.getReader(path);
        if(reader == null) {
            return def;
        }

        JsonReader min = reader.getReader("min");
        JsonReader max = reader.getReader("max");
        if(min == null || max == null) {
            return def;
        }

        return new Bound(
                min.getInt("x"), min.getInt("y"), min.getInt("z"),
                max.getInt("x"), max.getInt("y"), max.getInt("z")
        );
    }

    @NotNull
    public JsonArray getJsonArray(@NotNull String path) {
        return getJsonArray(path, new JsonArray());
    }

    @Nullable
    @Contract("_, !null -> !null")
    public JsonArray getJsonArray(@NotNull String path, @Nullable JsonArray def) {
        JsonElement jsonElement = this.get(path);
        return jsonElement instanceof JsonArray array ? array : def;
    }

    @NotNull
    public List<String> getListString(@NotNull String path) {
        return getListString(path, new ArrayList<>());
    }

    @NotNull
    public List<String> getListString(@NotNull String path, @NotNull List<String> def) {
        try {
            List<JsonElement> list = this.getList(path);
            if(list == null) {
                return def;
            }

            return list.stream().map(JsonElement::getAsString).toList();
        } catch (Exception e) {
            return def;
        }
    }

    @NotNull
    public List<JsonReader> getListReader(@NotNull String path) {
        return getListReader(path, new ArrayList<>());
    }

    @NotNull
    public List<JsonReader> getListReader(@NotNull String path, @NotNull List<JsonReader> def) {
        try {
            List<JsonElement> list = this.getList(path);
            if(list == null) {
                return def;
            }

            return list.stream()
                    .filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject)
                    .map(JsonReader::parseObject)
                    .toList();
        } catch (Exception e) {
            return def;
        }
    }

    @Nullable
    public List<JsonElement> getList(@NotNull String path) {
        return this.getList(path, new ArrayList<>());
    }

    @Nullable
    @Contract("_, !null -> !null")
    public List<JsonElement> getList(@NotNull String path, @Nullable List<JsonElement> def) {
        if(!(this.get(path) instanceof JsonArray array)) {
            return def;
        }

        List<JsonElement> list = new ArrayList<>();
        array.forEach(list::add);
        return list;
    }

    @Nullable
    public UUID getUUID(@NotNull String path) {
        return this.getUUID(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public UUID getUUID(@NotNull String path, @Nullable UUID def) {
        UUID uuid = StringUtils.toUUID(this.getString(path, ""));
        return uuid != null ? uuid : def;
    }

    public void getReader(@NotNull String path, @NotNull Consumer<JsonReader> fun) {
        JsonReader reader = this.getReader(path);
        if(reader == null) {
            return;
        }

        fun.accept(reader);
    }

    @Nullable
    public JsonReader getReader(@NotNull String path) {
        return this.getReader(path, (JsonReader) null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public JsonReader getReader(@NotNull String path, @Nullable JsonReader def) {
        JsonElement jsonElement = this.get(path);
        return jsonElement instanceof JsonObject object ? new JsonReader(object) : def;
    }

    @Nullable
    public JsonElement get(@NotNull String path) {
        return this.get(path, null);
    }

    public <T> void getAndRun(@NotNull String path, @NotNull Class<T> clazz, @NotNull Consumer<T> function) {
        JsonElement json = this.get(path);
        if(json instanceof JsonObject obj && clazz.equals(JsonReader.class)) {
            function.accept(clazz.cast(JsonReader.parseObject(obj)));
        } else if(json instanceof JsonObject obj && clazz.equals(JsonObject.class)) {
            function.accept(clazz.cast(JsonReader.parseObject(obj)));
        } else if (json instanceof JsonPrimitive primitive) {
            Object value = null;

            if (clazz.equals(String.class)) {
                value = primitive.getAsString();
            } else if ((clazz.equals(Integer.class) || clazz.equals(int.class)) && primitive.isNumber()) {
                value = primitive.getAsInt();
            } else if ((clazz.equals(Double.class) || clazz.equals(double.class)) && primitive.isNumber()) {
                value = primitive.getAsDouble();
            } else if ((clazz.equals(Boolean.class) || clazz.equals(boolean.class)) && primitive.isBoolean()) {
                value = primitive.getAsBoolean();
            } else if ((clazz.equals(Long.class) || clazz.equals(long.class)) && primitive.isNumber()) {
                value = primitive.getAsLong();
            } else if ((clazz.equals(Float.class) || clazz.equals(float.class)) && primitive.isNumber()) {
                value = primitive.getAsFloat();
            }

            if(value != null && clazz.equals(value.getClass())) {
                function.accept(clazz.cast(value));
            }
        } else if(json instanceof JsonArray array && clazz.equals(JsonArray.class)) {
            function.accept(clazz.cast(array));
        }
    }

    @Nullable
    @Contract("_, !null -> !null")
    public JsonElement get(@NotNull String path, @Nullable JsonElement def) {
        JsonElement object = jsonObject;
        try {
            String[] array = path.split("\\.");
            int length = array.length;

            if(length == 1) {
                return jsonObject.get(array[0]);
            }

            int index = 0;
            for (String key : array) {
                if(!(object instanceof JsonObject obj)) {
                    return def;
                }

                if(++index >= length) {
                    return obj.get(key);
                }

                object = obj.get(key);
            }
        } catch (JsonSyntaxException ignored) { }

        return def;
    }

    public static void parseFile(@NotNull File file, @NotNull Consumer<JsonReader> function) {
        JsonReader reader = JsonReader.parseFile(file);
        if(reader != null) {
            function.accept(reader);
        }
    }

    @Nullable
    public static JsonReader parseFile(@NotNull File file) {
        return JsonReader.parseString(FileUtils.readFileToString(file, "{}"));
    }

    @Nullable
    public static JsonReader parseString(@NotNull String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            return element instanceof JsonObject object ? new JsonReader(object) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void parseString(@NotNull String json, @NotNull Consumer<JsonReader> function) {
        JsonReader reader = JsonReader.parseString(json);
        if(reader != null) {
            function.accept(reader);
        }
    }

    @NotNull
    public static JsonReader parseObject(@NotNull JsonObject json) {
        return new JsonReader(json);
    }

    @NotNull
    public static <T> JsonArray getArrayString(@NotNull Stream<T> stream, Function<T, String> function) {
        return JsonReader.getArrayString(stream.toList(), function);
    }

    @NotNull
    public static <T> JsonArray getArrayString(@NotNull Collection<T> collect, Function<T, String> function) {
        JsonArray array = new JsonArray();
        collect.forEach(object -> array.add(function.apply(object)));
        return array;
    }

    @NotNull
    public static List<JsonReader> getArray(@NotNull JsonReader reader, @NotNull String path) {
        List<JsonElement> list = reader.getList(path);
        if(list == null) {
            return Collections.emptyList();
        }

        return list.stream()
                .map(element -> element instanceof JsonObject object ? object : null)
                .filter(Objects::nonNull).map(JsonReader::new).toList();
    }

    @NotNull
    public static List<JsonReader> parseJsonArray(@NotNull File file) {
        return JsonReader.parseJsonArray(FileUtils.readFileToString(file, "[]"));
    }

    @NotNull
    public static List<JsonReader> parseJsonArray(@NotNull String json) {
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            if(!(jsonElement instanceof JsonArray array)) {
                return Collections.emptyList();
            }

            List<JsonElement> list = new ArrayList<>();
            array.forEach(list::add);

            return list.stream()
                    .filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject)
                    .map(JsonReader::parseObject)
                    .toList();

        } catch (Exception ignore) { }
        return Collections.emptyList();
    }

    @NotNull
    public static List<String> parseJsonArrayString(@NotNull String json) {
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            if(!(jsonElement instanceof JsonArray array)) {
                return Collections.emptyList();
            }

            List<JsonElement> list = new ArrayList<>();
            array.forEach(list::add);

            return list.stream()
                    .filter(JsonElement::isJsonPrimitive)
                    .map(JsonElement::getAsJsonPrimitive)
                    .map(JsonPrimitive::getAsString)
                    .toList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @NotNull
    public static List<JsonPrimitive> parseJsonArrayPrimative(@NotNull String json) {
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            if(!(jsonElement instanceof JsonArray array)) {
                return Collections.emptyList();
            }

            List<JsonElement> list = new ArrayList<>();
            array.forEach(list::add);

            return list.stream()
                    .filter(JsonElement::isJsonPrimitive)
                    .map(JsonElement::getAsJsonPrimitive)
                    .toList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
