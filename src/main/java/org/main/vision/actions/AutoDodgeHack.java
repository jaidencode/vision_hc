package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.main.vision.VisionClient;

/**
 * Automatically dodges incoming projectiles by applying a small lateral
 * movement when a threat is detected nearby. This helps the player avoid
 * damage during PvP without fully automating combat.
 */
public class AutoDodgeHack extends ActionBase {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        ClientWorld world = mc.level;
        if (player == null || world == null) return;

        double range = VisionClient.getSettings().dodgeDetectionRange;
        double strength = VisionClient.getSettings().dodgeStrength;
        Vector3d pos = player.position();

        for (AbstractArrowEntity arrow : world.getEntitiesOfClass(AbstractArrowEntity.class,
                player.getBoundingBox().inflate(range))) {
            Vector3d motion = arrow.getDeltaMovement();
            if (motion.lengthSqr() == 0) continue;
            Vector3d toPlayer = pos.subtract(arrow.position());
            if (toPlayer.length() > range) continue;
            double dot = motion.normalize().dot(toPlayer.normalize());
            if (dot > 0.8) {
                Vector3d dodge = motion.cross(new Vector3d(0, 1, 0)).normalize().scale(strength);
                player.setDeltaMovement(player.getDeltaMovement().add(dodge));
                sendMovement(player);
                break;
            }
        }
    }

    private void sendMovement(ClientPlayerEntity player) {
        if (player.connection != null) {
            player.connection.send(new CPlayerPacket.PositionRotationPacket(
                    player.getX(), player.getY(), player.getZ(),
                    player.yRot, player.xRot, player.isOnGround()));
        }
    }
}
