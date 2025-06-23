package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.StringTextComponent;
import java.util.HashMap;
import java.util.Map;
import org.main.vision.config.HackSettings;
import org.main.vision.VisionClient;
import org.main.vision.PurpleButton;

/** Screen to configure the list of blocks highlighted by XRay hack. */
public class XRaySettingsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget field;
    private final Map<CheckboxButton, String> options = new HashMap<>();
    private static final String[] COMMON_ORES = new String[] {
            "minecraft:coal_ore", "minecraft:iron_ore", "minecraft:gold_ore",
            "minecraft:diamond_ore", "minecraft:emerald_ore", "minecraft:lapis_ore",
            "minecraft:redstone_ore", "minecraft:ancient_debris" };

    public XRaySettingsScreen(Screen parent) {
        super(new StringTextComponent("XRay Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HackSettings cfg = VisionClient.getSettings();
        int startY = this.height / 2 - 70;
        int x = this.width / 2 - 100;
        options.clear();
        for (int i = 0; i < COMMON_ORES.length; i++) {
            String id = COMMON_ORES[i];
            CheckboxButton cb = new CheckboxButton(x, startY + i * 20, 200, 20, new StringTextComponent(id), cfg.xrayBlocks.contains(id));
            options.put(cb, id);
            this.addButton(cb);
        }

        field = new TextFieldWidget(this.font, x, startY + COMMON_ORES.length * 20 + 5, 140, 20, new StringTextComponent("block id"));
        this.addWidget(field);
        this.addButton(new PurpleButton(x + 145, startY + COMMON_ORES.length * 20 + 5, 55, 20, new StringTextComponent("Add"), b -> addCustomBlock()));
        this.addButton(new PurpleButton(this.width / 2 - 50, this.height - 30, 100, 20, new StringTextComponent("Back"), b -> onClose()));
    }

    private void addCustomBlock() {
        String val = field.getValue().trim();
        if (!val.isEmpty()) {
            HackSettings cfg = VisionClient.getSettings();
            for (String part : val.split("\\s*,\\s*")) {
                if (!part.isEmpty() && !cfg.xrayBlocks.contains(part)) {
                    cfg.xrayBlocks.add(part);
                }
            }
            field.setValue("");
        }
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        drawCenteredString(ms, this.font, new StringTextComponent("XRay Blocks"), this.width / 2, 15, 0xFFFFFF);
        super.render(ms, mouseX, mouseY, partialTicks);
        field.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        HackSettings cfg = VisionClient.getSettings();
        // sync checkbox selections
        for (Map.Entry<CheckboxButton, String> e : options.entrySet()) {
            if (e.getKey().selected()) {
                if (!cfg.xrayBlocks.contains(e.getValue())) cfg.xrayBlocks.add(e.getValue());
            } else {
                cfg.xrayBlocks.remove(e.getValue());
            }
        }
        VisionClient.saveSettings();
        this.minecraft.setScreen(parent);
    }
}
