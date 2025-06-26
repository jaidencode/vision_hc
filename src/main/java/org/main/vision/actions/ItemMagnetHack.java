package org.main.vision.actions;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/** Pulls nearby dropped items directly to the player. */
public class ItemMagnetHack extends ActionBase {
    private static final double RANGE = 5.0D;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        PlayerEntity player = event.player;
        if (player.level == null) return;
        AxisAlignedBB box = player.getBoundingBox().inflate(RANGE, RANGE, RANGE);
        List<ItemEntity> items = player.level.getEntitiesOfClass(ItemEntity.class, box);
        for (ItemEntity item : items) {
            item.setPos(player.getX(), player.getY(), player.getZ());
        }
    }
}
