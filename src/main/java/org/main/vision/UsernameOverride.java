package org.main.vision;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.network.IPacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.main.vision.config.HackSettings;
import org.main.vision.VisionClient;

import java.lang.reflect.Field;

/**
 * Replaces the player's username in packets with a custom value.
 */
public class UsernameOverride {

    /**
     * Modify outgoing packets to replace the player's GameProfile with the spoofed
     * username when starting a login.
     */
    public static void handleOutgoingPacket(IPacket<?> packet) {
        if (!(packet instanceof CLoginStartPacket)) return;

        HackSettings cfg = VisionClient.getSettings();
        if (!cfg.usernameOverrideEnabled) return;

        String overrideName = cfg.customUsername;
        if (overrideName == null || overrideName.isEmpty()) return;

        try {
            Field f = ObfuscationReflectionHelper.findField(CLoginStartPacket.class, "field_149305_a");
            f.setAccessible(true);
            GameProfile profile = (GameProfile) f.get(packet);
            if (profile != null && !overrideName.equals(profile.getName())) {
                GameProfile replaced = new GameProfile(profile.getId(), overrideName);
                f.set(packet, replaced);
                // Also update the local player's session name so future packets use it
                Field userField = ObfuscationReflectionHelper.findField(Minecraft.class, "field_71449_j");
                userField.setAccessible(true);
                Object user = userField.get(Minecraft.getInstance());
                if (user != null) {
                    @SuppressWarnings("unchecked")
                    Field nameField = ObfuscationReflectionHelper.findField((Class<Object>) user.getClass(), "field_152554_a");
                    nameField.setAccessible(true);
                    nameField.set(user, overrideName);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
