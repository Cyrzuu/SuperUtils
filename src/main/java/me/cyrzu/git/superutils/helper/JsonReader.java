package me.cyrzu.git.superutils.helper;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import me.cyrzu.git.superutils.EnumUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private Set<String> keySet() {
        return jsonObject.keySet();
    }

    @NotNull
    private Map<String, JsonReader> getKeyValues() {
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

    @Nullable
    public String getString( @NotNull String path) {
        return getString(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String getString(@NotNull String path, @Nullable String def) {
        try {
            JsonElement element = get(path);
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

    public double getDouble(@NotNull JsonObject object, @NotNull String path) {
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

    @NotNull
    public List<String> getListString(@NotNull String path) {
        return getListString(path, new ArrayList<>());
    }

    @NotNull
    public List<String> getListString(@NotNull String path, @NotNull List<String> def) {
        try {
            JsonElement element = get(path);
            if(element == null) {
                return def;
            }

            JsonArray array = element.getAsJsonArray();
            return array.asList().stream().map(JsonElement::getAsString).toList();
        } catch (Exception e) {
            return def;
        }
    }

    @NotNull
    public List<JsonReader> getListObjects(@NotNull String path) {
        return getListObjects(path, new ArrayList<>());
    }

    @NotNull
    public List<JsonReader> getListObjects(@NotNull String path, @NotNull List<JsonReader> def) {
        try {
            JsonElement element = get(path);
            if(element == null) {
                return def;
            }

            JsonArray array = element.getAsJsonArray();
            return array.asList().stream()
                    .filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject)
                    .map(JsonReader::parseObject)
                    .toList();
        } catch (Exception e) {
            return def;
        }
    }

    @Nullable
    public JsonElement get(@NotNull String path) {
        return get(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public JsonElement get(@NotNull String path, @Nullable JsonElement def) {
        JsonObject object = jsonObject;
        try {
            String[] array = path.split("\\.");
            int length = array.length;
            if(length == 1) {
                return object.get(array[0]);
            }

            int index = 0;
            for (String key : array) {
                if(++index >= length) {
                    return object.get(key);
                }

                object = object.getAsJsonObject(key);
            }
        } catch (JsonSyntaxException ignored) { }

        return def;
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
        JsonReader easyJson = parseString(json);
        if(easyJson != null) {
            function.accept(easyJson);
        }
    }

    @NotNull
    public static JsonReader parseObject(@NotNull JsonObject json) {
        return new JsonReader(json);
    }

    @NotNull
    public static <T> JsonArray getArrayString(@NotNull Stream<T> stream, Function<T, String> function) {
        return getArrayString(stream.toList(), function);
    }
    @NotNull
    public static <T> JsonArray getArrayString(@NotNull Collection<T> collect, Function<T, String> function) {
        JsonArray array = new JsonArray();
        collect.forEach(object -> array.add(function.apply(object)));
        return array;
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
