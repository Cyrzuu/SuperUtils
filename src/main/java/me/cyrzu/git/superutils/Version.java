package me.cyrzu.git.superutils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Version {
    v1_16_R3("1.16.4/1.16.5"),
    v1_17_R1("1.17/1.17.1"),
    v1_18_R1("1.18/1.18.1"),
    v1_18_R2("1.18.2"),
    v1_19_R1("1.19/1.19.1/1.19.2"),
    v1_19_R2("1.19.3"),
    v1_19_R3("1.19.4"),
    v1_20_R1("1.20/1.20.1"),
    v1_20_R2("1.20.2"),
    v1_20_R3("1.20.3/1.20.4"),
    v1_20_R4("1.20.5/1.20.6"),
    v1_21_R1("1.21"),
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

//    @NotNull
//    private static Version getCurrentVersion() {
//        String[] bukkitPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
//        String protocol = bukkitPackage[bukkitPackage.length - 1];
//
//        return Arrays.stream(Version.values())
//                .filter(v -> v.name().equals(protocol))
//                .findAny()
//                .orElse(UNKNOWN);
//    }

    @NotNull
    private static Version getCurrentVersion() {
        String bukkitGetVersionOutput = Bukkit.getVersion();
        Matcher matcher = Pattern.compile("\\(MC: (?<version>[\\d]+\\.[\\d]+(\\.[\\d]+)?)\\)").matcher(bukkitGetVersionOutput);
        if (matcher.find()) {
            return switch (matcher.group("version")) {
                case "1.16.4", "1.16.5" -> Version.v1_16_R3;
                case "1.17", "1.17.1" -> Version.v1_17_R1;
                case "1.18", "1.18.1" -> Version.v1_18_R1;
                case "1.18.2" -> Version.v1_18_R2;
                case "1.19", "1.19.1", "1.19.2" -> Version.v1_19_R1;
                case "1.19.3" -> Version.v1_19_R2;
                case "1.19.4" -> Version.v1_19_R3;
                case "1.20", "1.20.1" -> Version.v1_20_R1;
                case "1.20.2" -> Version.v1_20_R2;
                case "1.20.3", "1.20.4" -> Version.v1_20_R3;
                case "1.20.5", "1.20.6" -> Version.v1_20_R4;
                case "1.21" -> Version.v1_21_R1;
                default -> UNKNOWN;
            };
        } else {
            throw new RuntimeException("Could not determine Minecraft version from Bukkit.getVersion(): " + bukkitGetVersionOutput);
        }
    }

}
