package org.main.vision;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Simple grid based menu for toggling hacks.
 */
public class VisionMenuScreen extends Screen {
    private static final int TOGGLE_WIDTH = 80;
    private static final int SETTINGS_WIDTH = 18;
    private static final int BUTTON_HEIGHT = 16;
    private static final int PADDING = 4;

    private static class Entry {
        PurpleButton toggle;
        PurpleButton settings;
        Supplier<StringTextComponent> label;
    }

    private final List<Entry> entries = new ArrayList<>();
    private int scrollOffset;

    public VisionMenuScreen() {
        super(new StringTextComponent("Vision Menu"));
    }

    @Override
    protected void init() {
        entries.clear();
        scrollOffset = 0;
        addEntry(() -> getSpeedLabel(), this::toggleSpeed, this::openSpeedSettings);
        addEntry(() -> getJumpLabel(), this::toggleJump, this::openJumpSettings);
        addEntry(() -> getFlyLabel(), this::toggleFly, this::openFlySettings);
        addEntry(() -> getJesusLabel(), this::toggleJesus, this::openJesusSettings);
        addEntry(() -> getNoFallLabel(), this::toggleNoFall, this::openNoFallSettings);
        addEntry(() -> getBlinkLabel(), this::toggleBlink, null);
        addEntry(() -> getXRayLabel(), this::toggleXRay, this::openXRaySettings);
        addEntry(() -> getFullBrightLabel(), this::toggleFullBright, null);
        addEntry(() -> getForceCritLabel(), this::toggleForceCrit, null);
        addEntry(() -> getAntiKnockbackLabel(), this::toggleAntiKnockback, null);
        addEntry(() -> getAutoToolLabel(), this::toggleAutoTool, null);
        addEntry(() -> getSafeWalkLabel(), this::toggleSafeWalk, null);
        addEntry(() -> getAutoSprintLabel(), this::toggleAutoSprint, null);
        addEntry(() -> getAutoRespawnLabel(), this::toggleAutoRespawn, null);
        addEntry(() -> getBowAimbotLabel(), this::toggleBowAimbot, null);
        addEntry(() -> getRubberBanderLabel(), this::toggleRubberBander, null);
        addEntry(() -> getSeeBarrierLabel(), this::toggleSeeBarrier, null);
        addEntry(() -> getNameTagsLabel(), this::toggleNameTags, null);
        addEntry(() -> getScaffoldLabel(), this::toggleScaffold, null);
        addEntry(() -> getQuickChargeLabel(), this::toggleQuickCharge, null);
        addEntry(() -> getItemMagnetLabel(), this::toggleItemMagnet, null);
        layoutButtons();
    }

    private void addEntry(Supplier<StringTextComponent> label, Runnable toggle, Runnable settings) {
        PurpleButton toggleBtn = new PurpleButton(0, 0, TOGGLE_WIDTH, BUTTON_HEIGHT, label.get(), b -> {
            toggle.run();
            b.setMessage(label.get());
        });
        this.addButton(toggleBtn);
        PurpleButton settingsBtn = null;
        if (settings != null) {
            settingsBtn = new PurpleButton(0, 0, SETTINGS_WIDTH, BUTTON_HEIGHT, new StringTextComponent("\u2699"), b -> settings.run());
            this.addButton(settingsBtn);
        }
        Entry e = new Entry();
        e.toggle = toggleBtn;
        e.settings = settingsBtn;
        e.label = label;
        entries.add(e);
    }

    private void layoutButtons() {
        int slotWidth = TOGGLE_WIDTH + SETTINGS_WIDTH + PADDING;
        int cols = Math.max(1, (this.width - 20) / slotWidth);
        for (int i = 0; i < entries.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int x = 10 + col * slotWidth;
            int y = 20 + row * (BUTTON_HEIGHT + PADDING) - scrollOffset;
            Entry e = entries.get(i);
            e.toggle.x = x;
            e.toggle.y = y;
            if (e.settings != null) {
                e.settings.x = x + TOGGLE_WIDTH + 2;
                e.settings.y = y;
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int slotHeight = BUTTON_HEIGHT + PADDING;
        int slotWidth = TOGGLE_WIDTH + SETTINGS_WIDTH + PADDING;
        int cols = Math.max(1, (this.width - 20) / slotWidth);
        int totalRows = (entries.size() + cols - 1) / cols;
        int visibleRows = (this.height - 40) / slotHeight;
        int max = Math.max(0, totalRows - visibleRows) * slotHeight;
        scrollOffset -= delta * slotHeight;
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > max) scrollOffset = max;
        layoutButtons();
        return true;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        layoutButtons();
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    // Toggle methods
    private void toggleSpeed() { VisionClient.getSpeedHack().toggle(); }
    private void toggleJump() { VisionClient.getJumpHack().toggle(); }
    private void toggleFly() { VisionClient.getFlyHack().toggle(); }
    private void toggleJesus() { VisionClient.getJesusHack().toggle(); }
    private void toggleNoFall() { VisionClient.getNoFallHack().toggle(); }
    private void toggleXRay() { VisionClient.getXRayHack().toggle(); }
    private void toggleBlink() { VisionClient.getBlinkHack().toggle(); }
    private void toggleFullBright() { VisionClient.getFullBrightHack().toggle(); }
    private void toggleForceCrit() { VisionClient.getForceCritHack().toggle(); }
    private void toggleAntiKnockback() { VisionClient.getAntiKnockbackHack().toggle(); }
    private void toggleAutoTool() { VisionClient.getAutoToolHack().toggle(); }
    private void toggleSafeWalk() { VisionClient.getSafeWalkHack().toggle(); }
    private void toggleAutoSprint() { VisionClient.getAutoSprintHack().toggle(); }
    private void toggleAutoRespawn() { VisionClient.getAutoRespawnHack().toggle(); }
    private void toggleBowAimbot() { VisionClient.getBowAimbotHack().toggle(); }
    private void toggleRubberBander() { VisionClient.getRubberBanderHack().toggle(); }
    private void toggleSeeBarrier() { VisionClient.getSeeBarrierHack().toggle(); }
    private void toggleNameTags() { VisionClient.getNameTagsHack().toggle(); }
    private void toggleScaffold() { VisionClient.getScaffoldHack().toggle(); }
    private void toggleQuickCharge() { VisionClient.getQuickChargeHack().toggle(); }
    private void toggleItemMagnet() { VisionClient.getItemMagnetHack().toggle(); }

    // Settings open methods
    private void openSpeedSettings() { this.minecraft.setScreen(new SpeedSettingsScreen(this)); }
    private void openJumpSettings() { this.minecraft.setScreen(new HackSettingsScreen(this, "Jump", () -> VisionClient.getSettings().jumpVelocity,
            v -> { VisionClient.getSettings().jumpVelocity = v; }, VisionClient::saveSettings, 1.2D)); }
    private void openFlySettings() { this.minecraft.setScreen(new HackSettingsScreen(this, "FlySpeed", () -> VisionClient.getSettings().flyHorizontalSpeed,
            v -> { VisionClient.getSettings().flyHorizontalSpeed = v; }, VisionClient::saveSettings, 0.75D)); }
    private void openJesusSettings() { this.minecraft.setScreen(new HackSettingsScreen(this, "Buoyancy", () -> VisionClient.getSettings().jesusBuoyancy,
            v -> { VisionClient.getSettings().jesusBuoyancy = v; }, VisionClient::saveSettings, 0.0D)); }
    private void openNoFallSettings() { this.minecraft.setScreen(new HackSettingsScreen(this, "Threshold", () -> VisionClient.getSettings().noFallThreshold,
            v -> { VisionClient.getSettings().noFallThreshold = v; }, VisionClient::saveSettings, 2.0D)); }
    private void openXRaySettings() { this.minecraft.setScreen(new XRaySettingsScreen(this)); }
    

    // Label helpers
    private StringTextComponent getSpeedLabel() { return new StringTextComponent((VisionClient.getSpeedHack().isEnabled() ? "Disable" : "Enable") + " Speed"); }
    private StringTextComponent getJumpLabel() { return new StringTextComponent((VisionClient.getJumpHack().isEnabled() ? "Disable" : "Enable") + " Jump"); }
    private StringTextComponent getFlyLabel() { return new StringTextComponent((VisionClient.getFlyHack().isEnabled() ? "Disable" : "Enable") + " Fly"); }
    private StringTextComponent getJesusLabel() { return new StringTextComponent((VisionClient.getJesusHack().isEnabled() ? "Disable" : "Enable") + " Jesus"); }
    private StringTextComponent getNoFallLabel() { return new StringTextComponent((VisionClient.getNoFallHack().isEnabled() ? "Disable" : "Enable") + " NoFall"); }
    private StringTextComponent getXRayLabel() { return new StringTextComponent((VisionClient.getXRayHack().isEnabled() ? "Disable" : "Enable") + " XRay"); }
    private StringTextComponent getBlinkLabel() { return new StringTextComponent((VisionClient.getBlinkHack().isEnabled() ? "Disable" : "Enable") + " Blink"); }
    private StringTextComponent getFullBrightLabel() { return new StringTextComponent((VisionClient.getFullBrightHack().isEnabled() ? "Disable" : "Enable") + " FullBright"); }
    private StringTextComponent getForceCritLabel() { return new StringTextComponent((VisionClient.getForceCritHack().isEnabled() ? "Disable" : "Enable") + " ForceCrit"); }
    private StringTextComponent getAntiKnockbackLabel() { return new StringTextComponent((VisionClient.getAntiKnockbackHack().isEnabled() ? "Disable" : "Enable") + " AntiKnockback"); }
    private StringTextComponent getAutoToolLabel() { return new StringTextComponent((VisionClient.getAutoToolHack().isEnabled() ? "Disable" : "Enable") + " AutoTool"); }
    private StringTextComponent getSafeWalkLabel() { return new StringTextComponent((VisionClient.getSafeWalkHack().isEnabled() ? "Disable" : "Enable") + " SafeWalk"); }
    private StringTextComponent getAutoSprintLabel() { return new StringTextComponent((VisionClient.getAutoSprintHack().isEnabled() ? "Disable" : "Enable") + " AutoSprint"); }
    private StringTextComponent getAutoRespawnLabel() { return new StringTextComponent((VisionClient.getAutoRespawnHack().isEnabled() ? "Disable" : "Enable") + " AutoRespawn"); }
    private StringTextComponent getBowAimbotLabel() { return new StringTextComponent((VisionClient.getBowAimbotHack().isEnabled() ? "Disable" : "Enable") + " BowAimbot"); }
    private StringTextComponent getRubberBanderLabel() { return new StringTextComponent((VisionClient.getRubberBanderHack().isEnabled() ? "Disable" : "Enable") + " RubberBander"); }
    private StringTextComponent getSeeBarrierLabel() { return new StringTextComponent((VisionClient.getSeeBarrierHack().isEnabled() ? "Disable" : "Enable") + " SeeBarrier"); }
    private StringTextComponent getNameTagsLabel() { return new StringTextComponent((VisionClient.getNameTagsHack().isEnabled() ? "Disable" : "Enable") + " NameTags++"); }
    private StringTextComponent getScaffoldLabel() { return new StringTextComponent((VisionClient.getScaffoldHack().isEnabled() ? "Disable" : "Enable") + " Scaffold"); }
    private StringTextComponent getQuickChargeLabel() { return new StringTextComponent((VisionClient.getQuickChargeHack().isEnabled() ? "Disable" : "Enable") + " QuickCharge"); }
    private StringTextComponent getItemMagnetLabel() { return new StringTextComponent((VisionClient.getItemMagnetHack().isEnabled() ? "Disable" : "Enable") + " ItemMagnet"); }
}
