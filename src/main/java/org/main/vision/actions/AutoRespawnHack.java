package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Automatically respawns the player after death.
 */
public class AutoRespawnHack extends ActionBase {
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player != null && player.isDeadOrDying()) {
            if (player.connection != null) {
                player.connection.send(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
            }
        }
    }
}
