package org.main.vision.actions;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Detects vanished players and draws a purple outline around them. */
public class AntiVanishHack extends ActionBase {
    private final Set<UUID> vanished = new HashSet<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) { vanished.clear(); return; }

        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        if (world == null || mc.getConnection() == null) return;

        Set<UUID> tab = new HashSet<>();
        for (NetworkPlayerInfo info : mc.getConnection().getOnlinePlayers()) {
            tab.add(info.getProfile().getId());
        }

        for (AbstractClientPlayerEntity p : world.players()) {
            UUID id = p.getGameProfile().getId();
            if (!tab.contains(id) && !id.equals(mc.player.getUUID())) {
                vanished.add(id);
            } else {
                vanished.remove(id);
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        if (world == null) return;

        Vector3d cam = mc.gameRenderer.getMainCamera().getPosition();
        MatrixStack ms = event.getMatrixStack();
        IRenderTypeBuffer.Impl buffer = mc.renderBuffers().bufferSource();

        for (UUID id : vanished) {
            Entity e = world.getPlayerByUUID(id);
            if (e != null) {
                AxisAlignedBB box = e.getBoundingBox().inflate(0.002D).move(-cam.x, -cam.y, -cam.z);
                drawOutline(ms, buffer, box);
            }
        }
        buffer.endBatch(RenderType.lines());
    }

    private static void drawOutline(MatrixStack ms, IRenderTypeBuffer buffer, AxisAlignedBB box) {
        IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
        WorldRenderer.renderLineBox(ms, builder, box, 1.0F, 0.0F, 1.0F, 1.0F);
    }
}
