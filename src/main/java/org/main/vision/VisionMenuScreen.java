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
import org.main.vision.config.HackSettings;
import org.main.vision.UsernameSettingsScreen;

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
    private PurpleButton fullBrightButton;
    private PurpleButton chestButton;
    private PurpleButton blinkButton;
    private PurpleButton usernameButton;
    private PurpleButton usernameSettings;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BAR_WIDTH = BUTTON_WIDTH + 25;
    private boolean dragging;
    private boolean draggingRender;
    private boolean draggingUtil;
    private int dragOffsetX, dragOffsetY;
    private int dragStartX, dragStartY;
    private int renderDragOffsetX, renderDragOffsetY;
    private int renderDragStartX, renderDragStartY;
    private int utilDragOffsetX, utilDragOffsetY;
    private int utilDragStartX, utilDragStartY;
    private float dropdownProgress;
    private float renderDropdownProgress;
    private float utilDropdownProgress;
    private boolean dropdownTarget;
    private boolean renderDropdownTarget;
    private boolean utilDropdownTarget;

    public VisionMenuScreen() {
        super(new StringTextComponent("Vision Menu"));
        this.state = UIState.load();
        this.dropdownTarget = state.hacksExpanded;
        this.renderDropdownTarget = state.renderExpanded;
        this.utilDropdownTarget = state.utilExpanded;
        this.dropdownProgress = state.hacksExpanded ? 1.0f : 0.0f;
        this.renderDropdownProgress = state.renderExpanded ? 1.0f : 0.0f;
        this.utilDropdownProgress = state.utilExpanded ? 1.0f : 0.0f;
    }

    @Override
    protected void init() {
        int width = BUTTON_WIDTH;
        int height = BUTTON_HEIGHT;
        // Movement bar
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

        this.blinkButton = addButton(new PurpleButton(state.miscBarX, state.miscBarY + 120 + (int)(20 * dropdownProgress) - 20, width, height,
                getBlinkLabel(), b -> toggleBlink()));

        speedButton.visible = jumpButton.visible = flyButton.visible = jesusButton.visible = noFallButton.visible = blinkButton.visible = dropdownProgress > 0.05f;
        speedSettings.visible = jumpSettings.visible = flySettings.visible = jesusSettings.visible = noFallSettings.visible = dropdownProgress > 0.05f;

        // Render bar
        this.xrayButton = addButton(new PurpleButton(state.renderBarX, state.renderBarY + 20 + (int)(20 * renderDropdownProgress) - 20, width, height,
                getXRayLabel(), b -> toggleXRay()));
        this.xraySettings = addButton(new PurpleButton(state.renderBarX + width + 5, state.renderBarY + 20 + (int)(20 * renderDropdownProgress) - 20, 20, height,
                new StringTextComponent("\u2699"), b -> openXRaySettings()));

        this.fullBrightButton = addButton(new PurpleButton(state.renderBarX, state.renderBarY + 40 + (int)(20 * renderDropdownProgress) - 20, width, height,
                getFullBrightLabel(), b -> toggleFullBright()));

        xrayButton.visible = fullBrightButton.visible = renderDropdownProgress > 0.05f;
        xraySettings.visible = renderDropdownProgress > 0.05f;

        // Utility bar
        this.chestButton = addButton(new PurpleButton(state.utilBarX, state.utilBarY + 20 + (int)(20 * utilDropdownProgress) - 20, width, height,
                getChestLabel(), b -> toggleChest()));
        this.usernameButton = addButton(new PurpleButton(state.utilBarX, state.utilBarY + 40 + (int)(20 * utilDropdownProgress) - 20, width, height,
                getUsernameLabel(), b -> toggleUsername()));
        this.usernameSettings = addButton(new PurpleButton(state.utilBarX + width + 5, state.utilBarY + 40 + (int)(20 * utilDropdownProgress) - 20, 20, height,
                new StringTextComponent("\u2699"), b -> openUsernameSettings()));

        chestButton.visible = usernameButton.visible = utilDropdownProgress > 0.05f;
        usernameSettings.visible = utilDropdownProgress > 0.05f;
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

    private void toggleBlink() {
        VisionClient.getBlinkHack().toggle();
        blinkButton.setMessage(getBlinkLabel());
        state.save();
    }

    private void toggleFullBright() {
        VisionClient.getFullBrightHack().toggle();
        fullBrightButton.setMessage(getFullBrightLabel());
        state.save();
    }

    private void toggleChest() {
        VisionClient.getChestHack().toggle();
        chestButton.setMessage(getChestLabel());
        state.save();
    }

    private void toggleUsername() {
        HackSettings cfg = VisionClient.getSettings();
        cfg.usernameOverrideEnabled = !cfg.usernameOverrideEnabled;
        usernameButton.setMessage(getUsernameLabel());
        VisionClient.saveSettings();
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

    private void openUsernameSettings() {
        this.minecraft.setScreen(new UsernameSettingsScreen(this));
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

    private StringTextComponent getBlinkLabel() {
        return new StringTextComponent((VisionClient.getBlinkHack().isEnabled() ? "Disable" : "Enable") + " Blink");
    }


    private StringTextComponent getFullBrightLabel() {
        return new StringTextComponent((VisionClient.getFullBrightHack().isEnabled() ? "Disable" : "Enable") + " FullBright");
    }

    private StringTextComponent getUsernameLabel() {
        boolean enabled = VisionClient.getSettings().usernameOverrideEnabled;
        return new StringTextComponent((enabled ? "Disable" : "Enable") + " CustomName");
    }

    private StringTextComponent getChestLabel() {
        return new StringTextComponent((VisionClient.getChestHack().isEnabled() ? "Disable" : "Enable") + " ChestLoot");
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
        if (button == 0 && mouseX >= state.renderBarX && mouseX <= state.renderBarX + BUTTON_WIDTH && mouseY >= state.renderBarY && mouseY <= state.renderBarY + 20) {
            draggingRender = true;
            renderDragOffsetX = (int)mouseX - state.renderBarX;
            renderDragOffsetY = (int)mouseY - state.renderBarY;
            renderDragStartX = (int)mouseX;
            renderDragStartY = (int)mouseY;
            return true;
        }
        if (button == 0 && mouseX >= state.utilBarX && mouseX <= state.utilBarX + BUTTON_WIDTH && mouseY >= state.utilBarY && mouseY <= state.utilBarY + 20) {
            draggingUtil = true;
            utilDragOffsetX = (int)mouseX - state.utilBarX;
            utilDragOffsetY = (int)mouseY - state.utilBarY;
            utilDragStartX = (int)mouseX;
            utilDragStartY = (int)mouseY;
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
            blinkButton.x = state.miscBarX;
            speedSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
            jumpSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
            flySettings.x = state.miscBarX + BUTTON_WIDTH + 5;
            jesusSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
            noFallSettings.x = state.miscBarX + BUTTON_WIDTH + 5;

            speedButton.y = state.miscBarY + 20 + (int)(20 * dropdownProgress) - 20;
            jumpButton.y = state.miscBarY + 40 + (int)(20 * dropdownProgress) - 20;
            flyButton.y = state.miscBarY + 60 + (int)(20 * dropdownProgress) - 20;
            jesusButton.y = state.miscBarY + 80 + (int)(20 * dropdownProgress) - 20;
            noFallButton.y = state.miscBarY + 100 + (int)(20 * dropdownProgress) - 20;
            blinkButton.y = state.miscBarY + 120 + (int)(20 * dropdownProgress) - 20;
            speedSettings.y = speedButton.y;
            jumpSettings.y = jumpButton.y;
            flySettings.y = flyButton.y;
            jesusSettings.y = jesusButton.y;
            noFallSettings.y = noFallButton.y;
            return true;
        }
        if (draggingRender) {
            state.renderBarX = (int)mouseX - renderDragOffsetX;
            state.renderBarY = (int)mouseY - renderDragOffsetY;
            xrayButton.x = state.renderBarX;
            fullBrightButton.x = state.renderBarX;
            xraySettings.x = state.renderBarX + BUTTON_WIDTH + 5;
            xrayButton.y = state.renderBarY + 20 + (int)(20 * renderDropdownProgress) - 20;
            fullBrightButton.y = state.renderBarY + 40 + (int)(20 * renderDropdownProgress) - 20;
            xraySettings.y = xrayButton.y;
            return true;
        }
        if (draggingUtil) {
            state.utilBarX = (int)mouseX - utilDragOffsetX;
            state.utilBarY = (int)mouseY - utilDragOffsetY;
            chestButton.x = state.utilBarX;
            chestButton.y = state.utilBarY + 20 + (int)(20 * utilDropdownProgress) - 20;
            usernameButton.x = state.utilBarX;
            usernameButton.y = state.utilBarY + 40 + (int)(20 * utilDropdownProgress) - 20;
            usernameSettings.x = state.utilBarX + BUTTON_WIDTH + 5;
            usernameSettings.y = usernameButton.y;
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
        if (draggingRender) {
            draggingRender = false;
            if (Math.abs(mouseX - renderDragStartX) < 5 && Math.abs(mouseY - renderDragStartY) < 5) {
                state.renderExpanded = !state.renderExpanded;
                renderDropdownTarget = state.renderExpanded;
            }
            state.save();
            return true;
        }
        if (draggingUtil) {
            draggingUtil = false;
            if (Math.abs(mouseX - utilDragStartX) < 5 && Math.abs(mouseY - utilDragStartY) < 5) {
                state.utilExpanded = !state.utilExpanded;
                utilDropdownTarget = state.utilExpanded;
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
        drawCenteredString(matrices, font, "Movement", state.miscBarX + 50, state.miscBarY + 6, 0xFFFFFF);
        dropdownProgress += ((dropdownTarget ? 1.0f : 0.0f) - dropdownProgress) * 0.2f;
        dropdownProgress = Math.max(0.0f, Math.min(1.0f, dropdownProgress));

        fill(matrices, state.renderBarX, state.renderBarY, state.renderBarX + BAR_WIDTH, state.renderBarY + 20, 0xAA5511AA);
        drawCenteredString(matrices, font, "Render", state.renderBarX + 50, state.renderBarY + 6, 0xFFFFFF);
        renderDropdownProgress += ((renderDropdownTarget ? 1.0f : 0.0f) - renderDropdownProgress) * 0.2f;
        renderDropdownProgress = Math.max(0.0f, Math.min(1.0f, renderDropdownProgress));

        fill(matrices, state.utilBarX, state.utilBarY, state.utilBarX + BAR_WIDTH, state.utilBarY + 20, 0xAA5511AA);
        drawCenteredString(matrices, font, "Utility", state.utilBarX + 50, state.utilBarY + 6, 0xFFFFFF);
        utilDropdownProgress += ((utilDropdownTarget ? 1.0f : 0.0f) - utilDropdownProgress) * 0.2f;
        utilDropdownProgress = Math.max(0.0f, Math.min(1.0f, utilDropdownProgress));

        int offsetY = (int)(20 * dropdownProgress);
        speedButton.x = state.miscBarX;
        jumpButton.x = state.miscBarX;
        flyButton.x = state.miscBarX;
        jesusButton.x = state.miscBarX;
        noFallButton.x = state.miscBarX;
        blinkButton.x = state.miscBarX;
        speedSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
        jumpSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
        flySettings.x = state.miscBarX + BUTTON_WIDTH + 5;
        jesusSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
        noFallSettings.x = state.miscBarX + BUTTON_WIDTH + 5;
        xrayButton.x = state.renderBarX;
        fullBrightButton.x = state.renderBarX;
        xraySettings.x = state.renderBarX + BUTTON_WIDTH + 5;

        speedButton.y = state.miscBarY + 20 + offsetY - 20;
        jumpButton.y = state.miscBarY + 40 + offsetY - 20;
        flyButton.y = state.miscBarY + 60 + offsetY - 20;
        jesusButton.y = state.miscBarY + 80 + offsetY - 20;
        noFallButton.y = state.miscBarY + 100 + offsetY - 20;
        blinkButton.y = state.miscBarY + 120 + offsetY - 20;
        speedSettings.y = speedButton.y;
        jumpSettings.y = jumpButton.y;
        flySettings.y = flyButton.y;
        jesusSettings.y = jesusButton.y;
        noFallSettings.y = noFallButton.y;
        xrayButton.y = state.renderBarY + 20 + (int)(20 * renderDropdownProgress) - 20;
        fullBrightButton.y = state.renderBarY + 40 + (int)(20 * renderDropdownProgress) - 20;
        xraySettings.y = xrayButton.y;
        chestButton.x = state.utilBarX;
        chestButton.y = state.utilBarY + 20 + (int)(20 * utilDropdownProgress) - 20;
        usernameButton.x = state.utilBarX;
        usernameButton.y = state.utilBarY + 40 + (int)(20 * utilDropdownProgress) - 20;
        usernameSettings.x = state.utilBarX + BUTTON_WIDTH + 5;
        usernameSettings.y = usernameButton.y;

        boolean vis = dropdownProgress > 0.05f;
        boolean visR = renderDropdownProgress > 0.05f;
        boolean visU = utilDropdownProgress > 0.05f;
        speedButton.visible = jumpButton.visible = flyButton.visible = jesusButton.visible = noFallButton.visible = blinkButton.visible = vis;
        speedSettings.visible = jumpSettings.visible = flySettings.visible = jesusSettings.visible = noFallSettings.visible = vis;
        xrayButton.visible = fullBrightButton.visible = visR;
        xraySettings.visible = visR;
        chestButton.visible = usernameButton.visible = visU;
        usernameSettings.visible = visU;
        speedButton.setAlpha(dropdownProgress);
        jumpButton.setAlpha(dropdownProgress);
        flyButton.setAlpha(dropdownProgress);
        jesusButton.setAlpha(dropdownProgress);
        noFallButton.setAlpha(dropdownProgress);
        blinkButton.setAlpha(dropdownProgress);
        speedSettings.setAlpha(dropdownProgress);
        jumpSettings.setAlpha(dropdownProgress);
        flySettings.setAlpha(dropdownProgress);
        jesusSettings.setAlpha(dropdownProgress);
        noFallSettings.setAlpha(dropdownProgress);
        xrayButton.setAlpha(renderDropdownProgress);
        fullBrightButton.setAlpha(renderDropdownProgress);
        xraySettings.setAlpha(renderDropdownProgress);
        chestButton.setAlpha(utilDropdownProgress);
        usernameButton.setAlpha(utilDropdownProgress);
        usernameSettings.setAlpha(utilDropdownProgress);

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        state.save();
        super.onClose();
    }
}
