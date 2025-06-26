package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
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
        double step = VisionClient.getSettings().dodgeStrength;
        Vector3d pos = player.position();

        for (AbstractArrowEntity arrow : world.getEntitiesOfClass(AbstractArrowEntity.class,
                player.getBoundingBox().inflate(range))) {
            Vector3d motion = arrow.getDeltaMovement();
            if (motion.lengthSqr() == 0) continue;

            Vector3d start = arrow.position();
            Vector3d end = start.add(motion.scale(20.0D));

            if (!willCollide(pos, start, end, 1.0D)) continue;

            Vector3d dodgePos = findBestDodge(world, pos, start, end, step);
            if (dodgePos != null) {
                Vector3d delta = dodgePos.subtract(pos);
                player.setDeltaMovement(player.getDeltaMovement().add(delta));
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

    /**
     * Determine the best nearby location to dodge towards. The method
     * samples several offsets around the player and selects the one that
     * is both valid to stand on and furthest from the incoming arrow path.
     */
    private Vector3d findBestDodge(ClientWorld world, Vector3d playerPos,
                                   Vector3d arrowStart, Vector3d arrowEnd,
                                   double step) {
        Vector3d[] dirs = new Vector3d[] {
                new Vector3d(1, 0, 0), new Vector3d(-1, 0, 0),
                new Vector3d(0, 0, 1), new Vector3d(0, 0, -1),
                new Vector3d(1, 0, 1).normalize(), new Vector3d(-1, 0, 1).normalize(),
                new Vector3d(1, 0, -1).normalize(), new Vector3d(-1, 0, -1).normalize()
        };

        Vector3d bestPos = null;
        double bestDist = 0.0D;
        for (Vector3d d : dirs) {
            Vector3d candidate = playerPos.add(d.scale(step));
            if (!isValidLocation(world, candidate)) continue;
            double dist = distanceFromSegment(candidate, arrowStart, arrowEnd);
            if (dist > bestDist) {
                bestDist = dist;
                bestPos = candidate;
            }
        }
        return bestPos;
    }

    private boolean isValidLocation(ClientWorld world, Vector3d pos) {
        BlockPos below = new BlockPos(pos.x, pos.y - 1, pos.z);
        BlockPos feet = new BlockPos(pos.x, pos.y, pos.z);
        BlockPos head = new BlockPos(pos.x, pos.y + 1, pos.z);
        if (world.getBlockState(below).isAir()) return false;
        return world.getBlockState(feet).isAir() && world.getBlockState(head).isAir();
    }

    private double distanceFromSegment(Vector3d point, Vector3d start, Vector3d end) {
        Vector3d lineDir = end.subtract(start);
        double length = lineDir.lengthSqr();
        if (length == 0.0D) return point.distanceTo(start);
        double t = point.subtract(start).dot(lineDir) / length;
        t = Math.max(0.0D, Math.min(1.0D, t));
        Vector3d proj = start.add(lineDir.scale(t));
        return point.distanceTo(proj);
    }

    private boolean willCollide(Vector3d playerPos, Vector3d start, Vector3d end, double radius) {
        return distanceFromSegment(playerPos, start, end) < radius;
    }
}
