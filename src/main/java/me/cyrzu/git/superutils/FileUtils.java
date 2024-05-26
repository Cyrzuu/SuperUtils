package me.cyrzu.git.superutils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class FileUtils {

    @Getter
    @NotNull
    private final FilenameFilter jsonFilter = (dir, name) -> name.endsWith(".json");

    @Getter
    @NotNull
    private final FilenameFilter ymlFilter = (dir, name) -> name.endsWith(".yml");

    @Nullable
    public String readFileToString(@NotNull File file) {
        return FileUtils.readFileToString(file, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String readFileToString(@NotNull File file, @Nullable String def) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (Exception ignore) { }
        return def;
    }

    public boolean createFile(@NotNull File file) {
        if(file.exists()) {
            return false;
        }

        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()) {
            return false;
        }

        try {
            return file.createNewFile();
        } catch (Exception e) {
            return false;
        }
    }

}
