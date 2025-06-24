package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Increases the player's step height for smoother navigation over blocks.
 */
public class StepHack extends ActionBase {
    private static final float STEP_HEIGHT = 1.5F;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;
        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        player.maxUpStep = isEnabled() ? STEP_HEIGHT : 0.6F;
    }
}
