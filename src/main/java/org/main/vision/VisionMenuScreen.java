package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import org.main.vision.actions.SpeedHack;
import org.main.vision.config.UIState;

/** Simple in-game menu with a draggable bar and dropdown for hacks. */
public class VisionMenuScreen extends Screen {
    private final Minecraft mc = Minecraft.getInstance();
    private final UIState state;
    private Button hackButton;
    private boolean dragging;
    private int dragOffsetX, dragOffsetY;

    public VisionMenuScreen() {
        super(new StringTextComponent("Vision Menu"));
        this.state = UIState.load();
    }

    @Override
    protected void init() {
        int width = 100;
        int height = 20;
        this.hackButton = addButton(new Button(state.miscBarX, state.miscBarY + 20, width, height,
                getHackLabel(), b -> toggleHack()));
        hackButton.visible = state.hacksExpanded;
    }

    private void toggleHack() {
        SpeedHack hack = VisionClient.getSpeedHack();
        hack.toggle();
        hackButton.setMessage(getHackLabel());
        state.save();
    }

    private StringTextComponent getHackLabel() {
        return new StringTextComponent((VisionClient.getSpeedHack().isEnabled() ? "Disable" : "Enable") + " Speed");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= state.miscBarX && mouseX <= state.miscBarX + 100 && mouseY >= state.miscBarY && mouseY <= state.miscBarY + 20) {
            // click on bar toggles dropdown
            state.hacksExpanded = !state.hacksExpanded;
            hackButton.visible = state.hacksExpanded;
            state.save();
            dragging = true;
            dragOffsetX = (int)mouseX - state.miscBarX;
            dragOffsetY = (int)mouseY - state.miscBarY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            state.miscBarX = (int)mouseX - dragOffsetX;
            state.miscBarY = (int)mouseY - dragOffsetY;
            hackButton.x = state.miscBarX;
            hackButton.y = state.miscBarY + 20;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragging = false;
            state.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrices);
        fill(matrices, state.miscBarX, state.miscBarY, state.miscBarX + 100, state.miscBarY + 20, 0x80000000);
        drawCenteredString(matrices, font, "Misc", state.miscBarX + 50, state.miscBarY + 6, 0xFFFFFF);
        if (state.hacksExpanded) {
            hackButton.visible = true;
        }
        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        state.save();
        super.onClose();
    }
}
