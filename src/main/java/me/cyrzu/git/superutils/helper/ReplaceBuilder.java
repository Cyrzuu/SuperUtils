package me.cyrzu.git.superutils.helper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplaceBuilder {

    public final static ReplaceBuilder PLAYER = new ReplaceBuilder("%player");

    public final static ReplaceBuilder PLAYER_MESSAGE = new ReplaceBuilder("%player", "%message");

    private final List<String> replacment;

    public ReplaceBuilder(String... var0) {
        this.replacment = new ArrayList<>(Arrays.asList(var0));
    }

    public ReplaceBuilder add(@NotNull String... original) {
        replacment.addAll(Arrays.asList(original));
        return this;
    }

    public String replaceMessage(@NotNull String message, @NotNull Object... args) {
        if(this.replacment.size() <= args.length) {
            return message;
        }

        int index = 0;
        for (String s : replacment) {
            message = message.replace(s, args[index++].toString());
        }

        return message;
    }

    @NotNull
    public static ReplaceBuilder of(@NotNull String... originals) {
        return new ReplaceBuilder(originals);
    }

}
