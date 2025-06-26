package org.main.vision.actions;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Renders extended nametag info above entities.
 */
public class NameTagsHack extends ActionBase {
    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        if (!isEnabled()) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (entity instanceof PlayerEntity && entity == Minecraft.getInstance().player) return;

        Vector3d camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double dist = entity.position().distanceTo(camPos);
        String name = entity.getName().getString();
        String health = String.format("HP: %.0f%%",
                (entity.getHealth() / Math.max(1.0f, entity.getMaxHealth())) * 100.0f);
        String distance = String.format("Dist: %.1fm", dist);
        String coords = String.format("XYZ: %d %d %d",
                (int) entity.getX(), (int) entity.getY(), (int) entity.getZ());
        String[] lines = new String[] { name, health + " " + distance, coords };

        MatrixStack ms = event.getMatrixStack();
        EntityRendererManager rm = Minecraft.getInstance().getEntityRenderDispatcher();
        ms.pushPose();
        ms.translate(0.0D, entity.getBbHeight() + 0.5D, 0.0D);
        ms.mulPose(rm.cameraOrientation());
        ms.scale(-0.025F, -0.025F, 0.025F);
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        int lineHeight = Minecraft.getInstance().font.lineHeight;
        int boxWidth = 0;
        for (String s : lines) {
            boxWidth = Math.max(boxWidth, Minecraft.getInstance().font.width(s));
        }
        int half = boxWidth / 2 + 2;
        net.minecraft.client.gui.AbstractGui.fill(ms, -half, -2, half, lines.length * lineHeight + 2, 0x55000000);

        for (int i = 0; i < lines.length; i++) {
            String s = lines[i];
            float x = -Minecraft.getInstance().font.width(s) / 2f;
            Minecraft.getInstance().font.draw(ms, s, x, i * lineHeight, 0xFFFFFFFF);
        }

        buffer.endBatch(RenderType.lines());
        ms.popPose();
    }
}
