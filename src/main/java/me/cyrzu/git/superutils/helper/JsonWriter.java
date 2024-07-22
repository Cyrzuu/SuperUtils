package me.cyrzu.git.superutils.helper;

import com.google.gson.*;
import me.cyrzu.git.superutils.FileUtils;
import me.cyrzu.git.superutils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public <T> JsonWriter set(@NotNull String path, @NotNull T value) {
        this.setPath(path, value);
        return this;
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
        } else if(value instanceof Location location) {
            temp.add(key, LocationUtils.serializeJsonObject(location, 3, false));
        } else if(value instanceof Vector vector) {
            temp.add(key, new JsonWriter()
                    .set("x", vector.getX())
                    .set("y", vector.getY())
                    .set("z", vector.getZ()).getCopy());
        } else if(value instanceof Bound bound) {
            temp.add(key, new JsonWriter()
                    .set("min", bound.getMinVector())
                    .set("max", bound.getMaxVector())
                    .getCopy());
        }
        else {
            temp.addProperty(key, value.toString());
        }
    }

    public JsonObject getCopy() {
        return json.deepCopy();
    }

    @Override
    public String toString() {
        return json.deepCopy().toString();
    }

    public void saveToFile(@NotNull File file) {
        this.saveToFile(file, false, false);
    }

    public void saveToFile(@NotNull File file, boolean pretty) {
        this.saveToFile(file, pretty, false);
    }

    public void saveToFile(@NotNull File file, boolean pretty, boolean append) {
        FileUtils.createFile(file);
        try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8)) {
            Gson gson = pretty ? new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create() : new GsonBuilder().disableHtmlEscaping().create();
            gson.toJson(this.getCopy(), outputStreamWriter);
        } catch (IOException ignore) {
        }
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

    public static void saveArrayToFile(@NotNull File file, @NotNull JsonArray array) {
        JsonWriter.saveArrayToFile(file, array, false, false);
    }

    public static void saveArrayToFile(@NotNull File file, @NotNull JsonArray array, boolean pretty) {
        JsonWriter.saveArrayToFile(file, array, pretty, false);
    }

    public static void saveArrayToFile(@NotNull File file, @NotNull JsonArray array, boolean pretty, boolean append) {
        FileUtils.createFile(file);
        try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8)) {
            Gson gson = pretty ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
            gson.toJson(array, outputStreamWriter);
        } catch (IOException ignore) {
        }
    }

}
