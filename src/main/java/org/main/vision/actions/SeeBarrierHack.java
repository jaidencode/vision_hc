package org.main.vision.actions;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/** Highlights barrier blocks to make them visible. */
public class SeeBarrierHack extends ActionBase {
    private final List<BlockPos> cache = new ArrayList<>();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        if (world == null) return;

        Vector3d cam = mc.gameRenderer.getMainCamera().getPosition();
        int radius = 16;
        cache.clear();
        BlockPos start = new BlockPos(cam.x - radius, cam.y - radius, cam.z - radius);
        BlockPos end = new BlockPos(cam.x + radius, cam.y + radius, cam.z + radius);
        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            if (world.getBlockState(pos).getBlock() == Blocks.BARRIER) {
                cache.add(pos.immutable());
            }
        }

        MatrixStack ms = event.getMatrixStack();
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        for (BlockPos pos : cache) {
            AxisAlignedBB box = world.getBlockState(pos).getShape(world, pos).bounds()
                    .move(pos).inflate(0.002D)
                    .move(-cam.x, -cam.y, -cam.z);
            drawOutline(ms, buffer, box);
        }
        buffer.endBatch(RenderType.lines());

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.lineWidth(2.0F);
        RenderSystem.depthMask(false);
        for (BlockPos pos : cache) {
            AxisAlignedBB box = world.getBlockState(pos).getShape(world, pos).bounds()
                    .move(pos).inflate(0.002D)
                    .move(-cam.x, -cam.y, -cam.z);
            drawOutlineGL(ms, box);
        }
        RenderSystem.depthMask(true);
        RenderSystem.lineWidth(1.0F);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    private static void drawOutline(MatrixStack ms, IRenderTypeBuffer buffer, AxisAlignedBB box) {
        IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
        WorldRenderer.renderLineBox(ms, builder, box, 1.0F, 0.0F, 0.0F, 1.0F);
    }

    private static void drawOutlineGL(MatrixStack ms, AxisAlignedBB box) {
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(ms.last().pose());
        GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(box.minX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
        GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.minZ);
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);
        GL11.glVertex3d(box.minX, box.minY, box.minZ);
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
        GL11.glEnd();
        RenderSystem.popMatrix();
    }
}
