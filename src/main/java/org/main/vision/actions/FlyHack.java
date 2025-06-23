package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.main.vision.VisionClient;

/**
 * Forcefully allows the player to fly by directly manipulating movement.
 */
public class FlyHack extends ActionBase {

    /**
     * Up/down speed added when pressing jump or sneak.
     * Using 0.08 to roughly match Minecraft's gravity so
     * ascending and descending feel symmetrical.
     */

    /**
     * Constant upward velocity applied every tick to counteract
     * gravity so the player can hover when no keys are pressed.
     */

    /**
     * Horizontal speed applied when pressing WASD.
     */

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        Minecraft mc = Minecraft.getInstance();

        // Start with a small upward push to negate gravity so the player hovers
        double yMotion = VisionClient.getSettings().flyHoverVelocity;

        // Modify the vertical motion based on input keys
        if (mc.options.keyJump.isDown()) {
            yMotion += VisionClient.getSettings().flyVerticalSpeed;
        }
        if (mc.options.keyShift.isDown()) {
            yMotion -= VisionClient.getSettings().flyVerticalSpeed;
        }

        // Calculate horizontal velocity based on WASD input and player yaw
        double xMotion = 0.0D;
        double zMotion = 0.0D;
        float yawRad = (float) Math.toRadians(player.yRot);
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);
        double strafeX = Math.cos(yawRad);
        double strafeZ = Math.sin(yawRad);

        if (mc.options.keyUp.isDown()) {
            xMotion += forwardX;
            zMotion += forwardZ;
        }
        if (mc.options.keyDown.isDown()) {
            xMotion -= forwardX;
            zMotion -= forwardZ;
        }
        if (mc.options.keyLeft.isDown()) {
            xMotion += strafeX;
            zMotion += strafeZ;
        }
        if (mc.options.keyRight.isDown()) {
            xMotion -= strafeX;
            zMotion -= strafeZ;
        }

        // Normalize to keep diagonal movement consistent
        double mag = Math.sqrt(xMotion * xMotion + zMotion * zMotion);
        if (mag > 0.0D) {
            double hs = VisionClient.getSettings().flyHorizontalSpeed;
            xMotion = xMotion / mag * hs;
            zMotion = zMotion / mag * hs;
        }

        // Apply the calculated velocity each tick for responsive flight
        player.setDeltaMovement(xMotion, yMotion, zMotion);
        sendMovement(player);

        player.fallDistance = 0.0f;
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
