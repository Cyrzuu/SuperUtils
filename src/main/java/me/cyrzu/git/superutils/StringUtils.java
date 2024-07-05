package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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

}
