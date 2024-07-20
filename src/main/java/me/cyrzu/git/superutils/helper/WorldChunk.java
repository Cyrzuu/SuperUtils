package me.cyrzu.git.superutils.helper;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class WorldChunk {

    private final int x;

    private final int z;

    @NotNull
    private final String world;

    public WorldChunk(@NotNull Location location) {
        this(location, Objects.requireNonNull(location.getWorld()));
    }

    public WorldChunk(@NotNull Location location, @NotNull World world) {
        this(world.getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public WorldChunk(@NotNull Block block) {
        this(block.getWorld().getName(), block.getX() >> 4, block.getZ() >> 4);
    }

    public WorldChunk(@NotNull Chunk chunk) {
        this(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public WorldChunk(@NotNull String world, int x, int z) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    @NotNull
    public World getBukkitWorld() {
        World world = Bukkit.getWorld(this.world);
        Objects.requireNonNull(world);

        return world;
    }

    @NotNull
    public Chunk toBukkitChunk() {
        return this.getBukkitWorld().getChunkAt(x, z);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WorldChunk worldChunk)) return false;
        return x == worldChunk.x && z == worldChunk.z && world.equals(worldChunk.world);
    }

    @Override
    public final int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + z;
        result = 31 * result + Objects.hashCode(world);
        return result;
    }

    @Override
    public String toString() {
        return "WorldChunk{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", z=" + z +
                '}';
    }

    private static final Map<String, Map<Integer, Map<Integer, WorldChunk>>> cache = new HashMap<>();

    @NotNull
    public static WorldChunk of(@NotNull Location location) {
        World world = Objects.requireNonNull(location.getWorld());
        return WorldChunk.of(world.getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @NotNull
    public static WorldChunk of(@NotNull Block block) {
        return WorldChunk.of(block.getWorld().getName(), block.getX() >> 4, block.getZ() >> 4);
    }

    @NotNull
    public static WorldChunk of(@NotNull String world, int x, int z) {
        return new WorldChunk(world, x, z);
    }

}