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
    private TextFieldWidget burstField;

    public SpeedSettingsScreen(Screen parent) {
        super(new StringTextComponent("Speed Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HackSettings cfg = VisionClient.getSettings();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        multiplierField = new TextFieldWidget(this.font, centerX - 40, centerY - 20, 80, 20, new StringTextComponent("Multiplier"));
        multiplierField.setValue(Float.toString(cfg.speedMultiplier));
        burstField = new TextFieldWidget(this.font, centerX - 40, centerY + 5, 80, 20, new StringTextComponent("Packets"));
        burstField.setValue(Integer.toString(VisionClient.getSpeedHack().getPacketBurst()));
        addWidget(multiplierField);
        addWidget(burstField);
        addButton(new PurpleButton(centerX - 50, centerY + 35, 100, 20, new StringTextComponent("Back"), b -> onClose()));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        drawCenteredString(ms, this.font, new StringTextComponent("Multiplier:"), this.width / 2, this.height / 2 - 32, 0xFFFFFF);
        multiplierField.render(ms, mouseX, mouseY, partialTicks);
        drawCenteredString(ms, this.font, new StringTextComponent("Extra Packets:"), this.width / 2, this.height / 2 - 7, 0xFFFFFF);
        burstField.render(ms, mouseX, mouseY, partialTicks);
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        HackSettings cfg = VisionClient.getSettings();
        try {
            cfg.speedMultiplier = Float.parseFloat(multiplierField.getValue());
        } catch (NumberFormatException ignored) {}
        try {
            VisionClient.getSpeedHack().setPacketBurst(Integer.parseInt(burstField.getValue()));
        } catch (NumberFormatException ignored) {}
        VisionClient.saveSettings();
        this.minecraft.setScreen(parent);
    }
}
