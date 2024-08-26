package me.cyrzu.git.superutils.helper;

import lombok.Getter;
import me.cyrzu.git.superutils.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


public class BridgeResult {

    @Getter
    private final Map<String, ?> bridge;

    public BridgeResult(Map<?, ?> bridge) {
        this.bridge = BridgeResult.parseMap(bridge);
    }

    public boolean isEmpty() {
        return bridge.isEmpty();
    }

    public Optional<String> getString(String key) {
        Object object = this.getObject(key);
        return object != null ? Optional.of(object.toString()) : Optional.empty();
    }

    public Optional<Integer> getInt(String key) {
        return this.getObject(key, Integer.class);
    }

    public Optional<Long> getLong(String key) {
        Object value = this.getObject(key);

        if (value != null) {
            if (value instanceof Long) {
                return Optional.of((long) value);
            } else if (value instanceof Integer) {
                return Optional.of((long) (int) value);
            }
        }

        return Optional.empty();
    }

    public Optional<Double> getDouble(String key) {
        return this.getObject(key) instanceof Number number ? Optional.of(number.doubleValue()) : Optional.empty();
    }

    public Optional<Boolean> getBoolean(String key) {
        Object object = this.getObject(key);
        if(object instanceof Number value) {
            return Optional.of(value.intValue() == 1);
        } else if(object instanceof Boolean value) {
            return Optional.of(value);
        }

        return Optional.empty();
    }

    public Optional<Byte> getByte(String key) {
        Object value = this.getObject(key);

        if (value != null) {
            if (value instanceof Byte) {
                return Optional.of((byte) value);
            } else if (value instanceof Boolean) {
                return Optional.of((Boolean) value ? (byte) 1 : 0);
            } else if (value instanceof Integer) {
                return Optional.of((byte) (int) value);
            }
        }

        return Optional.empty();
    }

    public Optional<BigDecimal> getBigDecimal(String key) {
        Optional<String> value = this.getString(key);
        try {
            return value.map(BigDecimal::new);
        } catch (NumberFormatException | NullPointerException ex) {
            return Optional.empty();
        }
    }

    public Optional<UUID> getUUID(String key) {
        Optional<String> value = this.getString(key);
        if (value.isEmpty())
            return Optional.empty();

        try {
            return Optional.of(UUID.fromString(value.get()));
        } catch (IllegalArgumentException error) {
            return Optional.empty();
        }
    }

    public Optional<byte[]> getBlob(String key) {
        return this.getObject(key, byte[].class);
    }

    public Optional<World> getWorld(String key) {
        Optional<String> value = this.getString(key);

        if (value.isEmpty()) {
            return Optional.empty();
        }

        World world = Bukkit.getWorld(value.get());
        return world != null ? Optional.of(world) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(final String key, final Class<T> clazz) {
        if(this.getObject(key) instanceof List<?> list) {

            return (List<T>) list;
        }

        return Collections.emptyList();
    }

    @Contract("_, _, !null -> !null")
    public <T extends Enum<T>> T getEnum(@NotNull String key, @NotNull Class<T> enumType, @Nullable T def) {
        return this.getEnum(key, enumType).orElse(def);
    }

    public <T extends Enum<T>> Optional<T> getEnum(@NotNull String key, @NotNull Class<T> enumType) {
        Optional<String> value = this.getString(key);
        if(value.isEmpty()) {
            return Optional.empty();
        }


        T anEnum = EnumUtils.getEnum(value.get(), enumType);
        return anEnum != null ? Optional.of(anEnum) : Optional.empty();
    }

    @NotNull
    public List<BridgeResult> getResultList(String key) {
        Object object = this.getObject(key);
        if(object instanceof List<?> list) {
            return list.stream()
                .map(BridgeResult::parseObject)
                .map(BridgeResult::new).toList();
        }

        return Collections.emptyList();
    }

    @NotNull
    public BridgeResult getResult(String key) {
        Object object = this.getObject(key);
        if(object != null) {
            Map<String, ?> stringMap = BridgeResult.parseObject(object);
            return new BridgeResult(stringMap);
        }

        return EMPTY_RESULT;
    }

    private <T> Optional<T> getObject(String key, Class<T> clazz) {
        Object value = this.getObject(key);
        return Optional.ofNullable(value == null || !value.getClass().isAssignableFrom(clazz) ? null : clazz.cast(value));
    }

    @Nullable
    private Object getObject(@NotNull String key) {
        String[] path = key.split("\\.");
        if(path.length < 2) {
            return bridge.get(key);
        }

        Object current = bridge.get(path[0]);
        for (int i = 1; i < path.length; i++) {
            if(current instanceof Map<?, ?> document) {
                current = document.get(path[i]);
                continue;
            }

            return null;
        }

        return current;
    }

    @Override
    public String toString() {
        return this.bridge.toString();
    }

    @NotNull
    public static Map<String, ?> parseObject(@NotNull Object object) {
        if(!(object instanceof Map<?,?> map)) {
            return Collections.emptyMap();
        }

        return BridgeResult.parseMap(map);
    }

    @NotNull
    public static Map<String, ?> parseMap(@NotNull Map<?, ?> map) {
        return map.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof String)
                .collect(Collectors.toMap(
                        entry -> (String) entry.getKey(),
                        Map.Entry::getValue,
                        (a, b) -> b, LinkedHashMap::new)
                );
    }

    @NotNull
    private final static BridgeResult EMPTY_RESULT = new BridgeResult(Collections.emptyMap());

    @NotNull
    public static BridgeResult of(@Nullable Map<?, ?> map) {
        return new BridgeResult(map);
    }

}
