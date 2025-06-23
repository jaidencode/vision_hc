package org.main.vision.actions;

import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.main.vision.VisionClient;

/**
 * Allows the player to stand and walk on water blocks as if they were solid.
 */
public class JesusHack extends ActionBase {

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;

        BlockPos below = new BlockPos(player.getX(), player.getY() - 0.1D, player.getZ());
        if (player.level.getBlockState(below).getBlock() == Blocks.WATER) {
            player.setOnGround(true);
            if (player.getDeltaMovement().y < 0.0D && !player.input.jumping) {
                player.setDeltaMovement(player.getDeltaMovement().x, 0.0D, player.getDeltaMovement().z);
            }
            player.fallDistance = 0.0f;
            sendMovement(player);
        }
    }

    private void sendMovement(ClientPlayerEntity player) {
        ClientPlayNetHandler conn = player.connection;
        if (conn != null) {
            conn.send(new CPlayerPacket.PositionRotationPacket(
                    player.getX(), player.getY(), player.getZ(),
                    player.yRot, player.xRot, true));
        }
    }
}
