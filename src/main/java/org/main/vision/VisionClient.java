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
import org.main.vision.actions.BlinkHack;
import org.main.vision.actions.AntiKnockbackHack;
import org.main.vision.actions.AutoToolHack;
import org.main.vision.actions.SafeWalkHack;
import org.main.vision.actions.AutoSprintHack;
import org.main.vision.actions.AutoRespawnHack;
import org.main.vision.actions.BowAimbotHack;
import org.main.vision.actions.RubberBanderHack;
import org.main.vision.actions.SeeBarrierHack;
import org.main.vision.actions.NameTagsHack;
import org.main.vision.actions.ScaffoldHack;
import org.main.vision.actions.QuickChargeHack;
import org.main.vision.actions.TeleportHack;
import org.main.vision.actions.AutoDodgeHack;
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
    private static final BlinkHack BLINK_HACK = new BlinkHack();
    private static final AntiKnockbackHack ANTIKNOCKBACK_HACK = new AntiKnockbackHack();
    private static final AutoToolHack AUTOTOOL_HACK = new AutoToolHack();
    private static final SafeWalkHack SAFEWALK_HACK = new SafeWalkHack();
    private static final AutoSprintHack AUTOSPRINT_HACK = new AutoSprintHack();
    private static final AutoRespawnHack AUTORESPAWN_HACK = new AutoRespawnHack();
    private static final BowAimbotHack BOWAIMBOT_HACK = new BowAimbotHack();
    private static final RubberBanderHack RUBBERBANDER_HACK = new RubberBanderHack();
    private static final SeeBarrierHack SEEBARRIER_HACK = new SeeBarrierHack();
    private static final NameTagsHack NAMETAGS_HACK = new NameTagsHack();
    private static final ScaffoldHack SCAFFOLD_HACK = new ScaffoldHack();
    private static final QuickChargeHack QUICKCHARGE_HACK = new QuickChargeHack();
    private static final AutoDodgeHack AUTODODGE_HACK = new AutoDodgeHack();
    private static final TeleportHack TELEPORT_HACK = new TeleportHack();
    private static HackSettings SETTINGS;

    static void init() {
        VisionKeybind.register();
        SETTINGS = HackSettings.load();
        TELEPORT_HACK.setTarget(SETTINGS.teleportX, SETTINGS.teleportY, SETTINGS.teleportZ);
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

    public static BlinkHack getBlinkHack() {
        return BLINK_HACK;
    }

    public static AntiKnockbackHack getAntiKnockbackHack() {
        return ANTIKNOCKBACK_HACK;
    }

    public static AutoToolHack getAutoToolHack() {
        return AUTOTOOL_HACK;
    }

    public static SafeWalkHack getSafeWalkHack() {
        return SAFEWALK_HACK;
    }

    public static AutoSprintHack getAutoSprintHack() {
        return AUTOSPRINT_HACK;
    }

    public static AutoRespawnHack getAutoRespawnHack() {
        return AUTORESPAWN_HACK;
    }

    public static BowAimbotHack getBowAimbotHack() {
        return BOWAIMBOT_HACK;
    }

    public static RubberBanderHack getRubberBanderHack() {
        return RUBBERBANDER_HACK;
    }

    public static SeeBarrierHack getSeeBarrierHack() {
        return SEEBARRIER_HACK;
    }

    public static NameTagsHack getNameTagsHack() {
        return NAMETAGS_HACK;
    }

    public static ScaffoldHack getScaffoldHack() {
        return SCAFFOLD_HACK;
    }

    public static QuickChargeHack getQuickChargeHack() {
        return QUICKCHARGE_HACK;
    }

    public static AutoDodgeHack getAutoDodgeHack() {
        return AUTODODGE_HACK;
    }

    public static TeleportHack getTeleportHack() {
        return TELEPORT_HACK;
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
        if (event.getKey() == VisionKeybind.blinkKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            BLINK_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.antiKnockbackKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            ANTIKNOCKBACK_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.autoToolKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            AUTOTOOL_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.safeWalkKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            SAFEWALK_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.autoSprintKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            AUTOSPRINT_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.autoRespawnKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            AUTORESPAWN_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.bowAimbotKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            BOWAIMBOT_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.rubberBanderKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            RUBBERBANDER_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.seeBarrierKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            SEEBARRIER_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.nameTagsKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            NAMETAGS_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.scaffoldKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            SCAFFOLD_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.quickChargeKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            QUICKCHARGE_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.autoDodgeKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            AUTODODGE_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.teleportKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) {
            TELEPORT_HACK.toggle();
        }
        if (event.getKey() == VisionKeybind.menuKey.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
            Minecraft.getInstance().setScreen(new VisionMenuScreen());
        }
    }
}
