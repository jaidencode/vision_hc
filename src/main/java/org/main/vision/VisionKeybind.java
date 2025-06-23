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
    public static KeyBinding menuKey;

    static void register() {
        speedKey = new KeyBinding("key.vision.speed", GLFW.GLFW_KEY_G, "key.categories.vision");
        jumpKey = new KeyBinding("key.vision.jump", GLFW.GLFW_KEY_H, "key.categories.vision");
        flyKey = new KeyBinding("key.vision.fly", GLFW.GLFW_KEY_J, "key.categories.vision");
        menuKey = new KeyBinding("key.vision.menu", GLFW.GLFW_KEY_BACKSLASH, "key.categories.vision");
        ClientRegistry.registerKeyBinding(speedKey);
        ClientRegistry.registerKeyBinding(jumpKey);
        ClientRegistry.registerKeyBinding(flyKey);
        ClientRegistry.registerKeyBinding(menuKey);
    }
}
