package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Deflects incoming arrows by altering their motion when they get close to the player.
 * This uses direct motion changes and a subtle interaction packet to keep the server
 * in sync without noticeable rubberbanding.
 */
public class ArrowDeflectHack extends ActionBase {

    /** Radius around the player to check for incoming arrows. */
    private static final double DEFLECT_RADIUS = 3.0D;

    /** Track arrows we have already processed to avoid spamming packets. */
    private final Set<Integer> processedArrows = new HashSet<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.level == null) return;

        List<AbstractArrowEntity> arrows = mc.level.getEntitiesOfClass(AbstractArrowEntity.class,
                player.getBoundingBox().inflate(DEFLECT_RADIUS));

        for (AbstractArrowEntity arrow : arrows) {
            if (!arrow.isAlive()) continue;
            Vector3d motion = arrow.getDeltaMovement();
            Vector3d toPlayer = player.position().subtract(arrow.position());
            if (motion.dot(toPlayer) <= 0) continue; // Not heading toward the player

            // Rotate arrow motion around Y axis to deflect sideways
            Vector3d newMotion = new Vector3d(-motion.z, motion.y, motion.x);
            arrow.setDeltaMovement(newMotion);

            // Only notify the server once per arrow to prevent disconnects
            if (processedArrows.add(arrow.getId())) {
                sendInteractPacket(arrow);
            }
        }

        // Remove arrows that are no longer nearby
        Set<Integer> nearbyIds = arrows.stream().map(AbstractArrowEntity::getId).collect(Collectors.toSet());
        processedArrows.retainAll(nearbyIds);
    }

    /**
     * Send a minimal interaction packet so the server acknowledges the arrow update.
     */
    private void sendInteractPacket(AbstractArrowEntity arrow) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayNetHandler conn = mc.getConnection();
        if (conn != null && arrow.isAlive()) {
            conn.send(new CUseEntityPacket(arrow, false));
        }
    }
}
