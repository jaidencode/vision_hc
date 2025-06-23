package org.main.vision.actions;

import net.minecraftforge.common.MinecraftForge;

/**
 * Basic toggleable action helper.
 */
public abstract class ActionBase {
    private boolean enabled;

    /** Toggle the action on or off. */
    public void toggle() {
        setEnabled(!enabled);
    }

    /** Enable or disable the action. */
    public void setEnabled(boolean enable) {
        if (this.enabled == enable) {
            return;
        }
        this.enabled = enable;
        if (enable) {
            MinecraftForge.EVENT_BUS.register(this);
            onEnable();
        } else {
            MinecraftForge.EVENT_BUS.unregister(this);
            onDisable();
        }
    }

    /** @return true if action is enabled. */
    public boolean isEnabled() {
        return enabled;
    }

    /** Called when the action is enabled. */
    protected void onEnable() {}

    /** Called when the action is disabled. */
    protected void onDisable() {}
}
