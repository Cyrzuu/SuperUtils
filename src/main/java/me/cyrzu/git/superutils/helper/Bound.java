package me.cyrzu.git.superutils.helper;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Getter
public class Bound {

    @NotNull
    public final static Bound ZERO_BOUND = new Bound(0, 0, 0, 0, 0, 0);

    private final int minX;
    private final int minY;
    private final int minZ;

    private final int maxX;
    private final int maxY;
    private final int maxZ;

    public Bound(@NotNull Location min, @NotNull Location max) {
        this(min.toVector(), max.toVector());
    }

    public Bound(@NotNull Vector min, @NotNull Vector max) {
        this(min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());
    }

    public Bound(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public Vector getMinVector() {
        return new Vector(minX, minY, minZ);
    }

    public Vector getMaxVector() {
        return new Vector(maxX, maxY, maxZ);
    }

    public boolean contains(@NotNull Player player) {
        return this.contains(player.getLocation());
    }

    public boolean contains(Block block) {
        return this.contains(block.getX(), block.getY(), block.getZ());
    }

    public boolean contains(@NotNull Location location) {
        return this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean contains(int x, int y, int z) {
        return (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ);
    }

    public int sizeX() {
        return (this.maxX - this.minX) + 1;
    }

    public int sizeZ() {
        return (this.maxZ - this.minZ) + 1;
    }

    public int sizeY() {
        return (this.maxY - this.minY) + 1;
    }

    @Override
    public String toString() {
        return "Bound{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", minZ=" + minZ +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", maxZ=" + maxZ +
                '}';
    }

    public static Bound of(@NotNull Location min, @NotNull Location max) {
        return new Bound(min, max);
    }

    public static Bound of(@NotNull Vector min, @NotNull Vector max) {
        return new Bound(min, max);
    }

}