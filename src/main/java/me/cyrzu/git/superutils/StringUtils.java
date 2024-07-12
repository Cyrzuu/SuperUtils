package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@UtilityClass
public class StringUtils {

    private static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    @Nullable
    public String compressToBase64(@NotNull String text) {
        return StringUtils.compressToBase64(text, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String compressToBase64(@NotNull String text, @Nullable String def) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(text.getBytes(CHARSET_UTF8));
            return Base64.getEncoder().encodeToString(byteStream.toByteArray());
        } catch (IOException e) {
            return def;
        }
    }

    @Nullable
    public String decodeFromBase64(@NotNull String text) {
        return StringUtils.decodeFromBase64(text, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String decodeFromBase64(@NotNull String text, @Nullable String def) {
        byte[] decodedBytes = Base64.getDecoder().decode(text);
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(decodedBytes);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteInputStream);
             ByteArrayOutputStream decompressedStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                decompressedStream.write(buffer, 0, len);
            }

            return decompressedStream.toString();
        } catch (IOException e) {
            return def;
        }
    }

    @Nullable
    public UUID toUUID(@Nullable String text) {
        if(text == null) {
            return null;
        }
        String capitalize = WordUtils.capitalize("");
        try {
            return UUID.fromString(text);
        } catch (Exception e) {
            return null;
        }
    }

    public static String capitalize(@NotNull String str) {
        return StringUtils.capitalize(str, new char[0]);
    }

    public static String capitalize(@NotNull String str, char... delimiters) {
        if (str.isEmpty()) {
            return str;
        }
        final Predicate<Integer> isDelimiter = StringUtils.generateIsDelimiterFunction(delimiters);
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

    public static String capitalizeFully(@NotNull String str) {
        return StringUtils.capitalizeFully(str, new char[0]);
    }

    public static String capitalizeFully(@NotNull String str, char... delimiters) {
        if (str.isEmpty()) {
            return str;
        }
        str = str.toLowerCase();
        return StringUtils.capitalize(str, delimiters);
    }

    @NotNull
    private Predicate<Integer> generateIsDelimiterFunction(char[] delimiters) {
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
