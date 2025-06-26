package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Provides a secondary controllable camera. Full multiplayer support
 * requires server side handling of the camera entity.
 */
public class DualCamHack extends ActionBase {
    private RemoteClientPlayerEntity camera;
    private int cameraId;
    private boolean controlling;

    private double startX, startY, startZ;
    private float bodyYaw, bodyPitch;
    private boolean hasStart;

    @Override
    protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        ClientPlayerEntity player = mc.player;
        if (world != null && player != null) {
            startX = player.getX();
            startY = player.getY();
            startZ = player.getZ();
            bodyYaw = player.yRot;
            bodyPitch = player.xRot;
            hasStart = true;

            cameraId = -3000 - world.random.nextInt(1000);
            camera = new RemoteClientPlayerEntity(world, player.getGameProfile());
            camera.setId(cameraId);
            camera.moveTo(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot);
            world.addPlayer(cameraId, camera);
            controlling = false;
        }
    }

    @Override
    protected void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        ClientPlayerEntity player = mc.player;
        if (world != null && camera != null) {
            world.removeEntity(cameraId);
        }
        if (mc.getCameraEntity() == camera && mc.player != null) {
            mc.setCameraEntity(mc.player);
        }
        if (player != null && hasStart) {
            float rotYaw = player.yRot;
            float rotPitch = player.xRot;
            if (mc.getCameraEntity() == camera && camera != null) {
                rotYaw = camera.yRot;
                rotPitch = camera.xRot;
            }
            player.moveTo(startX, startY, startZ, rotYaw, rotPitch);
            if (player.connection != null) {
                player.connection.send(new net.minecraft.network.play.client.CPlayerPacket.PositionRotationPacket(
                        startX, startY, startZ, rotYaw, rotPitch, player.isOnGround()));
            }
        }
        camera = null;
        controlling = false;
        hasStart = false;
    }

    /** Toggle between controlling the player and the camera. */
    public void toggleControl() {
        Minecraft mc = Minecraft.getInstance();
        if (camera == null) return;
        controlling = !controlling;
        if (controlling) {
            if (mc.player != null) {
                bodyYaw = mc.player.yRot;
                bodyPitch = mc.player.xRot;
            }
            mc.setCameraEntity(camera);
        } else if (mc.player != null) {
            mc.player.moveTo(mc.player.getX(), mc.player.getY(), mc.player.getZ(), camera.yRot, camera.xRot);
            mc.setCameraEntity(mc.player);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled() || !controlling || camera == null) return;
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;

        if (player != null) {
            player.setDeltaMovement(0, 0, 0);
            player.yRot = bodyYaw;
            player.xRot = bodyPitch;
            player.yHeadRot = bodyYaw;
            if (player.connection != null) {
                player.connection.send(new net.minecraft.network.play.client.CPlayerPacket.PositionRotationPacket(
                        player.getX(), player.getY(), player.getZ(), bodyYaw, bodyPitch, player.isOnGround()));
            }
        }

        double speed = 0.5D;
        double y = 0.0D;
        if (mc.options.keyJump.isDown()) y += speed;
        if (mc.options.keyShift.isDown()) y -= speed;

        double x = 0.0D;
        double z = 0.0D;
        float yawRad = (float) Math.toRadians(camera.yRot);
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);
        double strafeX = Math.cos(yawRad);
        double strafeZ = Math.sin(yawRad);

        if (mc.options.keyUp.isDown()) { x += forwardX; z += forwardZ; }
        if (mc.options.keyDown.isDown()) { x -= forwardX; z -= forwardZ; }
        if (mc.options.keyLeft.isDown()) { x += strafeX; z += strafeZ; }
        if (mc.options.keyRight.isDown()) { x -= strafeX; z -= strafeZ; }

        double mag = Math.sqrt(x * x + z * z);
        if (mag > 0.0D) {
            x = x / mag * speed;
            z = z / mag * speed;
        }

        camera.moveTo(camera.getX() + x, camera.getY() + y, camera.getZ() + z, camera.yRot, camera.xRot);
    }
}
