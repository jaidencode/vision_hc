package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockRayTraceResult;

/** Simple teleport through the block the player is facing. */
public class WallWarp {
    /** Attempt to warp the player through the wall they are looking at. */
    public static void warp() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        ClientPlayerEntity player = mc.player;
        Vector3d eye = player.getEyePosition(1.0f);
        Vector3d look = player.getViewVector(1.0f);
        Vector3d reach = eye.add(look.scale(5.0d));

        RayTraceContext ctx = new RayTraceContext(eye, reach, RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE, player);
        RayTraceResult result = mc.level.clip(ctx);
        if (result.getType() != RayTraceResult.Type.BLOCK) {
            return; // nothing in front
        }

        BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
        Direction face = blockResult.getDirection();
        BlockPos pos = blockResult.getBlockPos().relative(face);
        // find first air spot up to 5 blocks ahead
        for (int i = 0; i < 5; i++) {
            BlockPos target = pos.relative(face, i);
            if (mc.level.getBlockState(target).isAir()) {
                double tx = target.getX() + 0.5;
                double ty = player.getY();
                double tz = target.getZ() + 0.5;
                player.setPos(tx, ty, tz);
                ClientPlayNetHandler conn = player.connection;
                if (conn != null) {
                    conn.send(new CPlayerPacket.PositionRotationPacket(tx, ty, tz,
                            player.yRot, player.xRot, player.isOnGround()));
                }
                break;
            }
        }
    }
}
