package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.main.vision.VisionClient;

/**
 * Applies a high jump velocity when the jump key is pressed.
 */
public class JumpHack extends ActionBase {


    /** Tracks whether the jump key was pressed last tick to
     *  detect rising-edge presses for consistent boosting. */
    private boolean wasJumping = false;

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;

        boolean jumping = player.input != null && player.input.jumping;

        // Trigger the boosted jump on the rising edge of the jump key
        // only when the player is on the ground to avoid mid-air boosts
        if (jumping && !wasJumping && player.isOnGround()) {
            double vel = VisionClient.getSettings().jumpVelocity;
            player.setDeltaMovement(player.getDeltaMovement().x, vel, player.getDeltaMovement().z);
            sendMovement(player);
        }

        wasJumping = jumping;
    }

    private void sendMovement(ClientPlayerEntity player) {
        ClientPlayNetHandler conn = player.connection;
        if (conn != null) {
            conn.send(new CPlayerPacket.PositionRotationPacket(
                    player.getX(), player.getY(), player.getZ(),
                    player.yRot, player.xRot, player.isOnGround()));
        }
    }
}
