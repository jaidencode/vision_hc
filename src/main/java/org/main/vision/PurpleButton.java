package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

/** Simple button with a purple color scheme. */
public class PurpleButton extends Button {
    public PurpleButton(int x, int y, int width, int height, ITextComponent title, IPressable onPress) {
        super(x, y, width, height, title, onPress);
    }

    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        int color = isHovered ? 0xFFAA55FF : 0xCC7722AA;
        fill(ms, x, y, x + width, y + height, color);
        drawCenteredString(ms, Minecraft.getInstance().font, getMessage(), x + width / 2, y + (height - 8) / 2, 0xFFFFFF);
    }
}
