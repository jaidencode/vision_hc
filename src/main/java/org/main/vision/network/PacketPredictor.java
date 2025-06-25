package org.main.vision.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Tracks predicted player positions by observing outgoing and incoming packets.
 * Helps reconcile any desync between client and server state.
 */
public class PacketPredictor {
    private static final PacketPredictor INSTANCE = new PacketPredictor();

    private double predictedX;
    private double predictedY;
    private double predictedZ;

    public double getPredictedX() {
        return predictedX;
    }

    public double getPredictedY() {
        return predictedY;
    }

    public double getPredictedZ() {
        return predictedZ;
    }

    private PacketPredictor() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static PacketPredictor getInstance() {
        return INSTANCE;
    }

    public void onSend(IPacket<?> packet) {
        if (packet instanceof CPlayerPacket) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                CPlayerPacket p = (CPlayerPacket) packet;
                predictedX = p.getX(player.getX());
                predictedY = p.getY(player.getY());
                predictedZ = p.getZ(player.getZ());
            }
        }
    }

    public void onReceive(IPacket<?> packet) {
        if (packet instanceof SPlayerPositionLookPacket) {
            SPlayerPositionLookPacket p = (SPlayerPositionLookPacket) packet;
            predictedX = p.getX();
            predictedY = p.getY();
            predictedZ = p.getZ();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            double dx = player.getX() - predictedX;
            double dy = player.getY() - predictedY;
            double dz = player.getZ() - predictedZ;
            if (Math.abs(dx) > 0.5 || Math.abs(dy) > 0.5 || Math.abs(dz) > 0.5) {
                predictedX = player.getX();
                predictedY = player.getY();
                predictedZ = player.getZ();
            }
        }
    }
}
