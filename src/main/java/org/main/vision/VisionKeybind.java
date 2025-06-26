package org.main.vision;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

/**
 * Holds keybindings for Vision mod.
 */
public class VisionKeybind {
    public static KeyBinding speedKey;
    public static KeyBinding jumpKey;
    public static KeyBinding flyKey;
    public static KeyBinding jesusKey;
    public static KeyBinding noFallKey;
    public static KeyBinding xrayKey;
    public static KeyBinding fullBrightKey;
    public static KeyBinding forceCritKey;
    public static KeyBinding blinkKey;
    public static KeyBinding antiKnockbackKey;
    public static KeyBinding autoToolKey;
    public static KeyBinding safeWalkKey;
    public static KeyBinding autoSprintKey;
    public static KeyBinding autoRespawnKey;
    public static KeyBinding bowAimbotKey;
    public static KeyBinding rubberBanderKey;
    public static KeyBinding seeBarrierKey;
    public static KeyBinding nameTagsKey;
    public static KeyBinding scaffoldKey;
    public static KeyBinding quickChargeKey;
    public static KeyBinding autoDodgeKey;
    public static KeyBinding teleportKey;
    public static KeyBinding menuKey;

    static void register() {
        speedKey = new KeyBinding("key.vision.speed", GLFW.GLFW_KEY_G, "key.categories.vision");
        jumpKey = new KeyBinding("key.vision.jump", GLFW.GLFW_KEY_H, "key.categories.vision");
        flyKey = new KeyBinding("key.vision.fly", GLFW.GLFW_KEY_J, "key.categories.vision");
        jesusKey = new KeyBinding("key.vision.jesus", GLFW.GLFW_KEY_K, "key.categories.vision");
        noFallKey = new KeyBinding("key.vision.nofall", GLFW.GLFW_KEY_L, "key.categories.vision");
        xrayKey = new KeyBinding("key.vision.xray", GLFW.GLFW_KEY_SEMICOLON, "key.categories.vision");
        fullBrightKey = new KeyBinding("key.vision.fullbright", GLFW.GLFW_KEY_APOSTROPHE, "key.categories.vision");
        forceCritKey = new KeyBinding("key.vision.forcecrit", GLFW.GLFW_KEY_P, "key.categories.vision");
        blinkKey = new KeyBinding("key.vision.blink", GLFW.GLFW_KEY_O, "key.categories.vision");
        antiKnockbackKey = new KeyBinding("key.vision.antiknockback", GLFW.GLFW_KEY_M, "key.categories.vision");
        autoToolKey = new KeyBinding("key.vision.autotool", GLFW.GLFW_KEY_B, "key.categories.vision");
        safeWalkKey = new KeyBinding("key.vision.safewalk", GLFW.GLFW_KEY_V, "key.categories.vision");
        autoSprintKey = new KeyBinding("key.vision.autosprint", GLFW.GLFW_KEY_C, "key.categories.vision");
        autoRespawnKey = new KeyBinding("key.vision.autorespawn", GLFW.GLFW_KEY_R, "key.categories.vision");
        bowAimbotKey = new KeyBinding("key.vision.bowaimbot", GLFW.GLFW_KEY_X, "key.categories.vision");
        rubberBanderKey = new KeyBinding("key.vision.rubberbander", GLFW.GLFW_KEY_Y, "key.categories.vision");
        seeBarrierKey = new KeyBinding("key.vision.seebarrier", GLFW.GLFW_KEY_I, "key.categories.vision");
        nameTagsKey = new KeyBinding("key.vision.nametags", GLFW.GLFW_KEY_F6, "key.categories.vision");
        scaffoldKey = new KeyBinding("key.vision.scaffold", GLFW.GLFW_KEY_F7, "key.categories.vision");
        quickChargeKey = new KeyBinding("key.vision.quickcharge", GLFW.GLFW_KEY_F9, "key.categories.vision");
        autoDodgeKey = new KeyBinding("key.vision.autododge", GLFW.GLFW_KEY_F10, "key.categories.vision");
        teleportKey = new KeyBinding("key.vision.teleport", GLFW.GLFW_KEY_F12, "key.categories.vision");
        menuKey = new KeyBinding("key.vision.menu", GLFW.GLFW_KEY_BACKSLASH, "key.categories.vision");
        ClientRegistry.registerKeyBinding(speedKey);
        ClientRegistry.registerKeyBinding(jumpKey);
        ClientRegistry.registerKeyBinding(flyKey);
        ClientRegistry.registerKeyBinding(jesusKey);
        ClientRegistry.registerKeyBinding(noFallKey);
        ClientRegistry.registerKeyBinding(xrayKey);
        ClientRegistry.registerKeyBinding(fullBrightKey);
        ClientRegistry.registerKeyBinding(forceCritKey);
        ClientRegistry.registerKeyBinding(blinkKey);
        ClientRegistry.registerKeyBinding(antiKnockbackKey);
        ClientRegistry.registerKeyBinding(autoToolKey);
        ClientRegistry.registerKeyBinding(safeWalkKey);
        ClientRegistry.registerKeyBinding(autoSprintKey);
        ClientRegistry.registerKeyBinding(autoRespawnKey);
        ClientRegistry.registerKeyBinding(bowAimbotKey);
        ClientRegistry.registerKeyBinding(rubberBanderKey);
        ClientRegistry.registerKeyBinding(seeBarrierKey);
        ClientRegistry.registerKeyBinding(nameTagsKey);
        ClientRegistry.registerKeyBinding(scaffoldKey);
        ClientRegistry.registerKeyBinding(quickChargeKey);
        ClientRegistry.registerKeyBinding(autoDodgeKey);
        ClientRegistry.registerKeyBinding(teleportKey);
        ClientRegistry.registerKeyBinding(menuKey);
    }
}
