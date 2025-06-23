package org.main.vision.actions;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.main.vision.VisionClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Highlights selected blocks through walls when enabled.
 */
public class XRayHack extends ActionBase {
    private final List<BlockPos> highlightCache = new ArrayList<>();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        if (world == null) return;

        MatrixStack ms = event.getMatrixStack();
        Vector3d cam = mc.gameRenderer.getMainCamera().getPosition();
        int radius = 24;
        highlightCache.clear();
        BlockPos start = new BlockPos(cam.x - radius, cam.y - radius, cam.z - radius);
        BlockPos end = new BlockPos(cam.x + radius, cam.y + radius, cam.z + radius);
        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            if (VisionClient.getSettings().isXrayTarget(world.getBlockState(pos).getBlock())) {
                highlightCache.add(pos.immutable());
            }
        }

        IRenderTypeBuffer.Impl buffer = mc.renderBuffers().bufferSource();

        // Render outlines with depth testing disabled so they remain visible through walls
        com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
        for (BlockPos pos : highlightCache) {
            AxisAlignedBB box = world.getBlockState(pos).getShape(world, pos).bounds()
                    .move(pos).inflate(0.002D)
                    .move(-cam.x, -cam.y, -cam.z);
            WorldRenderer.renderLineBox(ms, buffer.getBuffer(RenderType.lines()), box, 0.0f, 1.0f, 0.0f, 1.0f);
        }
        buffer.endBatch(RenderType.lines());
        com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
    }
}
