package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

/**
 * Swaps the camera to the entity the player is looking at.
 */
public class PerspectiveSwapHack extends ActionBase {
    private Entity target;

    @Override
    protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            setEnabled(false);
            return;
        }
        RayTraceResult result = player.pick(50.0D, 1.0F, false);
        if (result.getType() == RayTraceResult.Type.ENTITY) {
            target = ((EntityRayTraceResult) result).getEntity();
            mc.setCameraEntity(target);
        } else {
            target = null;
            setEnabled(false);
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
