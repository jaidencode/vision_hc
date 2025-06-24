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
    public static KeyBinding chestKey;
    public static KeyBinding blinkKey;
    public static KeyBinding noclipKey;
    public static KeyBinding menuKey;

    static void register() {
        speedKey = new KeyBinding("key.vision.speed", GLFW.GLFW_KEY_G, "key.categories.vision");
        jumpKey = new KeyBinding("key.vision.jump", GLFW.GLFW_KEY_H, "key.categories.vision");
        flyKey = new KeyBinding("key.vision.fly", GLFW.GLFW_KEY_J, "key.categories.vision");
        jesusKey = new KeyBinding("key.vision.jesus", GLFW.GLFW_KEY_K, "key.categories.vision");
        noFallKey = new KeyBinding("key.vision.nofall", GLFW.GLFW_KEY_L, "key.categories.vision");
        xrayKey = new KeyBinding("key.vision.xray", GLFW.GLFW_KEY_SEMICOLON, "key.categories.vision");
        fullBrightKey = new KeyBinding("key.vision.fullbright", GLFW.GLFW_KEY_APOSTROPHE, "key.categories.vision");
        chestKey = new KeyBinding("key.vision.chest", GLFW.GLFW_KEY_P, "key.categories.vision");
        blinkKey = new KeyBinding("key.vision.blink", GLFW.GLFW_KEY_O, "key.categories.vision");
        noclipKey = new KeyBinding("key.vision.noclip", GLFW.GLFW_KEY_I, "key.categories.vision");
        menuKey = new KeyBinding("key.vision.menu", GLFW.GLFW_KEY_BACKSLASH, "key.categories.vision");
        ClientRegistry.registerKeyBinding(speedKey);
        ClientRegistry.registerKeyBinding(jumpKey);
        ClientRegistry.registerKeyBinding(flyKey);
        ClientRegistry.registerKeyBinding(jesusKey);
        ClientRegistry.registerKeyBinding(noFallKey);
        ClientRegistry.registerKeyBinding(xrayKey);
        ClientRegistry.registerKeyBinding(fullBrightKey);
        ClientRegistry.registerKeyBinding(chestKey);
        ClientRegistry.registerKeyBinding(blinkKey);
        ClientRegistry.registerKeyBinding(noclipKey);
        ClientRegistry.registerKeyBinding(menuKey);
    }
}
