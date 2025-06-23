package org.main.vision;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

/**
 * Holds keybindings for Vision mod.
 */
public class VisionKeybind {
    public static KeyBinding speedKey;

    static void register() {
        speedKey = new KeyBinding("key.vision.speed", GLFW.GLFW_KEY_O, "key.categories.vision");
        ClientRegistry.registerKeyBinding(speedKey);
    }
}
