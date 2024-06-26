package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
public class NumberUtils {

    @NotNull
    private final static Random random = new Random();

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


    public static long parseFancyNumber(@NotNull String text) {
        AtomicLong ammount = new AtomicLong();
        try {
            String[] parts = text.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            if(parts.length == 1) {
                return parseLong(parts[0], 0L);
            }

            for (int i = 0; i < parts.length; i += 2) {
                long value = parseLong(parts[i], 0L);
                String unit = parts[i + 1];

                switch (unit) {
                    case "k","t","tho","thousand","thousands" -> ammount.addAndGet(value * 1_000);
                    case "m","mln","milion","milions" -> ammount.addAndGet(value * 1_000_000);
                    case "b","bil","billion","billions" -> ammount.addAndGet(value * 1_000_000_000);
                    default -> ammount.addAndGet(0);
                }
            }

            return ammount.get();
        } catch (Exception ignored) {
            return 0;
        }
    }

    public int randomInteger(int min, int max) {
        return min >= max ? min : random.nextInt(max + 1 - min) + min;
    }

    public boolean chance(double chance) {
        if(chance >= 100D) {
            return true;
        }

        if(chance <= 0D) {
            return false;
        }

        double randomValue = random.nextDouble() * 100;
        return randomValue < chance;
    }

    public double getPercents(double percent, double number) {
        return (percent / 100.0) * number;
    }

    public double getPercents(double percent, double number, int round) {
        return NumberUtils.round((percent / 100.0) * number, round);
    }

}
