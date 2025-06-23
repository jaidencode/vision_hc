package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import org.main.vision.PurpleButton;
import net.minecraft.util.text.StringTextComponent;
import org.main.vision.config.UIState;

/** Simple in-game menu with a draggable bar and dropdown for hacks. */
public class VisionMenuScreen extends Screen {
    private final Minecraft mc = Minecraft.getInstance();
    private final UIState state;
    private PurpleButton speedButton;
    private PurpleButton jumpButton;
    private PurpleButton flyButton;
    private boolean dragging;
    private int dragOffsetX, dragOffsetY;
    private float dropdownProgress;
    private boolean dropdownTarget;

    public VisionMenuScreen() {
        super(new StringTextComponent("Vision Menu"));
        this.state = UIState.load();
        this.dropdownTarget = state.hacksExpanded;
        this.dropdownProgress = state.hacksExpanded ? 1.0f : 0.0f;
    }

    @Override
    protected void init() {
        int width = 100;
        int height = 20;
        this.speedButton = addButton(new PurpleButton(state.miscBarX, state.miscBarY + 20 + (int)(20 * dropdownProgress) - 20, width, height,
                getSpeedLabel(), b -> toggleSpeed()));
        this.jumpButton = addButton(new PurpleButton(state.miscBarX, state.miscBarY + 40 + (int)(20 * dropdownProgress) - 20, width, height,
                getJumpLabel(), b -> toggleJump()));
        this.flyButton = addButton(new PurpleButton(state.miscBarX, state.miscBarY + 60 + (int)(20 * dropdownProgress) - 20, width, height,
                getFlyLabel(), b -> toggleFly()));
        speedButton.visible = jumpButton.visible = flyButton.visible = dropdownProgress > 0.05f;
    }

    private void toggleSpeed() {
        SpeedHack hack = VisionClient.getSpeedHack();
        hack.toggle();
        speedButton.setMessage(getSpeedLabel());
        state.save();
    }

    private void toggleJump() {
        VisionClient.getJumpHack().toggle();
        jumpButton.setMessage(getJumpLabel());
        state.save();
    }

    private void toggleFly() {
        VisionClient.getFlyHack().toggle();
        flyButton.setMessage(getFlyLabel());
        state.save();
    }

    private StringTextComponent getSpeedLabel() {
        return new StringTextComponent((VisionClient.getSpeedHack().isEnabled() ? "Disable" : "Enable") + " Speed");
    }

    private StringTextComponent getJumpLabel() {
        return new StringTextComponent((VisionClient.getJumpHack().isEnabled() ? "Disable" : "Enable") + " Jump");
    }

    private StringTextComponent getFlyLabel() {
        return new StringTextComponent((VisionClient.getFlyHack().isEnabled() ? "Disable" : "Enable") + " Fly");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= state.miscBarX && mouseX <= state.miscBarX + 100 && mouseY >= state.miscBarY && mouseY <= state.miscBarY + 20) {
            // click on bar toggles dropdown
            state.hacksExpanded = !state.hacksExpanded;
            dropdownTarget = state.hacksExpanded;
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
            speedButton.x = state.miscBarX;
            jumpButton.x = state.miscBarX;
            flyButton.x = state.miscBarX;
            speedButton.y = state.miscBarY + 20 + (int)(20 * dropdownProgress) - 20;
            jumpButton.y = state.miscBarY + 40 + (int)(20 * dropdownProgress) - 20;
            flyButton.y = state.miscBarY + 60 + (int)(20 * dropdownProgress) - 20;
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
        fill(matrices, state.miscBarX, state.miscBarY, state.miscBarX + 100, state.miscBarY + 20, 0xAA5511AA);
        drawCenteredString(matrices, font, "Misc", state.miscBarX + 50, state.miscBarY + 6, 0xFFFFFF);
        dropdownProgress += ((dropdownTarget ? 1.0f : 0.0f) - dropdownProgress) * 0.2f;
        dropdownProgress = Math.max(0.0f, Math.min(1.0f, dropdownProgress));

        int offsetY = (int)(20 * dropdownProgress);
        speedButton.x = state.miscBarX;
        jumpButton.x = state.miscBarX;
        flyButton.x = state.miscBarX;
        speedButton.y = state.miscBarY + 20 + offsetY - 20;
        jumpButton.y = state.miscBarY + 40 + offsetY - 20;
        flyButton.y = state.miscBarY + 60 + offsetY - 20;
        speedButton.visible = jumpButton.visible = flyButton.visible = dropdownProgress > 0.05f;
        speedButton.setAlpha(dropdownProgress);
        jumpButton.setAlpha(dropdownProgress);
        flyButton.setAlpha(dropdownProgress);

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        state.save();
        super.onClose();
    }
}
