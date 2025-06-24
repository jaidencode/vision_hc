package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Overlay that displays active hacks in the top right corner. */
@Mod.EventBusSubscriber(modid = "vision", value = Dist.CLIENT)
public class VisionOverlay {
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        MatrixStack ms = event.getMatrixStack();
        int width = mc.getWindow().getGuiScaledWidth();
        int y = 5;
        if (VisionClient.getSpeedHack().isEnabled()) {
            String text = "SpeedHack";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getJumpHack().isEnabled()) {
            String text = "JumpHack";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getFlyHack().isEnabled()) {
            String text = "FlyHack";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getJesusHack().isEnabled()) {
            String text = "Jesus";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getNoFallHack().isEnabled()) {
            String text = "NoFall";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getXRayHack().isEnabled()) {
            String text = "XRay";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getFullBrightHack().isEnabled()) {
            String text = "FullBright";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getBlinkHack().isEnabled()) {
            String text = "Blink";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getForceCritHack().isEnabled()) {
            String text = "ForceCrit";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getAntiVanishHack().isEnabled()) {
            String text = "AntiVanish";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getAntiKnockbackHack().isEnabled()) {
            String text = "AntiKnockback";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getAutoToolHack().isEnabled()) {
            String text = "AutoTool";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getSafeWalkHack().isEnabled()) {
            String text = "SafeWalk";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getAutoSprintHack().isEnabled()) {
            String text = "AutoSprint";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
        if (VisionClient.getAutoRespawnHack().isEnabled()) {
            String text = "AutoRespawn";
            int w = mc.font.width(text);
            mc.font.draw(ms, text, width - w - 5, y, 0xFFAA55FF);
            y += mc.font.lineHeight;
        }
    }
}
