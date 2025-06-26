package org.main.vision.actions;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Displays the contents of storage blocks when looking at them.
 */
public class InventoryPreviewHack extends ActionBase {
    private ItemStack[] preview = null;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) { preview = null; return; }

        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        RayTraceResult hit = mc.hitResult;
        if (hit == null || hit.getType() != RayTraceResult.Type.BLOCK) {
            preview = null;
            return;
        }

        BlockRayTraceResult block = (BlockRayTraceResult) hit;
        double distSq = player.position().distanceToSqr(Vector3d.atCenterOf(block.getBlockPos()));
        if (distSq > 25.0D) { preview = null; return; }

        TileEntity te = mc.level.getBlockEntity(block.getBlockPos());
        if (te instanceof IInventory) {
            IInventory inv = (IInventory) te;
            preview = new ItemStack[inv.getContainerSize()];
            for (int i = 0; i < inv.getContainerSize(); i++) {
                preview[i] = inv.getItem(i).copy();
            }
        } else {
            preview = null;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!isEnabled() || preview == null) return;

        Minecraft mc = Minecraft.getInstance();
        ItemRenderer renderer = mc.getItemRenderer();
        FontRenderer font = mc.font;
        MatrixStack ms = event.getMatrixStack();
        int startX = 10;
        int startY = 10;
        for (int i = 0; i < preview.length; i++) {
            ItemStack stack = preview[i];
            if (stack.isEmpty()) continue;
            int x = startX + (i % 9) * 18;
            int y = startY + (i / 9) * 18;
            renderer.renderAndDecorateItem(stack, x, y);
            renderer.renderGuiItemDecorations(font, stack, x, y);
        }
    }
}
