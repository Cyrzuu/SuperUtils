package me.cyrzu.git.superutils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WorldChunk {

    @Getter
    private final int x;

    @Getter
    private final int z;

    @Getter
    @NotNull
    private final String world;

    public WorldChunk(int x, int z, @NotNull String world) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public WorldChunk(@NotNull Chunk chunk) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.world = chunk.getWorld().getName();
    }

    public WorldChunk(@NotNull BlockPos blockPos) {
        this.x = blockPos.getX() >> 4;
        this.z = blockPos.getZ() >> 4;
        this.world = blockPos.getBukkitWorld().getName();
    }

    @NotNull
    public World getBukkitWorld() {
        World world = Bukkit.getWorld(this.world);
        if(world == null)
            throw new RuntimeException("Cannot find a world with the name: %s. Ensure the world exists or use the correct name.".formatted(this.world));

        return world;
    }

    @NotNull
    public Chunk getBukkitChunk() {
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

}
