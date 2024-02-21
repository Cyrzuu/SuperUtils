package me.cyrzu.git.superutils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockPos {

    @Getter
    private final int x;

    @Getter
    private final int y;

    @Getter
    private final int z;

    @Getter
    @NotNull
    private final String world;

    public BlockPos(int x, int y, int z, @NotNull World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world.getName();
    }

    public BlockPos(@NotNull Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.world = block.getWorld().getName();
    }

    public BlockPos(@NotNull Location location) {
        World world = location.getWorld();
        if(world == null)
            throw new RuntimeException("World cannot be null");

        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = world.getName();
    }

    public BlockPos(@NotNull Location location, @NotNull World world) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = world.getName();
    }

    @NotNull
    public World getBukkitWorld() {
        World world = Bukkit.getWorld(this.world);
        if(world == null)
            throw new RuntimeException("No world with this name has been found");

        return world;
    }

    @NotNull
    public Location toLocation() {
        return new Location(getBukkitWorld(), x, y, z);
    }

    @NotNull
    public Location toCenterLocation() {
        return toLocation().add(0.5, 0, 0.5);
    }

    @NotNull
    public WorldChunk getWorldChunk() {
        return new WorldChunk(x >> 4, z >> 4, world);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockPos blockPos)) return false;
        return x == blockPos.x && y == blockPos.y && z == blockPos.z && world.equals(blockPos.world);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + world.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BlockPos{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

}
