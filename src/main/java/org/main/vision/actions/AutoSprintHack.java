package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Automatically sets the player sprinting when moving forward.
 */
public class AutoSprintHack extends ActionBase {
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        if (player.input == null) return;

        boolean forward = player.input.forwardImpulse > 0;
        if (forward && !player.isSprinting() && !player.isCrouching()) {
            player.setSprinting(true);
            sendSprintPacket(player, true);
        } else if (!forward && player.isSprinting()) {
            player.setSprinting(false);
            sendSprintPacket(player, false);
        }
    }

    private void sendSprintPacket(ClientPlayerEntity player, boolean start) {
        ClientPlayNetHandler conn = player.connection;
        if (conn != null) {
            CEntityActionPacket.Action action = start ?
                    CEntityActionPacket.Action.START_SPRINTING :
                    CEntityActionPacket.Action.STOP_SPRINTING;
            conn.send(new CEntityActionPacket(player, action));
        }
    }
}
