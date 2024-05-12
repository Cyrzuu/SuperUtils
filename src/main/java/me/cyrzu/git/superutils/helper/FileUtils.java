package me.cyrzu.git.superutils.helper;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public String readFileToString(@NotNull File file) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (Exception ignore) { }
        return "";
    }

    public boolean createFile(@NotNull File file) {
        if(file.exists()) {
            return false;
        }

        if(!file.getParentFile().exists() && !file.mkdirs()) {
            return false;
        }

        try {
            return file.createNewFile();
        } catch (Exception e) {
            return false;
        }
    }

}
