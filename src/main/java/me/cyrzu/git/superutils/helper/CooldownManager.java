package me.cyrzu.git.superutils.helper;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CooldownManager<T> {

    @NotNull
    private final Map<T, Instant> cooldownMap = new ConcurrentHashMap<>();

    public CooldownManager() {
    }

    public final void setCooldown(@NotNull T value, int time, @NotNull TimeUnit unit) {
        this.setCooldown(value, Duration.ofMillis(unit.toMillis(time)));
    }

    public final void setCooldown(@NotNull T value, @NotNull Duration duration) {
        Instant expirationTime = Instant.now().plus(duration);
        cooldownMap.put(value, expirationTime);
    }

    public final boolean hasCooldown(@NotNull T value) {
        Instant expirationTime = cooldownMap.get(value);
        return expirationTime != null && Instant.now().isBefore(expirationTime);
    }

    public final Duration getRemainingCooldown(@NotNull T value) {
        Instant expirationTime = cooldownMap.get(value);
        if (expirationTime != null) {
            return Duration.between(Instant.now(), expirationTime);
        }

        return Duration.ZERO;
    }

    public boolean checkAndSetCooldown(@NotNull T value, int time, @NotNull TimeUnit unit) {
        return this.checkAndSetCooldown(value, Duration.ofMillis(unit.toMillis(time)));
    }

    public boolean checkAndSetCooldown(@NotNull T value, @NotNull Duration duration) {
        if(this.hasCooldown(value)) {
            return true;
        }

        this.setCooldown(value, duration);
        return false;
    }

    public void addCooldown(@NotNull T value, int time, @NotNull TimeUnit unit) {
        this.addCooldown(value, Duration.ofMillis(unit.toMillis(time)));
    }
    
    public void addCooldown(@NotNull T value, @NotNull Duration duration) {
        if(this.hasCooldown(value)) {
            Instant expirationTime = Instant.now().plus(this.getRemainingCooldown(value).plus(duration));
            this.cooldownMap.put(value, expirationTime);
            return;
        }

        this.setCooldown(value, duration);
    }

    @NotNull
    public Map<T, Instant> getMap() {
        return Map.copyOf(this.cooldownMap);
    }

    @NotNull
    public Set<T> keySet() {
        return this.cooldownMap.keySet();
    }

}