package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import org.main.vision.config.HackSettings;

/** Screen to edit QuantumTunnel target coordinates. */
public class QuantumTunnelSettingsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget xField;
    private TextFieldWidget yField;
    private TextFieldWidget zField;
    private PurpleButton applyButton;
    private double originalX;
    private double originalY;
    private double originalZ;

    public QuantumTunnelSettingsScreen(Screen parent) {
        super(new StringTextComponent("QuantumTunnel Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HackSettings cfg = VisionClient.getSettings();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int fieldWidth = 80;
        xField = new TextFieldWidget(this.font, centerX - fieldWidth - 5, centerY - 10, fieldWidth, 20, new StringTextComponent("X"));
        yField = new TextFieldWidget(this.font, centerX - fieldWidth / 2, centerY - 10, fieldWidth, 20, new StringTextComponent("Y"));
        zField = new TextFieldWidget(this.font, centerX + 5 + fieldWidth / 2, centerY - 10, fieldWidth, 20, new StringTextComponent("Z"));
        xField.setMaxLength(32);
        yField.setMaxLength(32);
        zField.setMaxLength(32);
        originalX = cfg.quantumX;
        originalY = cfg.quantumY;
        originalZ = cfg.quantumZ;
        xField.setValue(Double.toString(originalX));
        yField.setValue(Double.toString(originalY));
        zField.setValue(Double.toString(originalZ));
        addWidget(xField);
        addWidget(yField);
        addWidget(zField);
        int y = centerY + 20;
        applyButton = addButton(new PurpleButton(centerX - 60, y, 120, 20, new StringTextComponent("Apply"), b -> apply()));
        y += 24;
        addButton(new PurpleButton(centerX - 60, y, 120, 20, new StringTextComponent("Back"), b -> onClose()));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        xField.render(ms, mouseX, mouseY, partialTicks);
        yField.render(ms, mouseX, mouseY, partialTicks);
        zField.render(ms, mouseX, mouseY, partialTicks);
        boolean changed = !xField.getValue().equals(Double.toString(originalX)) ||
                !yField.getValue().equals(Double.toString(originalY)) ||
                !zField.getValue().equals(Double.toString(originalZ));
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
            cfg.quantumX = Double.parseDouble(xField.getValue());
            cfg.quantumY = Double.parseDouble(yField.getValue());
            cfg.quantumZ = Double.parseDouble(zField.getValue());
            originalX = cfg.quantumX;
            originalY = cfg.quantumY;
            originalZ = cfg.quantumZ;
            VisionClient.saveSettings();
            VisionClient.getQuantumTunnelHack().setTarget(cfg.quantumX, cfg.quantumY, cfg.quantumZ);
        } catch (NumberFormatException ignored) {}
    }
}
