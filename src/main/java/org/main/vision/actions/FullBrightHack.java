package org.main.vision.actions;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Gives the player night vision for a brighter world. */
public class FullBrightHack extends ActionBase {
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        PlayerEntity player = event.player;
        if (isEnabled()) {
            player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 220, 0, false, false));
        } else if (player.hasEffect(Effects.NIGHT_VISION)) {
            player.removeEffect(Effects.NIGHT_VISION);
        }
    }

    @Override
    protected void onDisable() {
        PlayerEntity player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null) {
            player.removeEffect(Effects.NIGHT_VISION);
        }
    }
}
