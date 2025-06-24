package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.IPacket;
import java.util.LinkedList;
import java.util.Queue;

/** Buffers outgoing packets until disabled. */
public class BlinkHack extends ActionBase {
    private final Queue<IPacket<?>> queue = new LinkedList<>();
    private RemoteClientPlayerEntity clonePlayer;
    private int cloneId;

    public static boolean handleSend(IPacket<?> packet) {
        BlinkHack hack = org.main.vision.VisionClient.getBlinkHack();
        if (hack.isEnabled()) {
            hack.queue.add(packet);
            return true;
        }
        return false;
    }

    @Override
    protected void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        ClientPlayerEntity player = mc.player;
        if (world != null && player != null) {
            cloneId = -2000 - world.random.nextInt(1000);
            clonePlayer = new RemoteClientPlayerEntity(world, player.getGameProfile());
            clonePlayer.setId(cloneId);
            clonePlayer.moveTo(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot);
            world.addPlayer(cloneId, clonePlayer);
        }
    }

    @Override
    protected void onDisable() {
        NetworkManager nm = Minecraft.getInstance().getConnection().getConnection();
        while (!queue.isEmpty()) {
            nm.send(queue.poll());
        }
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        if (world != null && clonePlayer != null) {
            world.removeEntity(cloneId);
            clonePlayer = null;
        }
    }
}
