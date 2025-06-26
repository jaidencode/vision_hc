package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Swaps the camera to the entity the player is looking at.
 */
public class PerspectiveSwapHack extends ActionBase {
    private Entity target;

    @Override
    protected void onEnable() {
        target = null;
    }

    @SubscribeEvent
    public void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        RayTraceResult result = player.pick(50.0D, 1.0F, false);
        if (result.getType() == RayTraceResult.Type.ENTITY) {
            Entity newTarget = ((EntityRayTraceResult) result).getEntity();
            if (newTarget != target) {
                target = newTarget;
                mc.setCameraEntity(target);
            }
        }
    }

    @Override
    protected void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.setCameraEntity(mc.player);
        }
        target = null;
    }
}
