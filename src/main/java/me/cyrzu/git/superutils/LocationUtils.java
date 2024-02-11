package me.cyrzu.git.superutils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class LocationUtils {

    @NotNull
    public static String serialize(@NotNull Location location) {
        if(location.getWorld() == null) {
            throw new RuntimeException("The location's world is undefined");
        }

        JsonObject object = new JsonObject();
        object.addProperty("world", location.getWorld().getName());
        object.addProperty("x", NumberUtils.round(location.getX(), 4));
        object.addProperty("y", NumberUtils.round(location.getY(), 4));
        object.addProperty("z", NumberUtils.round(location.getZ(), 4));
        object.addProperty("yaw", NumberUtils.round(location.getYaw(), 2));
        object.addProperty("pitch", NumberUtils.round(location.getPitch(), 2));

        return object.toString();
    }

    @NotNull
    public static String serializeBlock(@NotNull Location location) {
        if(location.getWorld() == null) {
            throw new RuntimeException("The location's world is undefined");
        }

        JsonObject object = new JsonObject();
        object.addProperty("world", location.getWorld().getName());
        object.addProperty("x", location.getBlockX());
        object.addProperty("y", location.getBlockX());
        object.addProperty("z", location.getBlockX());

        return object.toString();
    }

    @NotNull
    public static Location deserialize(@Nullable String text) {
        if(text == null) {
            throw new RuntimeException("The string value must not be null");
        }

        JsonObject element = JsonParser.parseString(text).getAsJsonObject();
        String world = element.get("world").getAsString();
        if(Bukkit.getWorld(world) == null) {
            throw new RuntimeException("There is no such world with that name");
        }

        return new Location(
                Bukkit.getWorld(element.get("world").getAsString()),
                element.get("x").getAsDouble(),
                element.get("y").getAsDouble(),
                element.get("z").getAsDouble(),
                element.get("yaw").getAsFloat(),
                element.get("pitch").getAsFloat()
        );
    }

    @NotNull
    public static Location deserializeBlock(@Nullable String text) {
        if(text == null) {
            throw new RuntimeException("The string value must not be null");
        }

        JsonObject element = JsonParser.parseString(text).getAsJsonObject();
        String world = element.get("world").getAsString();
        if(Bukkit.getWorld(world) == null) {
            throw new RuntimeException("There is no such world with that name");
        }

        return new Location(
                Bukkit.getWorld(element.get("world").getAsString()),
                element.get("x").getAsInt(),
                element.get("y").getAsInt(),
                element.get("z").getAsInt()
        );
    }

}
