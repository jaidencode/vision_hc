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

    private static final double VERTICAL_SPEED = 0.05D;

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        Minecraft mc = Minecraft.getInstance();

        double yMotion = 0.0D;
        if (mc.options.keyJump.isDown()) {
            yMotion += VERTICAL_SPEED;
        }
        if (mc.options.keyShift.isDown()) {
            yMotion -= VERTICAL_SPEED;
        }

        if (yMotion != 0.0D) {
            player.setDeltaMovement(player.getDeltaMovement().x, yMotion, player.getDeltaMovement().z);
            sendMovement(player);
        }

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
