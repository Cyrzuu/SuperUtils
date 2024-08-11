package me.cyrzu.git.superutils;

import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;
import me.cyrzu.git.superutils.helper.JsonReader;
import me.cyrzu.git.superutils.helper.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.NumberConversions;
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
    @Contract("_, false -> !null")
    public static String serialize(@NotNull Location location, boolean needWorld) {
        return LocationUtils.serialize(location, 3, needWorld);
    }

    @NotNull
    public static String serialize(@NotNull Location location, int round) {
        return LocationUtils.serialize(location, round, false);
    }

    @Nullable
    @Contract("_, _, false -> !null")
    public static String serialize(@NotNull Location location, int round, boolean needWorld) {
        JsonObject object = LocationUtils.serializeJsonObject(location, round, needWorld);
        return object != null ? object.toString() : null;
    }

    @Nullable
    @Contract("_, _, false -> !null")
    public static JsonObject serializeJsonObject(@NotNull Location location, int round, boolean needWorld) {
        World world = location.getWorld();
        if(world == null && needWorld) {
            return null;
        }

        return new JsonWriter()
                .set("world", world == null ? "world" : world.getName())
                .set("x", NumberUtils.round(location.getX(), round))
                .set("y", NumberUtils.round(location.getY(), round))
                .set("z", NumberUtils.round(location.getZ(), round))
                .set("yaw", NumberUtils.round(location.getYaw(), 2))
                .set("pitch", NumberUtils.round(location.getPitch(), 2))
                .getCopy();
    }

    @NotNull
    public static String serializeNoWorld(@NotNull Location location, int round) {
        return LocationUtils.serializeNoWorldJsonObject(location, round).toString();
    }

    @NotNull
    public static JsonObject serializeNoWorldJsonObject(@NotNull Location location, int round) {
        return new JsonWriter()
                .set("x", NumberUtils.round(location.getX(), round))
                .set("y", NumberUtils.round(location.getY(), round))
                .set("z", NumberUtils.round(location.getZ(), round))
                .set("yaw", NumberUtils.round(location.getYaw(), 2))
                .set("pitch", NumberUtils.round(location.getPitch(), 2))
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
    public static JsonObject serializeBlockJsonObject(@NotNull Location location) {
        return LocationUtils.serializeBlockJsonObject(location, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static JsonObject serializeBlockJsonObject(@NotNull Location location, @Nullable JsonObject def) {
        World world = location.getWorld();
        if(world == null) {
            return def;
        }

        return new JsonWriter()
                .set("world", world.getName())
                .set("x", location.getBlockX())
                .set("y", location.getBlockY())
                .set("z", location.getBlockZ())
                .getCopy();
    }

    @NotNull
    public static String serializeBlockNoWorld(@NotNull Location location) {
        return LocationUtils.serializeBlockNoWorldJsonObject(location).toString();
    }

    @NotNull
    public static JsonObject serializeBlockNoWorldJsonObject(@NotNull Location location) {
        return new JsonWriter()
            .set("x", location.getBlockX())
            .set("y", location.getBlockY())
            .set("z", location.getBlockZ())
            .getCopy();
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

        String worldName = reader.getString("world");
        World world = worldName != null ? Bukkit.getWorld(worldName) : null;
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
    public static Location deserializeNoWorld(@Nullable String text) {
        return LocationUtils.deserializeNoWorld(text, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static Location deserializeNoWorld(@Nullable String text, @Nullable Location def) {
        JsonReader reader = JsonReader.parseString(text == null ? "" : text);
        if(reader == null) {
            return def;
        }

        return new Location(
                null,
                reader.getDouble("x"),
                reader.getDouble("y"),
                reader.getDouble("z"),
                (float) reader.getDouble("yaw"),
                (float) reader.getDouble("pitch")
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

    @Nullable
    public static Location deserializeBlockNoWorld(@Nullable String text) {
        return LocationUtils.deserializeBlockNoWorld(text, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static Location deserializeBlockNoWorld(@Nullable String text, @Nullable Location def) {
        JsonReader reader = JsonReader.parseString(text == null ? "" : text);
        if(reader == null) {
            return def;
        }

        return new Location(
                null,
                reader.getInt("x"),
                reader.getInt("y"),
                reader.getInt("z")
        );
    }

    public double distance(@Nullable Location location, @Nullable Location secondLocation) {
        return LocationUtils.distance(location, secondLocation, false);
    }

    public double distance(@Nullable Location location, @Nullable Location secondLocation, boolean worldEqual) {
        return Math.sqrt(LocationUtils.distanceSquared(location, secondLocation, worldEqual));
    }


    public double distanceSquared(@Nullable Location location, @Nullable Location secondLocation) {
        return LocationUtils.distanceSquared(location, secondLocation, false);
    }

    public double distanceSquared(@Nullable Location location, @Nullable Location secondLocation, boolean worldEqual) {
        if(location == null || secondLocation == null) {
            return Double.MAX_VALUE;
        }

        World world = location.getWorld();
        World secondWorld = secondLocation.getWorld();
        if(worldEqual && (world == null || secondWorld == null || !world.getUID().equals(secondWorld.getUID()))) {
            return Double.MAX_VALUE;
        }

        return NumberConversions.square(location.getX() - secondLocation.getX()) +
                NumberConversions.square(location.getY() - secondLocation.getY()) +
                NumberConversions.square(location.getZ() - secondLocation.getZ());
    }

}
