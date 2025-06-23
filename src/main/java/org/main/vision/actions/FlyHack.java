package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Allows the player to fly in survival mode.
 */
public class FlyHack extends ActionBase {
    private boolean originalMayfly;

    @Override
    protected void onEnable() {
        ClientPlayerEntity player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null) {
            originalMayfly = player.abilities.mayfly;
            player.abilities.mayfly = true;
            player.onUpdateAbilities();
        }
    }

    @Override
    protected void onDisable() {
        ClientPlayerEntity player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null) {
            player.abilities.mayfly = originalMayfly;
            if (!player.abilities.mayfly) {
                player.abilities.flying = false;
            }
            player.onUpdateAbilities();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (!isEnabled()) return;
        if (event.player instanceof ClientPlayerEntity) {
            ClientPlayerEntity player = (ClientPlayerEntity) event.player;
            player.abilities.mayfly = true;
        }
    }
}
