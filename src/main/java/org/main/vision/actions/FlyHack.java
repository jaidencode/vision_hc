package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Forcefully allows the player to fly by directly manipulating movement.
 */
public class FlyHack extends ActionBase {

    /**
     * Up/down speed added when pressing jump or sneak.
     * Using 0.08 to roughly match Minecraft's gravity so
     * ascending and descending feel symmetrical.
     */
    private static final double VERTICAL_SPEED = 0.08D;

    /**
     * Constant upward velocity applied every tick to counteract
     * gravity so the player can hover when no keys are pressed.
     */
    private static final double HOVER_VELOCITY = 0.08D;

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        Minecraft mc = Minecraft.getInstance();

        // Start with a small upward push to negate gravity so the player hovers
        double yMotion = HOVER_VELOCITY;

        // Modify the vertical motion based on input keys
        if (mc.options.keyJump.isDown()) {
            yMotion += VERTICAL_SPEED;
        }
        if (mc.options.keyShift.isDown()) {
            yMotion -= VERTICAL_SPEED;
        }

        // Apply the calculated vertical motion every tick so ascending and
        // descending feel responsive and hovering is possible.
        player.setDeltaMovement(player.getDeltaMovement().x, yMotion, player.getDeltaMovement().z);
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
