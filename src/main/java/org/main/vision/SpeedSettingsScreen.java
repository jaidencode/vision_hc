package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import org.main.vision.config.HackSettings;
import org.main.vision.VisionClient;
import org.main.vision.PurpleButton;

/** Settings screen for the Speed hack providing multiple tweakable options. */
public class SpeedSettingsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget multiplierField;
    private PurpleButton applyButton;
    private float originalMultiplier;
    private static final float DEFAULT_MULTIPLIER = 1.5f;

    public SpeedSettingsScreen(Screen parent) {
        super(new StringTextComponent("Speed Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HackSettings cfg = VisionClient.getSettings();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int fieldWidth = 120;
        multiplierField = new TextFieldWidget(this.font, centerX - fieldWidth / 2, centerY - 10, fieldWidth, 20, new StringTextComponent("Multiplier"));
        multiplierField.setMaxLength(32);
        originalMultiplier = cfg.speedMultiplier;
        multiplierField.setValue(Float.toString(originalMultiplier));
        addWidget(multiplierField);
        // Align button positions with the generic hack settings screens
        int y = centerY + 20;
        this.applyButton = addButton(new PurpleButton(centerX - 60, y, 120, 20, new StringTextComponent("Apply"), b -> apply()));
        y += 24;
        addButton(new PurpleButton(centerX - 60, y, 120, 20, new StringTextComponent("Reset"), b -> reset()));
        y += 24;
        addButton(new PurpleButton(centerX - 60, y, 120, 20, new StringTextComponent("Back"), b -> onClose()));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        drawCenteredString(ms, this.font, new StringTextComponent("Multiplier:"), this.width / 2, this.height / 2 - 25, 0xFFFFFF);
        multiplierField.render(ms, mouseX, mouseY, partialTicks);
        boolean changed = !multiplierField.getValue().equals(Float.toString(originalMultiplier));
        applyButton.active = changed;
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        // ignore changes unless applied
        this.minecraft.setScreen(parent);
    }

    private void apply() {
        HackSettings cfg = VisionClient.getSettings();
        try {
            cfg.speedMultiplier = Float.parseFloat(multiplierField.getValue());
            originalMultiplier = cfg.speedMultiplier;
        } catch (NumberFormatException ignored) {}
        VisionClient.saveSettings();
    }

    private void reset() {
        multiplierField.setValue(Float.toString(DEFAULT_MULTIPLIER));
    }
}
