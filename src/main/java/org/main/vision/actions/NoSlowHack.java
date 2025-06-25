package org.main.vision.actions;

import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Removes movement slow-downs when using items or walking through
 * webs and soul sand.
 */
public class NoSlowHack extends ActionBase {
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        if (player.input != null && player.isUsingItem()) {
            player.input.leftImpulse *= 5.0F;
            player.input.forwardImpulse *= 5.0F;
        }

        BlockPos pos = new BlockPos(player.getX(), player.getY() - 0.1D, player.getZ());
        if (player.level.getBlockState(pos).getBlock() == Blocks.SOUL_SAND) {
            Vector3d m = player.getDeltaMovement();
            player.setDeltaMovement(m.x * 1.5D, m.y, m.z * 1.5D);
        }
    }
}
