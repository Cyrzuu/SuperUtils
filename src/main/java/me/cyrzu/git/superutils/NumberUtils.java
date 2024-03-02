package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class NumberUtils {

    public static double round(double value, int scale) {
        if(value <= 0) {
            return Math.round(value);
        }

        BigDecimal decimal = BigDecimal.valueOf(value);
        return decimal.setScale(scale, RoundingMode.FLOOR).doubleValue();
    }

    public static boolean doubleValue(@NotNull String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    public static double parseDouble(@NotNull String value) {
        return parseDouble(value, 0D);
    }

    public static double parseDouble(@NotNull String value, double def) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return def;
        }
    }

    public static boolean integerValue(@NotNull String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    public static int parseInteger(@NotNull String value) {
        return parseInteger(value, 0);
    }

    public static int parseInteger(@NotNull String value, int def) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return def;
        }
    }

    public static boolean longValue(@NotNull String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException ignore) {
            return false;
        }
    }

    public static long parseLong(@NotNull String value) {
        return parseLong(value, 0L);
    }

    public static long parseLong(@NotNull String value, long def) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }


}
