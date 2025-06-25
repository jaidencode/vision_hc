package org.main.vision.actions;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Renders health values above entities.
 */
public class HealthDisplayHack extends ActionBase {
    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        if (!isEnabled()) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (entity instanceof PlayerEntity && entity == Minecraft.getInstance().player) return;
        float health = entity.getHealth();
        String text = String.format("%.0f", health);
        MatrixStack ms = event.getMatrixStack();
        EntityRendererManager rm = Minecraft.getInstance().getEntityRenderDispatcher();
        ms.pushPose();
        ms.translate(0.0D, entity.getBbHeight() + 0.5D, 0.0D);
        ms.mulPose(rm.cameraOrientation());
        ms.scale(-0.025F, -0.025F, 0.025F);
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        float w = Minecraft.getInstance().font.width(text) / 2f;
        Minecraft.getInstance().font.draw(ms, text, -w, 0, 0xFFFFFFFF);
        buffer.endBatch();
        ms.popPose();
    }
}
