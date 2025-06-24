package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import org.main.vision.config.HackSettings;

/** Simple screen to edit a single numeric value for a hack. */
public class HackSettingsScreen extends Screen {
    private final Screen parent;
    private final Runnable saveCallback;
    private TextFieldWidget field;
    private PurpleButton applyButton;
    private final String label;
    private final java.util.function.Supplier<Double> getter;
    private final java.util.function.Consumer<Double> setter;
    private final double defaultValue;
    private double originalValue;

    public HackSettingsScreen(Screen parent, String label, java.util.function.Supplier<Double> getter,
                              java.util.function.Consumer<Double> setter, Runnable saveCallback, double defaultValue) {
        super(new StringTextComponent(label + " Settings"));
        this.parent = parent;
        this.label = label;
        this.getter = getter;
        this.setter = setter;
        this.saveCallback = saveCallback;
        this.defaultValue = defaultValue;
    }

    @Override
    protected void init() {
        int fieldWidth = 120;
        field = new TextFieldWidget(this.font, this.width / 2 - fieldWidth / 2, this.height / 2 - 10, fieldWidth, 20, new StringTextComponent(label));
        field.setMaxLength(32);
        originalValue = getter.get();
        field.setValue(Double.toString(originalValue));
        this.addWidget(field);
        int centerX = this.width / 2;
        int y = this.height / 2 + 20;
        this.applyButton = this.addButton(new PurpleButton(centerX - 60, y, 120, 20,
                new StringTextComponent("Apply"), b -> apply()));
        y += 24;
        this.addButton(new PurpleButton(centerX - 60, y, 120, 20,
                new StringTextComponent("Reset"), b -> reset()));
        y += 24;
        this.addButton(new PurpleButton(centerX - 60, y, 120, 20,
                new StringTextComponent("Back"), b -> onClose()));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        drawCenteredString(ms, this.font, new StringTextComponent(label + ":"), this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        field.render(ms, mouseX, mouseY, partialTicks);
        applyButton.active = !field.getValue().equals(Double.toString(originalValue));
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        // Revert changes by simply ignoring field contents
        this.minecraft.setScreen(parent);
    }

    private void apply() {
        try {
            double v = Double.parseDouble(field.getValue());
            setter.accept(v);
            originalValue = v;
            saveCallback.run();
        } catch (NumberFormatException ignored) {}
    }

    private void reset() {
        field.setValue(Double.toString(defaultValue));
    }
}

