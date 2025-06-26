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
            java.lang.reflect.Field f = net.minecraft.entity.player.PlayerEntity.class.getDeclaredField("attackStrengthTicker");
            f.setAccessible(true);
            int full = (int) (player.getCurrentItemAttackStrengthDelay() + 1);
            f.setInt(player, full);
        } catch (Throwable t) {
            // ignore if reflection fails
        }
    }
}
