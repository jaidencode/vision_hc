package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.block.Block;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import com.mojang.blaze3d.vertex.IVertexBuilder;
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
    private static final java.util.Map<String, float[]> COLOR_MAP = new java.util.HashMap<>();

    static {
        COLOR_MAP.put("minecraft:diamond_ore", new float[]{0.0f, 0.7f, 1.0f});
        COLOR_MAP.put("minecraft:emerald_ore", new float[]{0.0f, 1.0f, 0.0f});
        COLOR_MAP.put("minecraft:gold_ore", new float[]{1.0f, 0.84f, 0.0f});
        COLOR_MAP.put("minecraft:iron_ore", new float[]{0.8f, 0.8f, 0.8f});
        COLOR_MAP.put("minecraft:coal_ore", new float[]{0.1f, 0.1f, 0.1f});
        COLOR_MAP.put("minecraft:lapis_ore", new float[]{0.25f, 0.5f, 1.0f});
        COLOR_MAP.put("minecraft:redstone_ore", new float[]{1.0f, 0.0f, 0.0f});
        COLOR_MAP.put("minecraft:ancient_debris", new float[]{0.55f, 0.27f, 0.07f});
    }

    private static float[] getColor(Block block) {
        if (block == null || block.getRegistryName() == null) return new float[]{1.0f, 1.0f, 1.0f};
        return COLOR_MAP.getOrDefault(block.getRegistryName().toString(), new float[]{1.0f, 1.0f, 1.0f});
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        if (world == null) return;

        Vector3d cam = mc.gameRenderer.getMainCamera().getPosition();
        int radius = 16;
        highlightCache.clear();
        BlockPos start = new BlockPos(cam.x - radius, cam.y - radius, cam.z - radius);
        BlockPos end = new BlockPos(cam.x + radius, cam.y + radius, cam.z + radius);
        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            if (VisionClient.getSettings().isXrayTarget(world.getBlockState(pos).getBlock())) {
                highlightCache.add(pos.immutable());
            }
        }

        MatrixStack ms = event.getMatrixStack();
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        // First render using Minecraft's normal line renderer with depth testing
        for (BlockPos pos : highlightCache) {
            AxisAlignedBB box = world.getBlockState(pos).getShape(world, pos).bounds()
                    .move(pos).inflate(0.002D)
                    .move(-cam.x, -cam.y, -cam.z);
            float[] col = getColor(world.getBlockState(pos).getBlock());
            drawOutline(ms, buffer, box, col);
        }
        buffer.endBatch(RenderType.lines());

        // Then render outlines again with depth disabled so they show through walls
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.lineWidth(2.0F);
        RenderSystem.depthMask(false);
        for (BlockPos pos : highlightCache) {
            AxisAlignedBB box = world.getBlockState(pos).getShape(world, pos).bounds()
                    .move(pos).inflate(0.002D)
                    .move(-cam.x, -cam.y, -cam.z);
            float[] col = getColor(world.getBlockState(pos).getBlock());
            drawOutlineGL(ms, box, col);
        }
        RenderSystem.depthMask(true);
        RenderSystem.lineWidth(1.0F);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    /** Draws a colored outline around the given bounding box using Minecraft's renderer. */
    private static void drawOutline(MatrixStack ms, IRenderTypeBuffer buffer, AxisAlignedBB box, float[] color) {
        IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
        WorldRenderer.renderLineBox(ms, builder, box, color[0], color[1], color[2], 1.0F);
    }

    /**
     * Draw the given bounding box using raw OpenGL so that depth testing can
     * be disabled, keeping the outline visible through walls.
     */
    private static void drawOutlineGL(MatrixStack ms, AxisAlignedBB box, float[] color) {
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(ms.last().pose());
        GL11.glColor4f(color[0], color[1], color[2], 1.0f);
        GL11.glBegin(GL11.GL_LINES);

        // bottom rectangle
        GL11.glVertex3d(box.minX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.minZ);

        GL11.glVertex3d(box.maxX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.maxZ);

        GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.maxZ);

        GL11.glVertex3d(box.minX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.minZ);

        // top rectangle
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.minZ);

        GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);

        GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.maxZ);

        GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);

        // vertical lines
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
