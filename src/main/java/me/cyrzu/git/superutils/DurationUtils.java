package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

@UtilityClass
public class DurationUtils {

    @NotNull
    public Duration formatToDuration(String text) {
        try {
            Duration duration = Duration.ZERO;
            String[] parts = text.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            for (int i = 0; i < parts.length; i += 2) {
                long value = Long.parseLong(parts[i]);
                String unit = parts[i + 1];

                duration = switch (unit) {
                    case "d","day","days" -> duration.plusDays(value);
                    case "h","hour","hours" -> duration.plusHours(value);
                    case "m","min","minute","minutes" -> duration.plusMinutes(value);
                    case "s","sec","secound","secounds" -> duration.plusSeconds(value);
                    default -> duration.plusMillis(0);
                };
            }

            return duration;
        } catch (Exception ignore) {
            return Duration.ZERO;
        }
    }

    public static String formatToString(@NotNull Duration duration) {
        return DurationUtils.formatToString(duration.toSeconds());
    }

    @NotNull
    public static String formatToString(long time) {
        long seconds = Math.abs(time);

        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (days > 0) {
            return String.format("%dd, %dh, %dm, %ds", days, hours, minutes, secs);
        }

        if (hours > 0) {
            return String.format("%dh, %dm, %ds", hours, minutes, secs);
        }

        if (minutes > 0) {
            return String.format("%dm, %ds", minutes, secs);
        }

        return String.format("%ds", secs);
    }

}
