package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.network.IPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.main.vision.VisionClient;
import org.main.vision.config.HackSettings;

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
     * Replace occurrences of the player's actual name with the alias within the
     * provided packet. Login and handshake packets are ignored to avoid
     * protocol issues.
     */
    private void processPacket(IPacket<?> packet, String from, String to) {
        if (packet == null) return;
        String name = packet.getClass().getName();
        if (name.contains(".network.login.") || name.contains(".network.handshake.")) {
            return;
        }
        try {
            replaceStrings(packet, from, to, new java.util.IdentityHashMap<>());
        } catch (Exception ignored) {}
    }

    /**
     * Modify the player's name within incoming packets.
     */
    public static void handleIncoming(IPacket<?> packet) {
        SpoofNameHack hack = VisionClient.getSpoofNameHack();
        if (!hack.isEnabled()) return;
        HackSettings cfg = VisionClient.getSettings();
        if (!cfg.spoofIncoming) return;
        String alias = cfg.spoofName;
        if (alias == null || alias.isEmpty() || hack.actualName == null) return;

        hack.processPacket(packet, hack.actualName, alias);
    }

    /**
     * Modify the player's name within outgoing packets.
     */
    public static void handleOutgoing(IPacket<?> packet) {
        SpoofNameHack hack = VisionClient.getSpoofNameHack();
        if (!hack.isEnabled()) return;
        HackSettings cfg = VisionClient.getSettings();
        if (!cfg.spoofOutgoing) return;
        String alias = cfg.spoofName;
        if (alias == null || alias.isEmpty() || hack.actualName == null) return;

        hack.processPacket(packet, hack.actualName, alias);
    }

    /**
     * Recursively replace string values within the given object.
     */
    private void replaceStrings(Object obj, String from, String to, java.util.Map<Object, Boolean> visited) {
        if (obj == null || visited.containsKey(obj)) return;
        visited.put(obj, Boolean.TRUE);

        if (obj instanceof StringTextComponent) {
            try {
                java.lang.reflect.Field f = StringTextComponent.class.getDeclaredField("text");
                f.setAccessible(true);
                String val = (String) f.get(obj);
                if (val != null) f.set(obj, val.replace(from, to));
            } catch (Exception ignored) {}
        }

        if (obj instanceof ITextComponent) {
            for (ITextComponent child : ((ITextComponent) obj).getSiblings()) {
                replaceStrings(child, from, to, visited);
            }
            return;
        }

        Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            int len = java.lang.reflect.Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                replaceStrings(java.lang.reflect.Array.get(obj, i), from, to, visited);
            }
            return;
        }

        if (obj instanceof Iterable) {
            for (Object o : (Iterable<?>) obj) {
                replaceStrings(o, from, to, visited);
            }
            return;
        }

        while (cls != Object.class) {
            for (java.lang.reflect.Field field : cls.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object val = field.get(obj);
                    if (val instanceof String) {
                        field.set(obj, ((String) val).replace(from, to));
                    } else {
                        replaceStrings(val, from, to, visited);
                    }
                } catch (Exception ignored) {}
            }
            cls = cls.getSuperclass();
        }
    }
}
