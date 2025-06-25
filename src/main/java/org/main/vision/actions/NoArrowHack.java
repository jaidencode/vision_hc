package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

/**
 * Temporarily dodges incoming arrows by teleporting to a nearby safe spot
 * and returning once the projectile passes.
 */
public class NoArrowHack extends ActionBase {
    private Vector3d originalPos;
    private int returnTicks;
    private boolean avoiding;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        ClientPlayerEntity player = mc.player;
        if (world == null || player == null) return;

        if (avoiding) {
            handleReturn(player);
            return;
        }

        for (AbstractArrowEntity arrow : world.getEntitiesOfClass(AbstractArrowEntity.class,
                player.getBoundingBox().inflate(15))) {
            Vector3d arrowPos = arrow.position();
            Vector3d arrowVel = arrow.getDeltaMovement();
            Vector3d toPlayer = player.position().subtract(arrowPos);
            if (arrowVel.dot(toPlayer) <= 0) continue; // moving away
            if (lineDistance(arrowPos, arrowVel, player.position()) > 1.5) continue;
            BlockPos safe = findSafeSpot(world, player.blockPosition(), arrowPos, arrowVel);
            if (safe != null) {
                originalPos = player.position();
                player.setDeltaMovement(Vector3d.ZERO);
                player.moveTo(safe.getX() + 0.5, safe.getY(), safe.getZ() + 0.5, player.yRot, player.xRot);
                avoiding = true;
                returnTicks = 5;
            }
            break;
        }
    }

    private void handleReturn(ClientPlayerEntity player) {
        if (returnTicks > 0) {
            returnTicks--;
            Vector3d cur = player.position();
            double t = 1.0 / (returnTicks + 1);
            Vector3d target = originalPos;
            double nx = cur.x + (target.x - cur.x) * t;
            double ny = cur.y + (target.y - cur.y) * t;
            double nz = cur.z + (target.z - cur.z) * t;
            player.moveTo(nx, ny, nz, player.yRot, player.xRot);
        } else {
            player.moveTo(originalPos.x, originalPos.y, originalPos.z, player.yRot, player.xRot);
            avoiding = false;
        }
    }

    private BlockPos findSafeSpot(ClientWorld world, BlockPos start, Vector3d arrowPos, Vector3d arrowVel) {
        Queue<BlockPos> q = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        q.add(start);
        visited.add(start);
        int maxDist = 5;
        while (!q.isEmpty()) {
            BlockPos pos = q.poll();
            if (isSafe(world, pos, arrowPos, arrowVel)) return pos;
            if (pos.distManhattan(start) >= maxDist) continue;
            for (Direction d : Direction.Plane.HORIZONTAL) {
                BlockPos next = pos.relative(d);
                if (!visited.contains(next) && isWalkable(world, next)) {
                    visited.add(next);
                    q.add(next);
                }
            }
        }
        return null;
    }

    private boolean isWalkable(ClientWorld world, BlockPos pos) {
        return world.isEmptyBlock(pos) && world.isEmptyBlock(pos.above());
    }

    private boolean isSafe(ClientWorld world, BlockPos pos, Vector3d arrowPos, Vector3d arrowVel) {
        if (!isWalkable(world, pos)) return false;
        Vector3d p = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        return lineDistance(arrowPos, arrowVel, p) > 1.0;
    }

    private double lineDistance(Vector3d origin, Vector3d dir, Vector3d point) {
        Vector3d diff = point.subtract(origin);
        Vector3d cross = diff.cross(dir);
        return cross.length() / dir.length();
    }
}
