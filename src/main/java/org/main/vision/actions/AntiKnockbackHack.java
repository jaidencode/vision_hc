package org.main.vision.actions;

import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.entity.player.ClientPlayerEntity;

/**
 * Prevents external forces from moving the player by canceling knockback.
 */
public class AntiKnockbackHack extends ActionBase {
    @SubscribeEvent
    public void onKnockback(LivingKnockBackEvent event) {
        if (!isEnabled()) return;
        if (event.getEntityLiving() instanceof ClientPlayerEntity) {
            event.setCanceled(true);
            event.setStrength(0.0F);
        }
    }
}
