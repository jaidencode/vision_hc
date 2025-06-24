package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Automatically selects the best tool in the hotbar for the block being mined.
 */
public class AutoToolHack extends ActionBase {

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!isEnabled()) return;
        if (!(event.getPlayer() instanceof ClientPlayerEntity)) return;
        ClientPlayerEntity player = (ClientPlayerEntity) event.getPlayer();
        BlockPos pos = event.getPos();
        if (pos == null) return;

        int bestSlot = player.inventory.selected;
        float bestSpeed = event.getOriginalSpeed();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.getItem(i);
            float speed = stack.getDestroySpeed(event.getState());
            if (speed > bestSpeed + 0.1f) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }
        player.inventory.selected = bestSlot;
    }
}
