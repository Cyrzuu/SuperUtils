package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.UUID;

@UtilityClass
public class ProfileUtils {

    @NotNull
    private final UUID uuid = new UUID(0, 0);

    private final String url = "http://textures.minecraft.net/texture/";

    @NotNull
    public static PlayerProfile getProfileTexture(@NotNull String value) {
        PlayerProfile playerProfile = Bukkit.createPlayerProfile(ProfileUtils.uuid);
        try {
            PlayerTextures textures = playerProfile.getTextures();

            URL url = new URL(value.startsWith(ProfileUtils.url) ? value : ProfileUtils.url + value);
            textures.setSkin(url);
            playerProfile.setTextures(textures);
        } catch (Exception ignore) { }

        return playerProfile;
    }

}
