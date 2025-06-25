package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import org.main.vision.network.AdaptivePacketHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Prevents the player from walking off ledges by automatically sneaking when no block is below.
 */
public class SafeWalkHack extends ActionBase {
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        if (player.input == null) return;
        if (isNearEdge(player) && !isIntentionalAction(player)) {
            haltMovement(player);
        }
    }

    /**
     * Returns true if the player is intentionally moving off
     * the ledge, such as jumping or performing an obvious
     * upward movement.
     */
    private boolean isIntentionalAction(ClientPlayerEntity player) {
        if (player.input.jumping) {
            return true;
        }
        return player.getDeltaMovement().y > 0.1D;
    }

    private boolean isNearEdge(ClientPlayerEntity player) {
        Vector3d motion = player.getDeltaMovement();
        double dx = motion.x;
        double dz = motion.z;
        if (dx == 0 && dz == 0) return false;
        BlockPos pos = new BlockPos(player.getX() + dx, player.getY() - 1, player.getZ() + dz);
        return player.level.isEmptyBlock(pos);
    }

    private void haltMovement(ClientPlayerEntity player) {
        Vector3d motion = player.getDeltaMovement();
        // gently push the player back from the ledge
        double newX = -motion.x * 0.25D;
        double newZ = -motion.z * 0.25D;
        player.setDeltaMovement(newX, motion.y, newZ);
        player.input.leftImpulse = 0f;
        player.input.forwardImpulse = 0f;
        player.input.shiftKeyDown = true;
        sendMovement(player);
    }

    private void sendMovement(ClientPlayerEntity player) {
        AdaptivePacketHandler.getInstance().sendDirect(
                new CPlayerPacket.PositionRotationPacket(
                        player.getX(), player.getY(), player.getZ(),
                        player.yRot, player.xRot, player.isOnGround()));
    }
}
