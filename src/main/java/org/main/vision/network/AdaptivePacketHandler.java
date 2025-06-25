package org.main.vision.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;

import org.main.vision.network.NetworkPerformanceMonitor;

/**
 * Centralised packet handler that uses a tiny neural network
 * to reject obviously invalid packets and adapt to server
 * corrections. This keeps spoofing logic consistent across
 * all features without heavy overhead.
 */
public class AdaptivePacketHandler {
    private static final AdaptivePacketHandler INSTANCE = new AdaptivePacketHandler();

    private final LightweightNN net = new LightweightNN();
    private final PacketPredictor predictor = PacketPredictor.getInstance();
    private final NetworkPerformanceMonitor monitor = NetworkPerformanceMonitor.getInstance();

    public static AdaptivePacketHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Called before sending a packet. Returns false when the
     * packet should be dropped.
     */
    public boolean handleOutgoing(IPacket<?> packet) {
        if (!org.main.vision.VisionClient.getActiveNetHack().isEnabled()) {
            return true;
        }
        if (packet instanceof CPlayerPacket) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                CPlayerPacket p = (CPlayerPacket) packet;
                double dx = p.getX(player.getX()) - predictor.getPredictedX();
                double dy = p.getY(player.getY()) - predictor.getPredictedY();
                double dz = p.getZ(player.getZ()) - predictor.getPredictedZ();
                double score = net.evaluate(dx, dy, dz);
                double threshold = 0.5 + (1.0 - monitor.getQuality()) * 0.25;
                if (score < threshold) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Invoked after a packet has been sent successfully.
     */
    public void afterSend(IPacket<?> packet) {
        if (!org.main.vision.VisionClient.getActiveNetHack().isEnabled()) return;
        predictor.onSend(packet);
    }

    /**
     * Process incoming packets from the server and train the
     * network using any position corrections.
     */
    public void handleIncoming(IPacket<?> packet) {
        if (!org.main.vision.VisionClient.getActiveNetHack().isEnabled()) return;
        predictor.onReceive(packet);
        if (packet instanceof SPlayerPositionLookPacket) {
            SPlayerPositionLookPacket p = (SPlayerPositionLookPacket) packet;
            double dx = p.getX() - predictor.getPredictedX();
            double dy = p.getY() - predictor.getPredictedY();
            double dz = p.getZ() - predictor.getPredictedZ();
            boolean valid = Math.abs(dx) < 5 && Math.abs(dy) < 5 && Math.abs(dz) < 5;
            net.train(dx, dy, dz, valid);
            monitor.recordCorrection(dx, dy, dz, valid);
        }
    }

    /**
     * Convenience wrapper for actually sending packets after
     * filtering when hacks call directly.
     */
    public void sendDirect(IPacket<?> packet) {
        if (!handleOutgoing(packet)) {
            return;
        }
        NetworkManager nm = Minecraft.getInstance().getConnection().getConnection();
        if (nm != null) {
            nm.send(packet);
            afterSend(packet);
        }
    }
}
