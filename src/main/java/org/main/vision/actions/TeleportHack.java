package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;

/**
 * Simple hack that teleports the player to preset coordinates
 * when toggled. Automatically disables after the teleport
 * completes.
 */
public class TeleportHack extends ActionBase {
    private double targetX;
    private double targetY;
    private double targetZ;

    /** Set the target coordinates for teleporting. */
    public void setTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    @Override
    protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player != null) {
            player.setPos(targetX, targetY, targetZ);
        }
        // immediately disable after teleporting
        setEnabled(false);
    }
}
