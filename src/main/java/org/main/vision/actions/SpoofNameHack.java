package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.main.vision.VisionClient;
import org.main.vision.config.HackSettings;
import org.main.vision.util.PacketStringReplacer;

/**
 * Replaces the player's name in chat with a user defined alias.
 */
public class SpoofNameHack extends ActionBase {
    private String actualName;

    @Override
    protected void onEnable() {
        captureName();
        applyAlias();
        applyTabListAlias();
    }

    @Override
    protected void onDisable() {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            player.setCustomName(null);
            clearTabListAlias();
        }
    }

    private void captureName() {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            actualName = player.getGameProfile().getName();
        }
    }

    private void applyAlias() {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        HackSettings cfg = VisionClient.getSettings();
        if (player != null && cfg.spoofName != null && !cfg.spoofName.isEmpty()) {
            player.setCustomName(new StringTextComponent(cfg.spoofName));
            applyTabListAlias();
        }
    }

    /** Update the player's name in the tab list. */
    private void applyTabListAlias() {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        HackSettings cfg = VisionClient.getSettings();
        if (player == null || Minecraft.getInstance().getConnection() == null) return;
        NetworkPlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(player.getUUID());
        if (info != null) {
            if (cfg.spoofName != null && !cfg.spoofName.isEmpty()) {
                info.setTabListDisplayName(new StringTextComponent(cfg.spoofName));
            } else {
                info.setTabListDisplayName(null);
            }
        }
    }

    /** Remove any alias from the tab list. */
    private void clearTabListAlias() {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().getConnection() == null) return;
        NetworkPlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(player.getUUID());
        if (info != null) {
            info.setTabListDisplayName(null);
        }
    }

    /** Reapply the alias when settings are changed. */
    public void refreshAlias() {
        if (isEnabled()) {
            applyAlias();
        }
    }

    @SubscribeEvent
    public void onLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if (!isEnabled()) return;
        captureName();
        applyAlias();
    }

    /**
     * Modify an outgoing packet to replace the player's real name with the alias.
     */
    public static void handleOutgoing(Object packet) {
        SpoofNameHack hack = VisionClient.getSpoofNameHack();
        hack.modifyPacket(packet, true);
    }

    /**
     * Modify an incoming packet to replace the real name with the alias.
     */
    public static void handleIncoming(Object packet) {
        SpoofNameHack hack = VisionClient.getSpoofNameHack();
        hack.modifyPacket(packet, false);
    }

    private void modifyPacket(Object packet, boolean outgoing) {
        if (!isEnabled()) return;
        HackSettings cfg = VisionClient.getSettings();
        if (outgoing && !cfg.spoofOutgoing) return;
        if (!outgoing && !cfg.spoofIncoming) return;
        if (actualName == null) return;
        String alias = cfg.spoofName;
        if (alias == null || alias.isEmpty()) return;
        PacketStringReplacer.replaceStrings(packet, outgoing ? alias : actualName,
                outgoing ? actualName : alias);
    }
}
