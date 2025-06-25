package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Field;

/**
 * Reduces the time required to consume food or potions.
 */
public class FastEatHack extends ActionBase {
    private static Field useItemField;
    static {
        try {
            useItemField = ClientPlayerEntity.class.getDeclaredField("useItemRemaining");
            useItemField.setAccessible(true);
        } catch (Exception e) {
            try {
                useItemField = ClientPlayerEntity.class.getDeclaredField("useItemRemainingTicks");
                useItemField.setAccessible(true);
            } catch (Exception ex) {
                useItemField = null;
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;
        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        if (!player.isUsingItem() || useItemField == null) return;
        try {
            int ticks = (int) useItemField.get(player);
            if (ticks > 2) {
                useItemField.set(player, 2);
            }
        } catch (Exception ignored) {
        }
    }
}
