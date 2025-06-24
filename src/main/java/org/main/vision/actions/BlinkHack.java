package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.IPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import java.util.LinkedList;
import java.util.Queue;

/** Buffers outgoing packets until disabled. */
public class BlinkHack extends ActionBase {
    private final Queue<IPacket<?>> queue = new LinkedList<>();
    private RemoteClientPlayerEntity clonePlayer;
    private int cloneId;
    private float cloneYaw;
    private float clonePitch;

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
            cloneYaw = player.yRot;
            clonePitch = player.xRot;
            clonePlayer.moveTo(player.getX(), player.getY(), player.getZ(), cloneYaw, clonePitch);
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

    /**
     * Keep the client-side clone oriented correctly while blink is active.
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled()) return;
        if (event.phase != TickEvent.Phase.END) return;
        if (clonePlayer != null) {
            clonePlayer.yRot = cloneYaw;
            clonePlayer.xRot = clonePitch;
            clonePlayer.yHeadRot = cloneYaw;
        }
    }
}
