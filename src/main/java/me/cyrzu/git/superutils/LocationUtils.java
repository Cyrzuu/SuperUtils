package me.cyrzu.git.superutils;

import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils.helper.JsonReader;
import me.cyrzu.git.superutils.helper.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class LocationUtils {

    @NotNull
    public static String serialize(@NotNull Location location) {
        return LocationUtils.serialize(location, 3, false);
    }

    @Nullable
    @Contract("_, true -> null; _, false -> !null")
    public static String serialize(@NotNull Location location, boolean needWorld) {
        return LocationUtils.serialize(location, 3, needWorld);
    }

    @NotNull
    public static String serialize(@NotNull Location location, int round) {
        return LocationUtils.serialize(location, round, false);
    }

    @Nullable
    @Contract("_, _, true -> null; _, _, false -> !null")
    public static String serialize(@NotNull Location location, int round, boolean needWorld) {
        World world = location.getWorld();
        if(world == null && needWorld) {
            return null;
        }

        JsonObject object = new JsonObject();
        object.addProperty("world", world == null ? "world" : world.getName());
        object.addProperty("x", NumberUtils.round(location.getX(), round));
        object.addProperty("y", NumberUtils.round(location.getY(), round));
        object.addProperty("z", NumberUtils.round(location.getZ(), round));
        object.addProperty("yaw", NumberUtils.round(location.getYaw(), 2));
        object.addProperty("pitch", NumberUtils.round(location.getPitch(), 2));

        return object.toString();
    }

    @Nullable
    @Contract("_, _, true -> null; _, _, false -> !null")
    public static JsonObject serializeJsonObject(@NotNull Location location, int round, boolean needWorld) {
        World world = location.getWorld();
        if(world == null && needWorld) {
            return null;
        }

        return new JsonWriter()
                .set("world", world == null ? "world" : world.getName())
                .set("x", location.getX())
                .set("y", location.getY())
                .set("z", location.getZ())
                .set("yaw", location.getYaw())
                .set("pitch", location.getPitch())
                .getCopy();
    }

    @Nullable
    public static String serializeBlock(@NotNull Location location) {
        return LocationUtils.serializeBlock(location, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static String serializeBlock(@NotNull Location location, @Nullable String def) {
        World world = location.getWorld();
        if(world == null) {
            return def;
        }

        JsonObject object = new JsonObject();
        object.addProperty("world", world.getName());
        object.addProperty("x", location.getBlockX());
        object.addProperty("y", location.getBlockY());
        object.addProperty("z", location.getBlockZ());

        return object.toString();
    }

    @Nullable
    public static Location deserialize(@Nullable String text) {
        return LocationUtils.deserialize(text, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static Location deserialize(@Nullable String text, @Nullable Location def) {
        JsonReader reader = JsonReader.parseString(text == null ? "" : text);
        if(reader == null) {
            return def;
        }

        World world = Bukkit.getWorld(reader.getString("world", "world"));
        if(world == null) {
            return def;
        }

        return new Location(
            world,
            reader.getDouble("x", 0),
            reader.getDouble("y", 0),
            reader.getDouble("z", 0),
            (float) reader.getDouble("yaw", 0),
            (float) reader.getDouble("pitch", 0)
        );
    }

    @Nullable
    public static Location deserializeBlock(@Nullable String text) {
        return LocationUtils.deserializeBlock(text, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static Location deserializeBlock(@Nullable String text, @Nullable Location def) {
        JsonReader reader = JsonReader.parseString(text == null ? "" : text);
        if(reader == null) {
            return def;
        }

        World world = Bukkit.getWorld(reader.getString("world", "world"));
        if(world == null) {
            return def;
        }

        return new Location(
                world,
                reader.getInt("x", 0),
                reader.getInt("y", 0),
                reader.getInt("z", 0)
        );
    }

}
