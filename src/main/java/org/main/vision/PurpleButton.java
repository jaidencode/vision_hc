package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

/** Simple button with a purple color scheme. */
public class PurpleButton extends Button {
    private float alpha = 1.0f;

    public PurpleButton(int x, int y, int width, int height, ITextComponent title, IPressable onPress) {
        super(x, y, width, height, title, onPress);
    }

    public void setAlpha(float a) {
        this.alpha = a;
    }

    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        int start = isHovered ? 0xFFAA55FF : 0xCC7722AA;
        int end = isHovered ? 0xFF7744FF : 0xAA5511AA;
        start = (start & 0x00FFFFFF) | ((int)(alpha * ((start >> 24) & 0xFF)) << 24);
        end = (end & 0x00FFFFFF) | ((int)(alpha * ((end >> 24) & 0xFF)) << 24);
        fillGradient(ms, x, y, x + width, y + height, start, end);

        Minecraft mc = Minecraft.getInstance();
        String text = getMessage().getString();
        int textWidth = mc.font.width(text);
        float scale = 1.0f;
        if (textWidth > width - 4) {
            scale = (width - 4) / (float) textWidth;
        }
        ms.pushPose();
        ms.translate(x + width / 2f, y + (height - mc.font.lineHeight * scale) / 2f, 0);
        ms.scale(scale, scale, 1.0f);
        int color = ((int)(alpha * 255.0f) << 24) | 0xFFFFFF;
        mc.font.draw(ms, text, -textWidth / 2f, 0, color);
        ms.popPose();
    }
}
