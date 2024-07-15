package me.cyrzu.git.superutils.helper;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CooldownManager<T> {

    @NotNull
    private final Map<T, Long> cooldownMap = new ConcurrentHashMap<>();

    public CooldownManager() {
    }

    public final void setCooldown(@NotNull T value, int time, @NotNull TimeUnit unit) {
        this.setCooldown(value, Duration.ofMillis(unit.toMillis(time)));
    }

    public final void setCooldown(@NotNull T value, @NotNull Duration duration) {
        long expirationTime = System.nanoTime() + duration.toNanos();
        cooldownMap.put(value, expirationTime);
    }

    public final boolean hasCooldown(@NotNull T value) {
        Long expirationTime = cooldownMap.get(value);
        return expirationTime != null && System.nanoTime() < expirationTime;
    }

    public final Duration getRemainingCooldown(@NotNull T value) {
        Long expirationTime = cooldownMap.get(value);
        if (expirationTime != null) {
            return Duration.ofNanos(expirationTime - System.nanoTime());
        }
        return Duration.ZERO;
    }

    public boolean checkAndSetCooldown(@NotNull T value, Number time, @NotNull TimeUnit unit) {
        return this.checkAndSetCooldown(value, Duration.ofMillis(unit.toMillis(time.longValue())));
    }

    public boolean checkAndSetCooldown(@NotNull T value, long time, @NotNull TimeUnit unit) {
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
            long expirationTime = System.nanoTime() + this.getRemainingCooldown(value).toNanos() + duration.toNanos();
            this.cooldownMap.put(value, expirationTime);
            return;
        }
        this.setCooldown(value, duration);
    }

    public boolean contains(@NotNull T value) {
        return this.cooldownMap.containsKey(value);
    }

    public void removeCooldown(@NotNull T value) {
        this.cooldownMap.remove(value);
    }

    public void removeCooldownIf(@NotNull Predicate<T> filter) {
        Set<T> ts = this.keySet();
        ts.forEach(key -> {
            if(!filter.test(key)) {
                return;
            }
            this.cooldownMap.remove(key);
        });
    }

    @NotNull
    public Map<T, Long> getMap() {
        return Map.copyOf(this.cooldownMap);
    }

    @NotNull
    public Set<T> keySet() {
        return this.cooldownMap.keySet();
    }

    @NotNull
    public Set<T> activeKeySet() {
        return this.cooldownMap.keySet().stream()
                .filter(this::hasCooldown)
                .collect(Collectors.toSet());
    }

}