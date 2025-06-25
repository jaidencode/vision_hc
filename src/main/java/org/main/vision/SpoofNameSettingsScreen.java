package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.StringTextComponent;
import org.main.vision.config.HackSettings;

/** Screen for configuring the SpoofName hack. */
public class SpoofNameSettingsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget aliasField;
    private CheckboxButton incomingBox;
    private CheckboxButton outgoingBox;
    private PurpleButton applyButton;
    private String originalAlias;
    private boolean originalIncoming;
    private boolean originalOutgoing;

    public SpoofNameSettingsScreen(Screen parent) {
        super(new StringTextComponent("SpoofName Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HackSettings cfg = VisionClient.getSettings();
        originalAlias = cfg.spoofName;
        originalIncoming = cfg.spoofIncoming;
        originalOutgoing = cfg.spoofOutgoing;

        int centerX = this.width / 2;
        int y = this.height / 2 - 30;
        aliasField = new TextFieldWidget(this.font, centerX - 80, y, 160, 20, new StringTextComponent("Alias"));
        aliasField.setMaxLength(32);
        aliasField.setValue(originalAlias);
        addWidget(aliasField);

        y += 25;
        incomingBox = new CheckboxButton(centerX - 80, y, 160, 20, new StringTextComponent("Spoof Incoming"), originalIncoming);
        addButton(incomingBox);
        y += 25;
        outgoingBox = new CheckboxButton(centerX - 80, y, 160, 20, new StringTextComponent("Spoof Outgoing"), originalOutgoing);
        addButton(outgoingBox);

        y += 30;
        applyButton = addButton(new PurpleButton(centerX - 60, y, 120, 20, new StringTextComponent("Apply"), b -> apply()));
        y += 24;
        addButton(new PurpleButton(centerX - 60, y, 120, 20, new StringTextComponent("Back"), b -> onClose()));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        drawCenteredString(ms, this.font, new StringTextComponent("Alias:"), this.width / 2, this.height / 2 - 45, 0xFFFFFF);
        super.render(ms, mouseX, mouseY, partialTicks);
        aliasField.render(ms, mouseX, mouseY, partialTicks);
        boolean changed = !aliasField.getValue().equals(originalAlias) || incomingBox.selected() != originalIncoming || outgoingBox.selected() != originalOutgoing;
        applyButton.active = changed;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    private void apply() {
        HackSettings cfg = VisionClient.getSettings();
        cfg.spoofName = aliasField.getValue().trim();
        cfg.spoofIncoming = incomingBox.selected();
        cfg.spoofOutgoing = outgoingBox.selected();
        VisionClient.saveSettings();
        originalAlias = cfg.spoofName;
        originalIncoming = cfg.spoofIncoming;
        originalOutgoing = cfg.spoofOutgoing;
        applyButton.active = false;
    }
}
