package org.main.vision.actions;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Predicts other players' movement and renders translucent ghosts ahead of them.
 */
public class GhostPredictHack extends ActionBase {
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        PlayerEntity self = mc.player;
        if (world == null || self == null) return;

        MatrixStack ms = event.getMatrixStack();
        IRenderTypeBuffer.Impl buffer = mc.renderBuffers().bufferSource();
        EntityRendererManager rm = mc.getEntityRenderDispatcher();
        Vector3d cam = mc.gameRenderer.getMainCamera().getPosition();
        float partial = event.getPartialTicks();

        for (PlayerEntity p : world.players()) {
            if (p == self) continue;
            Vector3d predicted = p.position().add(p.getDeltaMovement().scale(20.0D));
            RemoteClientPlayerEntity ghost = new RemoteClientPlayerEntity(world, new GameProfile(p.getUUID(), p.getName().getString()));
            ghost.yRot = p.yRot;
            ghost.xRot = p.xRot;
            ghost.yHeadRot = p.yHeadRot;
            ms.pushPose();
            ms.translate(predicted.x - cam.x, predicted.y - cam.y, predicted.z - cam.z);
            rm.render(ghost, 0.0D, 0.0D, 0.0D, ghost.yRot, partial, ms, buffer, 0x00F000F0);
            ms.popPose();
        }
        buffer.endBatch();
    }
}
