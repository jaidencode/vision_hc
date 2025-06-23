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
        drawCenteredString(ms, Minecraft.getInstance().font, getMessage(), x + width / 2, y + (height - 8) / 2, (int)(alpha * 255.0f) << 24 | 0xFFFFFF);
    }
}
