package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class EnumUtils {

    @Nullable
    public <T extends Enum<T>> T getEnum(@NotNull String value, final Class<T> clazz) {
        return getEnum(value, clazz, null);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public <T extends Enum<T>> T getEnum(@NotNull String value, final Class<T> clazz, final T def) {
        try {
            return Enum.valueOf(clazz, value.toUpperCase());
        } catch (final Exception exception) {
            return def;
        }
    }

    @NotNull
    public String capitalize(@NotNull Enum<?> value) {
        String name = value.name();
        return StringUtils.capitalizeFully(name.replace("_", " "));
    }

}
