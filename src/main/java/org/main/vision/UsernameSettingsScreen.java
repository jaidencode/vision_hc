package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import org.main.vision.config.HackSettings;

/** Screen for configuring the custom username feature. */
public class UsernameSettingsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget field;
    private PurpleButton toggleButton;
    private PurpleButton applyButton;
    private boolean enabled;
    private boolean originalEnabled;
    private String originalName;

    public UsernameSettingsScreen(Screen parent) {
        super(new StringTextComponent("Username Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HackSettings cfg = VisionClient.getSettings();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        enabled = cfg.usernameOverrideEnabled;
        originalEnabled = enabled;
        originalName = cfg.customUsername;

        toggleButton = addButton(new PurpleButton(centerX - 60, centerY - 40, 120, 20,
                getToggleLabel(), b -> toggle()));

        field = new TextFieldWidget(this.font, centerX - 60, centerY - 10, 120, 20, new StringTextComponent("Name"));
        field.setMaxLength(32);
        field.setValue(originalName);
        addWidget(field);

        applyButton = addButton(new PurpleButton(centerX - 60, centerY + 20, 120, 20,
                new StringTextComponent("Apply"), b -> apply()));
        addButton(new PurpleButton(centerX - 60, centerY + 50, 120, 20,
                new StringTextComponent("Back"), b -> onClose()));
    }

    private StringTextComponent getToggleLabel() {
        return new StringTextComponent((enabled ? "Disable" : "Enable") + " Custom Name");
    }

    private void toggle() {
        enabled = !enabled;
        toggleButton.setMessage(getToggleLabel());
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        drawCenteredString(ms, this.font, new StringTextComponent("Custom Name:"), this.width / 2, this.height / 2 - 25, 0xFFFFFF);
        field.render(ms, mouseX, mouseY, partialTicks);
        boolean changed = !field.getValue().equals(originalName) || enabled != originalEnabled;
        applyButton.active = changed;
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    private void apply() {
        HackSettings cfg = VisionClient.getSettings();
        cfg.customUsername = field.getValue();
        cfg.usernameOverrideEnabled = enabled;
        originalName = cfg.customUsername;
        originalEnabled = enabled;
        VisionClient.saveSettings();
    }
}
