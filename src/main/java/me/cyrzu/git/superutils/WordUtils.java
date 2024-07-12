package me.cyrzu.git.superutils;



import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;


public class WordUtils {


    public static String capitalize(@NotNull String str, final char... delimiters) {
        if (str.isEmpty()) {
            return str;
        }
        final Predicate<Integer> isDelimiter = generateIsDelimiterFunction(delimiters);
        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;

        boolean capitalizeNext = true;
        for (int index = 0; index < strLen;) {
            final int codePoint = str.codePointAt(index);

            if (isDelimiter.test(codePoint)) {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            } else if (capitalizeNext) {
                final int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String capitalizeFully(final String str) {
        return capitalizeFully(str, null);
    }


    public static String capitalizeFully(String str, final char... delimiters) {
        if (str.isEmpty()) {
            return str;
        }
        str = str.toLowerCase();
        return capitalize(str, delimiters);
    }



    private static Predicate<Integer> generateIsDelimiterFunction(final char[] delimiters) {
        final Predicate<Integer> isDelimiter;
        if (delimiters == null || delimiters.length == 0) {
            isDelimiter = delimiters == null ? Character::isWhitespace : c -> false;
        } else {
            final Set<Integer> delimiterSet = new HashSet<>();
            for (int index = 0; index < delimiters.length; index++) {
                delimiterSet.add(Character.codePointAt(delimiters, index));
            }
            isDelimiter = delimiterSet::contains;
        }

        return isDelimiter;
    }

}