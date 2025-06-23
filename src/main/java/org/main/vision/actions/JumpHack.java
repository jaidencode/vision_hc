package org.main.vision.actions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Gives the player a constant jump boost effect while enabled.
 */
public class JumpHack extends ActionBase {
    @Override
    protected void onEnable() {
        PlayerEntity player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null) {
            apply(player);
        }
    }

    @Override
    protected void onDisable() {
        PlayerEntity player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null) {
            remove(player);
        }
    }

    private void apply(PlayerEntity player) {
        player.addEffect(new EffectInstance(Effects.JUMP, 2, 4, false, false));
    }

    private void remove(PlayerEntity player) {
        player.removeEffect(Effects.JUMP);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (isEnabled()) {
            apply(event.player);
        }
    }
}
