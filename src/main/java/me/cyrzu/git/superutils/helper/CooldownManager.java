package me.cyrzu.git.superutils.helper;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    @NotNull
    private final Map<UUID, Instant> cooldownMap = new ConcurrentHashMap<>();

    public CooldownManager() {
    }

    public final void setCooldown(@NotNull UUID uuid, int time, @NotNull TimeUnit unit) {
        setCooldown(uuid, Duration.ofMillis(unit.toMillis(time)));
    }

    public final void setCooldown(@NotNull Player player, int time, @NotNull TimeUnit unit) {
        setCooldown(player.getUniqueId(), time, unit);
    }

    protected final void setCooldown(@NotNull UUID uuid, @NotNull Duration duration) {
        Instant expirationTime = Instant.now().plus(duration);
        cooldownMap.put(uuid, expirationTime);
    }

    protected final void setCooldown(@NotNull Player player, @NotNull Duration duration) {
        setCooldown(player.getUniqueId(), duration);
    }

    public final boolean hasCooldown(@NotNull UUID uuid) {
        Instant expirationTime = cooldownMap.get(uuid);
        return expirationTime != null && Instant.now().isBefore(expirationTime);
    }

    public final boolean hasCooldown(@NotNull Player player) {
        return hasCooldown(player.getUniqueId());
    }

    public final Duration getRemainingCooldown(@NotNull UUID uuid) {
        Instant expirationTime = cooldownMap.get(uuid);
        if (expirationTime != null) {
            return Duration.between(Instant.now(), expirationTime);
        }

        return Duration.ZERO;
    }

    public final Duration getRemainingCooldown(@NotNull Player player) {
        return getRemainingCooldown(player.getUniqueId());
    }

}