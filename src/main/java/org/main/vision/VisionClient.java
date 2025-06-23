package org.main.vision;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.main.vision.actions.SpeedHack;

/**
 * Handles client-only events.
 */
@Mod.EventBusSubscriber(modid = "vision", value = Dist.CLIENT)
public class VisionClient {
    private static final SpeedHack SPEED_HACK = new SpeedHack();

    static void init() {
        VisionKeybind.register();
    }

    public static SpeedHack getSpeedHack() {
        return SPEED_HACK;
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (VisionKeybind.speedKey.isDown()) {
            SPEED_HACK.toggle();
        }
    }
}
