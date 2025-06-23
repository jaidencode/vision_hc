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
    private final String label;
    private final java.util.function.Supplier<Double> getter;
    private final java.util.function.Consumer<Double> setter;

    public HackSettingsScreen(Screen parent, String label, java.util.function.Supplier<Double> getter, java.util.function.Consumer<Double> setter, Runnable saveCallback) {
        super(new StringTextComponent(label + " Settings"));
        this.parent = parent;
        this.label = label;
        this.getter = getter;
        this.setter = setter;
        this.saveCallback = saveCallback;
    }

    @Override
    protected void init() {
        field = new TextFieldWidget(this.font, this.width / 2 - 40, this.height / 2 - 10, 80, 20, new StringTextComponent(label));
        field.setValue(Double.toString(getter.get()));
        this.addWidget(field);
        this.addButton(new PurpleButton(this.width / 2 - 50, this.height / 2 + 20, 100, 20, new StringTextComponent("Back"), b -> onClose()));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        drawCenteredString(ms, this.font, new StringTextComponent(label + ":"), this.width / 2, this.height / 2 - 25, 0xFFFFFF);
        field.render(ms, mouseX, mouseY, partialTicks);
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        try {
            double v = Double.parseDouble(field.getValue());
            setter.accept(v);
        } catch (NumberFormatException ignored) {}
        saveCallback.run();
        this.minecraft.setScreen(parent);
    }
}

