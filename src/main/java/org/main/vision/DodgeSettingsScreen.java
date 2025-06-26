package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import org.main.vision.config.HackSettings;

/** Screen to edit AutoDodge settings. */
public class DodgeSettingsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget rangeField;
    private TextFieldWidget strengthField;
    private PurpleButton applyButton;
    private double originalRange;
    private double originalStrength;

    public DodgeSettingsScreen(Screen parent) {
        super(new StringTextComponent("AutoDodge Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HackSettings cfg = VisionClient.getSettings();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int fieldWidth = 80;
        rangeField = new TextFieldWidget(this.font, centerX - fieldWidth - 5, centerY - 10, fieldWidth, 20, new StringTextComponent("Range"));
        strengthField = new TextFieldWidget(this.font, centerX + 5, centerY - 10, fieldWidth, 20, new StringTextComponent("Strength"));
        rangeField.setMaxLength(32);
        strengthField.setMaxLength(32);
        originalRange = cfg.dodgeDetectionRange;
        originalStrength = cfg.dodgeStrength;
        rangeField.setValue(Double.toString(originalRange));
        strengthField.setValue(Double.toString(originalStrength));
        addWidget(rangeField);
        addWidget(strengthField);
        int y = centerY + 20;
        applyButton = addButton(new PurpleButton(centerX - 60, y, 120, 20, new StringTextComponent("Apply"), b -> apply()));
        y += 24;
        addButton(new PurpleButton(centerX - 60, y, 120, 20, new StringTextComponent("Back"), b -> onClose()));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        rangeField.render(ms, mouseX, mouseY, partialTicks);
        strengthField.render(ms, mouseX, mouseY, partialTicks);
        boolean changed = !rangeField.getValue().equals(Double.toString(originalRange)) ||
                          !strengthField.getValue().equals(Double.toString(originalStrength));
        applyButton.active = changed;
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    private void apply() {
        HackSettings cfg = VisionClient.getSettings();
        try {
            cfg.dodgeDetectionRange = Double.parseDouble(rangeField.getValue());
            cfg.dodgeStrength = Double.parseDouble(strengthField.getValue());
            originalRange = cfg.dodgeDetectionRange;
            originalStrength = cfg.dodgeStrength;
            VisionClient.saveSettings();
        } catch (NumberFormatException ignored) {}
    }
}
