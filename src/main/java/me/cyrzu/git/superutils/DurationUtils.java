package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

@UtilityClass
public class DurationUtils {

    @NotNull
    public static Duration formatToDuration(String text) {
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

}
