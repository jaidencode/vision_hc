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
    private java.util.List<String> workingBlocks = new java.util.ArrayList<>();
    private java.util.List<String> originalBlocks = new java.util.ArrayList<>();
    private PurpleButton applyButton;
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
        workingBlocks = new java.util.ArrayList<>(cfg.xrayBlocks);
        originalBlocks = new java.util.ArrayList<>(cfg.xrayBlocks);
        // Start higher on the screen so the buttons at the bottom do not overlap
        // with the ore list or text field
        int startY = 30;
        int x = this.width / 2 - 100;
        options.clear();
        for (int i = 0; i < COMMON_ORES.length; i++) {
            String id = COMMON_ORES[i];
            CheckboxButton cb = new CheckboxButton(x, startY + i * 20, 200, 20, new StringTextComponent(id), workingBlocks.contains(id));
            options.put(cb, id);
            this.addButton(cb);
        }

        int fieldWidth = 180;
        field = new TextFieldWidget(this.font, x, startY + COMMON_ORES.length * 20 + 5, fieldWidth, 20, new StringTextComponent("block id"));
        field.setMaxLength(64);
        this.addWidget(field);
        this.addButton(new PurpleButton(x + fieldWidth + 5, startY + COMMON_ORES.length * 20 + 5, 55, 20, new StringTextComponent("Add"), b -> addCustomBlock()));
        int bottomY = this.height - 30;
        int centerX = this.width / 2;
        this.applyButton = this.addButton(new PurpleButton(centerX - 95, bottomY, 60, 20, new StringTextComponent("Apply"), b -> apply()));
        this.addButton(new PurpleButton(centerX - 30, bottomY, 60, 20, new StringTextComponent("Reset"), b -> reset()));
        this.addButton(new PurpleButton(centerX + 35, bottomY, 60, 20, new StringTextComponent("Back"), b -> onClose()));
    }

    private void addCustomBlock() {
        String val = field.getValue().trim();
        if (!val.isEmpty()) {
            for (String part : val.split("\\s*,\\s*")) {
                if (!part.isEmpty() && !workingBlocks.contains(part)) {
                    workingBlocks.add(part);
                }
            }
            field.setValue("");
            applyButton.active = true;
        }
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        drawCenteredString(ms, this.font, new StringTextComponent("XRay Blocks"), this.width / 2, 15, 0xFFFFFF);
        super.render(ms, mouseX, mouseY, partialTicks);
        field.render(ms, mouseX, mouseY, partialTicks);
        boolean changed = !workingBlocks.equals(originalBlocks);
        if (!changed) {
            // check checkboxes
            for (Map.Entry<CheckboxButton, String> e : options.entrySet()) {
                boolean selected = e.getKey().selected();
                boolean origSel = originalBlocks.contains(e.getValue());
                if (selected != origSel) { changed = true; break; }
            }
        }
        applyButton.active = changed;
    }

    @Override
    public void onClose() {
        // ignore changes unless applied
        this.minecraft.setScreen(parent);
    }

    private void apply() {
        HackSettings cfg = VisionClient.getSettings();
        java.util.List<String> newList = new java.util.ArrayList<>();
        for (Map.Entry<CheckboxButton, String> e : options.entrySet()) {
            if (e.getKey().selected()) {
                newList.add(e.getValue());
            }
        }
        for (String custom : workingBlocks) {
            if (!newList.contains(custom)) newList.add(custom);
        }
        workingBlocks = new java.util.ArrayList<>(newList);
        cfg.xrayBlocks.clear();
        cfg.xrayBlocks.addAll(newList);
        originalBlocks = new java.util.ArrayList<>(newList);
        VisionClient.saveSettings();
        applyButton.active = false;
    }

    private void reset() {
        workingBlocks.clear();
        originalBlocks.clear();
        for (CheckboxButton cb : options.keySet()) {
            if (cb.selected()) cb.onPress();
        }
        field.setValue("");
        applyButton.active = true;
    }
}
