package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import org.main.vision.PurpleButton;
import net.minecraft.util.text.StringTextComponent;
import org.main.vision.config.UIState;
import org.main.vision.actions.SpeedHack;
import org.main.vision.VisionClient;
import org.main.vision.HackSettingsScreen;
import org.main.vision.SpeedSettingsScreen;

/** Simple in-game menu with a draggable bar and dropdown for hacks. */
public class VisionMenuScreen extends Screen {
    private final Minecraft mc = Minecraft.getInstance();
    private final UIState state;
    private PurpleButton speedButton;
    private PurpleButton jumpButton;
    private PurpleButton flyButton;
    private PurpleButton jesusButton;
    private PurpleButton noFallButton;
    private PurpleButton xrayButton;
    private PurpleButton speedSettings;
    private PurpleButton jumpSettings;
    private PurpleButton flySettings;
    private PurpleButton jesusSettings;
    private PurpleButton noFallSettings;
    private PurpleButton xraySettings;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BAR_WIDTH = BUTTON_WIDTH + 25;
    private boolean dragging;
    private int dragOffsetX, dragOffsetY;
    private int dragStartX, dragStartY;
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
        int width = BUTTON_WIDTH;
        int height = BUTTON_HEIGHT;
        this.speedButton = addButton(new PurpleButton(state.miscBarX, state.miscBarY + 20 + (int)(20 * dropdownProgress) - 20, width, height,
                getSpeedLabel(), b -> toggleSpeed()));
        this.speedSettings = addButton(new PurpleButton(state.miscBarX + width + 5, state.miscBarY + 20 + (int)(20 * dropdownProgress) - 20, 20, height,
                new StringTextComponent("\u2699"), b -> openSpeedSettings()));

        this.jumpButton = addButton(new PurpleButton(state.miscBarX, state.miscBarY + 40 + (int)(20 * dropdownProgress) - 20, width, height,
                getJumpLabel(), b -> toggleJump()));
        this.jumpSettings = addButton(new PurpleButton(state.miscBarX + width + 5, state.miscBarY + 40 + (int)(20 * dropdownProgress) - 20, 20, height,
                new StringTextComponent("\u2699"), b -> openJumpSettings()));

        this.flyButton = addButton(new PurpleButton(state.miscBarX, state.miscBarY + 60 + (int)(20 * dropdownProgress) - 20, width, height,
                getFlyLabel(), b -> toggleFly()));
        this.flySettings = addButton(new PurpleButton(state.miscBarX + width + 5, state.miscBarY + 60 + (int)(20 * dropdownProgress) - 20, 20, height,
                new StringTextComponent("\u2699"), b -> openFlySettings()));

        this.jesusButton = addButton(new PurpleButton(state.miscBarX, state.miscBarY + 80 + (int)(20 * dropdownProgress) - 20, width, height,
                getJesusLabel(), b -> toggleJesus()));
        this.jesusSettings = addButton(new PurpleButton(state.miscBarX + width + 5, state.miscBarY + 80 + (int)(20 * dropdownProgress) - 20, 20, height,
                new StringTextComponent("\u2699"), b -> openJesusSettings()));

        this.noFallButton = addButton(new PurpleButton(state.miscBarX, state.miscBarY + 100 + (int)(20 * dropdownProgress) - 20, width, height,
                getNoFallLabel(), b -> toggleNoFall()));
        this.noFallSettings = addButton(new PurpleButton(state.miscBarX + width + 5, state.miscBarY + 100 + (int)(20 * dropdownProgress) - 20, 20, height,
                new StringTextComponent("\u2699"), b -> openNoFallSettings()));

        this.xrayButton = addButton(new PurpleButton(state.miscBarX, state.miscBarY + 120 + (int)(20 * dropdownProgress) - 20, width, height,
                getXRayLabel(), b -> toggleXRay()));
        this.xraySettings = addButton(new PurpleButton(state.miscBarX + width + 5, state.miscBarY + 120 + (int)(20 * dropdownProgress) - 20, 20, height,
                new StringTextComponent("\u2699"), b -> openXRaySettings()));

        speedButton.visible = jumpButton.visible = flyButton.visible = jesusButton.visible = noFallButton.visible = xrayButton.visible = dropdownProgress > 0.05f;
        speedSettings.visible = jumpSettings.visible = flySettings.visible = jesusSettings.visible = noFallSettings.visible = xraySettings.visible = dropdownProgress > 0.05f;
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

    private void toggleJesus() {
        VisionClient.getJesusHack().toggle();
        jesusButton.setMessage(getJesusLabel());
        state.save();
    }

    private void toggleNoFall() {
        VisionClient.getNoFallHack().toggle();
        noFallButton.setMessage(getNoFallLabel());
        state.save();
    }

    private void toggleXRay() {
        VisionClient.getXRayHack().toggle();
        xrayButton.setMessage(getXRayLabel());
        state.save();
    }

    private void openSpeedSettings() {
        this.minecraft.setScreen(new SpeedSettingsScreen(this));
    }

    private void openJumpSettings() {
        this.minecraft.setScreen(new HackSettingsScreen(this, "Jump", () -> VisionClient.getSettings().jumpVelocity,
                v -> {VisionClient.getSettings().jumpVelocity = v;}, VisionClient::saveSettings, 1.2D));
    }

    private void openFlySettings() {
        this.minecraft.setScreen(new HackSettingsScreen(this, "FlySpeed", () -> VisionClient.getSettings().flyHorizontalSpeed,
                v -> {VisionClient.getSettings().flyHorizontalSpeed = v;}, VisionClient::saveSettings, 0.75D));
    }

    private void openJesusSettings() {
        this.minecraft.setScreen(new HackSettingsScreen(this, "Buoyancy", () -> VisionClient.getSettings().jesusBuoyancy,
                v -> {VisionClient.getSettings().jesusBuoyancy = v;}, VisionClient::saveSettings, 0.0D));
    }

    private void openNoFallSettings() {
        this.minecraft.setScreen(new HackSettingsScreen(this, "Threshold", () -> VisionClient.getSettings().noFallThreshold,
                v -> {VisionClient.getSettings().noFallThreshold = v;}, VisionClient::saveSettings, 2.0D));
    }

    private void openXRaySettings() {
        this.minecraft.setScreen(new XRaySettingsScreen(this));
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

    private StringTextComponent getJesusLabel() {
        return new StringTextComponent((VisionClient.getJesusHack().isEnabled() ? "Disable" : "Enable") + " Jesus");
    }

    private StringTextComponent getNoFallLabel() {
        return new StringTextComponent((VisionClient.getNoFallHack().isEnabled() ? "Disable" : "Enable") + " NoFall");
    }

    private StringTextComponent getXRayLabel() {
        return new StringTextComponent((VisionClient.getXRayHack().isEnabled() ? "Disable" : "Enable") + " XRay");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= state.miscBarX && mouseX <= state.miscBarX + BUTTON_WIDTH && mouseY >= state.miscBarY && mouseY <= state.miscBarY + 20) {
            dragging = true;
            dragOffsetX = (int)mouseX - state.miscBarX;
            dragOffsetY = (int)mouseY - state.miscBarY;
            dragStartX = (int)mouseX;
            dragStartY = (int)mouseY;
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
            jesusButton.x = state.miscBarX;
            noFallButton.x = state.miscBarX;
            xrayButton.x = state.miscBarX;
            speedSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
            jumpSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
            flySettings.x = state.miscBarX + BUTTON_WIDTH + 5;
            jesusSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
            noFallSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
            xraySettings.x = state.miscBarX + BUTTON_WIDTH + 5;

            speedButton.y = state.miscBarY + 20 + (int)(20 * dropdownProgress) - 20;
            jumpButton.y = state.miscBarY + 40 + (int)(20 * dropdownProgress) - 20;
            flyButton.y = state.miscBarY + 60 + (int)(20 * dropdownProgress) - 20;
            jesusButton.y = state.miscBarY + 80 + (int)(20 * dropdownProgress) - 20;
            noFallButton.y = state.miscBarY + 100 + (int)(20 * dropdownProgress) - 20;
            xrayButton.y = state.miscBarY + 120 + (int)(20 * dropdownProgress) - 20;
            speedSettings.y = speedButton.y;
            jumpSettings.y = jumpButton.y;
            flySettings.y = flyButton.y;
            jesusSettings.y = jesusButton.y;
            noFallSettings.y = noFallButton.y;
            xraySettings.y = xrayButton.y;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragging = false;
            if (Math.abs(mouseX - dragStartX) < 5 && Math.abs(mouseY - dragStartY) < 5) {
                state.hacksExpanded = !state.hacksExpanded;
                dropdownTarget = state.hacksExpanded;
            }
            state.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrices);
        fill(matrices, state.miscBarX, state.miscBarY, state.miscBarX + BAR_WIDTH, state.miscBarY + 20, 0xAA5511AA);
        drawCenteredString(matrices, font, "Misc", state.miscBarX + 50, state.miscBarY + 6, 0xFFFFFF);
        dropdownProgress += ((dropdownTarget ? 1.0f : 0.0f) - dropdownProgress) * 0.2f;
        dropdownProgress = Math.max(0.0f, Math.min(1.0f, dropdownProgress));

        int offsetY = (int)(20 * dropdownProgress);
        speedButton.x = state.miscBarX;
        jumpButton.x = state.miscBarX;
        flyButton.x = state.miscBarX;
        jesusButton.x = state.miscBarX;
        noFallButton.x = state.miscBarX;
        xrayButton.x = state.miscBarX;
        speedSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
        jumpSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
        flySettings.x = state.miscBarX + BUTTON_WIDTH + 5;
        jesusSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
        noFallSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
        xraySettings.x = state.miscBarX + BUTTON_WIDTH + 5;

        speedButton.y = state.miscBarY + 20 + offsetY - 20;
        jumpButton.y = state.miscBarY + 40 + offsetY - 20;
        flyButton.y = state.miscBarY + 60 + offsetY - 20;
        jesusButton.y = state.miscBarY + 80 + offsetY - 20;
        noFallButton.y = state.miscBarY + 100 + offsetY - 20;
        xrayButton.y = state.miscBarY + 120 + offsetY - 20;
        speedSettings.y = speedButton.y;
        jumpSettings.y = jumpButton.y;
        flySettings.y = flyButton.y;
        jesusSettings.y = jesusButton.y;
        noFallSettings.y = noFallButton.y;
        xraySettings.y = xrayButton.y;

        boolean vis = dropdownProgress > 0.05f;
        speedButton.visible = jumpButton.visible = flyButton.visible = jesusButton.visible = noFallButton.visible = xrayButton.visible = vis;
        speedSettings.visible = jumpSettings.visible = flySettings.visible = jesusSettings.visible = noFallSettings.visible = xraySettings.visible = vis;
        speedButton.setAlpha(dropdownProgress);
        jumpButton.setAlpha(dropdownProgress);
        flyButton.setAlpha(dropdownProgress);
        jesusButton.setAlpha(dropdownProgress);
        noFallButton.setAlpha(dropdownProgress);
        xrayButton.setAlpha(dropdownProgress);
        speedSettings.setAlpha(dropdownProgress);
        jumpSettings.setAlpha(dropdownProgress);
        flySettings.setAlpha(dropdownProgress);
        jesusSettings.setAlpha(dropdownProgress);
        noFallSettings.setAlpha(dropdownProgress);
        xraySettings.setAlpha(dropdownProgress);

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        state.save();
        super.onClose();
    }
}
