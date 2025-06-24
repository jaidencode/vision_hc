package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
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
        if (shouldSneak(player)) {
            player.input.shiftKeyDown = true;
        }
    }

    private boolean shouldSneak(ClientPlayerEntity player) {
        double dx = player.getDeltaMovement().x;
        double dz = player.getDeltaMovement().z;
        if (dx == 0 && dz == 0) return false;
        BlockPos pos = new BlockPos(player.getX() + dx, player.getY() - 1, player.getZ() + dz);
        return player.level.isEmptyBlock(pos);
    }
}
