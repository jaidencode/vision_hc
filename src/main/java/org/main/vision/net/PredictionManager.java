package org.main.vision.net;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Tracks predicted player position based on sent and received packets.
 */
@Mod.EventBusSubscriber(modid = "vision", value = Dist.CLIENT)
public class PredictionManager {
    private static double predictedX;
    private static double predictedY;
    private static double predictedZ;

    /** Handle an outgoing packet from the client. */
    public static void handleOutgoing(IPacket<?> packet) {
        if (packet instanceof CPlayerPacket) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                predictedX = player.getX();
                predictedY = player.getY();
                predictedZ = player.getZ();
            }
        }
    }

    /** Handle a position update packet from the server. */
    public static void handleInbound(SPlayerPositionLookPacket packet) {
        predictedX = packet.getX();
        predictedY = packet.getY();
        predictedZ = packet.getZ();
    }

    /** Periodically verify that the client prediction matches the real position. */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            double dx = player.getX() - predictedX;
            double dy = player.getY() - predictedY;
            double dz = player.getZ() - predictedZ;
            if (Math.abs(dx) > 1.0D || Math.abs(dy) > 1.0D || Math.abs(dz) > 1.0D) {
                predictedX = player.getX();
                predictedY = player.getY();
                predictedZ = player.getZ();
            }
        }
    }
}
