package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Quickly resets the attack cooldown each tick for instant charged hits. */
public class QuickChargeHack extends ActionBase {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;
        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        // Attempt to reset the attack cooldown so the next hit is critical
        try {
            player.resetAttackStrengthTicker();
        } catch (Throwable t) {
            // Fallback: ignore if the method is unavailable
        }
    }
}
