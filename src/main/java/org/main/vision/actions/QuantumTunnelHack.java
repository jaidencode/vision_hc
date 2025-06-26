package org.main.vision.actions;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Experimental remote camera that lets the player view a distant location.
 * Interaction is not implemented but the camera can look around remotely.
 */
public class QuantumTunnelHack extends ActionBase {
    private RemoteClientPlayerEntity cameraEntity;
    private int cameraId;
    private double targetX;
    private double targetY;
    private double targetZ;

    /** Set the target coordinates for the remote view. */
    public void setTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    @Override
    protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        ClientPlayerEntity player = mc.player;
        if (world == null || player == null) return;
        cameraId = -4000 - world.random.nextInt(1000);
        cameraEntity = new RemoteClientPlayerEntity(world, new GameProfile(player.getGameProfile().getId(), "QuantumCam"));
        cameraEntity.setId(cameraId);
        cameraEntity.moveTo(targetX, targetY, targetZ, player.yRot, player.xRot);
        world.addPlayer(cameraId, cameraEntity);
        mc.setCameraEntity(cameraEntity);
    }

    @Override
    protected void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        ClientPlayerEntity player = mc.player;
        if (world != null && cameraEntity != null) {
            world.removeEntity(cameraId);
            cameraEntity = null;
        }
        if (player != null) {
            mc.setCameraEntity(player);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (cameraEntity == null) return;
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;
        // Keep camera rotation in sync with player's current view
        cameraEntity.yRot = player.yRot;
        cameraEntity.xRot = player.xRot;
        cameraEntity.yHeadRot = player.yRot;
    }
}
