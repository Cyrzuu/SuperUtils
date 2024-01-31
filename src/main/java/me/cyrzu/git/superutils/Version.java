package me.cyrzu.git.superutils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum Version {

    v1_19_R3("1.19.4"),
    v1_20_R1("1.20.1"),
    v1_20_R2("1.20.2"),
    v1_20_R3("1.20.4"),
    UNKNOWN("Unknown");

    @Getter
    @NotNull
    private final String version;

    @Getter
    @NotNull
    private static final Version current = Version.getCurrentVersion();

    Version(@NotNull String string) {
        this.version = string;
    }

    public static boolean isAtLeast(@NotNull Version version) {
        return version.isCurrent() || getCurrentVersion().isHigher(version);
    }

    public static boolean isAbove(@NotNull Version version) {
        return getCurrentVersion().isHigher(version);
    }

    public boolean isHigher(@NotNull Version version) {
        return this.ordinal() > version.ordinal();
    }

    public boolean isLower(@NotNull Version version) {
        return this.ordinal() < version.ordinal();
    }

    public boolean isCurrent() {
        return this.equals(current);
    }

    @NotNull
    private static Version getCurrentVersion() {
        String[] bukkitPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        String protocol = bukkitPackage[bukkitPackage.length - 1];

        return Arrays.stream(Version.values())
                .filter(v -> v.name().equals(protocol))
                .findAny()
                .orElse(UNKNOWN);
    }

}
