package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Negates all incoming damage to the local player.
 */
public class NoDamageHack extends ActionBase {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!isEnabled()) return;
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
        if (event.getEntityLiving() != Minecraft.getInstance().player) return;
        event.setAmount(0.0F);
        event.setCanceled(true);
    }
}
