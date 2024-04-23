package me.cyrzu.git.superutils.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class JsonWriter {

    @NotNull
    private final JsonObject json;

    public JsonWriter() {
        this(new JsonObject());
    }

    public JsonWriter(@NotNull JsonObject json) {
        this.json = json;
    }

    public <T> void set(@NotNull String path, @NotNull T value) {
        setPath(path, value);
    }

    private <T> void setPath(@NotNull String path, @NotNull T value) {
        String[] keys = path.split("\\.");
        String key = keys[keys.length - 1];
        JsonObject temp = json;

        for (int i = 0; i < keys.length - 1; i++) {
            if (temp.get(keys[i]) instanceof JsonObject jsonObject) {
                temp = jsonObject;
            } else {
                JsonObject newObj = new JsonObject();
                temp.add(keys[i], newObj);
                temp = newObj;
            }
        }
        if(value instanceof JsonElement value0) {
            temp.add(key, value0);
        } else if(value instanceof String value0) {
            temp.addProperty(key, value0);
        }    else if(value instanceof Number value0) {
            temp.addProperty(key, value0);
        } else if(value instanceof Boolean value0) {
            temp.addProperty(key, value0);
        } else if(value instanceof Character value0) {
            temp.addProperty(key, value0);
        } else {
            temp.addProperty(key, value.toString());
        }
    }

    public JsonObject getCopy() {
        return json.deepCopy();
    }

    public static void modify(@NotNull JsonObject json, @NotNull Consumer<JsonWriter> fun) {
        JsonWriter writer = new JsonWriter(json);
        fun.accept(writer);
    }

    public static <T> JsonArray createArray(@NotNull Collection<T> collection, BiConsumer<JsonArray, T> fun) {
        JsonArray array = new JsonArray();

        for (T value : collection) {
            fun.accept(array, value);
        }

        return array;
    }

}
