package org.main.vision.actions;

import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Automatically selects the best tool from the hotbar when starting to mine a block.
 */
public class AutoToolHack extends ActionBase {
    @SubscribeEvent
    public void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (!isEnabled()) return;
        if (!(event.getPlayer() instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.getPlayer();
        BlockPos pos = event.getPos();
        BlockState state = player.level.getBlockState(pos);
        int bestSlot = findBestToolSlot(player, state);
        if (bestSlot >= 0 && bestSlot != player.inventory.selected) {
            player.inventory.selected = bestSlot;
        }
    }

    private int findBestToolSlot(ClientPlayerEntity player, BlockState state) {
        float bestSpeed = 0.0f;
        int bestSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.getItem(i);
            float speed = stack.getDestroySpeed(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }
        return bestSlot;
    }
}
