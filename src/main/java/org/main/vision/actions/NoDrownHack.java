package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Prevents drowning by constantly refreshing the player's air supply.
 */
public class NoDrownHack extends ActionBase {

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;

        // Refill the player's air each tick
        player.setAirSupply(player.getMaxAirSupply());

        // Send a movement packet so the server stays in sync
        ClientPlayNetHandler conn = player.connection;
        if (conn != null) {
            conn.send(new CPlayerPacket.PositionRotationPacket(
                    player.getX(), player.getY(), player.getZ(),
                    player.yRot, player.xRot, player.isOnGround()));
        }
    }
}
