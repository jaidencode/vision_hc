package org.main.vision;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import java.util.Arrays;
import org.main.vision.config.HackSettings;

/** Screen to configure the list of blocks highlighted by XRay hack. */
public class XRaySettingsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget field;

    public XRaySettingsScreen(Screen parent) {
        super(new StringTextComponent("XRay Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HackSettings cfg = VisionClient.getSettings();
        field = new TextFieldWidget(this.font, this.width / 2 - 100, this.height / 2 - 10, 200, 20, new StringTextComponent("blocks"));
        field.setValue(String.join(",", cfg.xrayBlocks));
        this.addWidget(field);
        this.addButton(new PurpleButton(this.width / 2 - 50, this.height / 2 + 20, 100, 20, new StringTextComponent("Back"), b -> onClose()));
    }

    @Override
    public void onClose() {
        HackSettings cfg = VisionClient.getSettings();
        cfg.xrayBlocks = Arrays.asList(field.getValue().split("\\s*,\\s*"));
        VisionClient.saveSettings();
        this.minecraft.setScreen(parent);
    }
}
