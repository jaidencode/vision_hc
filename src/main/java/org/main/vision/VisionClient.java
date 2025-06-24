package org.main.vision;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.main.vision.actions.SpeedHack;
import org.main.vision.actions.JumpHack;
import org.main.vision.actions.FlyHack;
import org.main.vision.actions.JesusHack;
import org.main.vision.actions.NoFallHack;
import org.main.vision.actions.XRayHack;
import org.main.vision.actions.FullBrightHack;
import org.main.vision.actions.ForceCritHack;
import org.main.vision.actions.AntiVanishHack;
import org.main.vision.actions.BlinkHack;
import org.main.vision.actions.AntiKnockbackHack;
import org.main.vision.actions.AntiCheatHack;
import org.main.vision.config.HackSettings;

/**
 * Handles client-only events.
 */
@Mod.EventBusSubscriber(modid = "vision", value = Dist.CLIENT)
public class VisionClient {
    private static final SpeedHack SPEED_HACK = new SpeedHack();
    private static final JumpHack JUMP_HACK = new JumpHack();
    private static final FlyHack FLY_HACK = new FlyHack();
    private static final JesusHack JESUS_HACK = new JesusHack();
    private static final NoFallHack NOFALL_HACK = new NoFallHack();
    private static final XRayHack XRAY_HACK = new XRayHack();
    private static final FullBrightHack FULLBRIGHT_HACK = new FullBrightHack();
    private static final ForceCritHack FORCECRIT_HACK = new ForceCritHack();
    private static final AntiVanishHack ANTIVANISH_HACK = new AntiVanishHack();
    private static final BlinkHack BLINK_HACK = new BlinkHack();
    private static final AntiKnockbackHack ANTIKNOCKBACK_HACK = new AntiKnockbackHack();
    private static final AntiCheatHack ANTICHEAT_HACK = new AntiCheatHack();
    private static HackSettings SETTINGS;

    static void init() {
        VisionKeybind.register();
        SETTINGS = HackSettings.load();
    }

    public static SpeedHack getSpeedHack() {
        return SPEED_HACK;
    }

    public static JumpHack getJumpHack() {
        return JUMP_HACK;
    }

    public static FlyHack getFlyHack() {
        return FLY_HACK;
    }

    public static JesusHack getJesusHack() {
        return JESUS_HACK;
    }

    public static NoFallHack getNoFallHack() {
        return NOFALL_HACK;
    }

    public static XRayHack getXRayHack() {
        return XRAY_HACK;
    }

    public static FullBrightHack getFullBrightHack() {
        return FULLBRIGHT_HACK;
    }

    public static ForceCritHack getForceCritHack() {
        return FORCECRIT_HACK;
    }

    public static AntiVanishHack getAntiVanishHack() {
        return ANTIVANISH_HACK;
    }

    public static BlinkHack getBlinkHack() {
        return BLINK_HACK;
    }

    public static AntiKnockbackHack getAntiKnockbackHack() {
        return ANTIKNOCKBACK_HACK;
    }


    public static AntiCheatHack getAntiCheatHack() {
        return ANTICHEAT_HACK;
    }


    public static HackSettings getSettings() {
        return SETTINGS;
    }

    /** Persist current settings to disk. */
    public static void saveSettings() {
        if (SETTINGS != null) {
            SETTINGS.save();
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (event.getKey() == VisionKeybind.speedKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            SPEED_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.jumpKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            JUMP_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.flyKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            FLY_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.jesusKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            JESUS_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.noFallKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            NOFALL_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.xrayKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            XRAY_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.fullBrightKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            FULLBRIGHT_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.forceCritKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            FORCECRIT_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.antiVanishKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            ANTIVANISH_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.blinkKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            BLINK_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.antiKnockbackKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            ANTIKNOCKBACK_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.antiCheatKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            ANTICHEAT_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.menuKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
            Minecraft.getInstance().setScreen(new VisionMenuScreen());
        }
    }
}
