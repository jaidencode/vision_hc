package org.main.vision.actions;

import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Automatically places blocks under the player while walking.
 */
public class ScaffoldHack extends ActionBase {
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;
        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        if (player.level == null) return;

        BlockPos below = new BlockPos(player.getX(), player.getY() - 1, player.getZ());
        BlockState state = player.level.getBlockState(below);
        if (!state.isAir()) return;

        int slot = findBlockSlot(player);
        if (slot < 0) return;

        int prev = player.inventory.selected;
        player.inventory.selected = slot;
        ClientPlayNetHandler conn = player.connection;
        if (conn != null) {
            BlockRayTraceResult rt = new BlockRayTraceResult(new Vector3d(0.5D, 0.0D, 0.5D), Direction.UP, below, false);
            conn.send(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, rt));
        }
        player.inventory.selected = prev;
    }

    private int findBlockSlot(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                return i;
            }
        }
        return -1;
    }
}
