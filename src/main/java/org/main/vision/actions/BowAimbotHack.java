package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


/**
 * Automatically adjusts aim for bow shots.
 * <p>
 * This version performs a predictive ray trace to select the entity the
 * player is actually looking at and solves a simple intercept equation
 * using the target's velocity and distance to estimate where the arrow
 * should be aimed.  This results in far more consistent hits on moving
 * targets and keeps the crosshair centered on the entity.
 */
public class BowAimbotHack extends ActionBase {
    public enum Mode { PLAYERS, MOBS, BOTH }
    private Mode mode = Mode.BOTH;
    private LivingEntity lockedTarget = null;

    public void setMode(Mode m) { mode = m; }
    public Mode getMode() { return mode; }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        ItemStack stack = player.getUseItem();
        boolean usingBow = stack.getItem() instanceof BowItem && player.isUsingItem();
        if (!usingBow) {
            lockedTarget = null;
            return;
        }

        if (lockedTarget == null || !lockedTarget.isAlive()) {
            lockedTarget = findTarget(player);
        }
        LivingEntity target = lockedTarget;
        if (target == null) return;

        Vector3d pos = predictPosition(target, player);
        facePos(player, pos);
    }

    private LivingEntity findTarget(PlayerEntity player) {
        double range = 50.0D;

        // Try to ray trace the exact entity the player is looking at first
        RayTraceResult result = player.pick(range, 1.0F, false);
        if (result.getType() == RayTraceResult.Type.ENTITY) {
            LivingEntity entity = (LivingEntity)((EntityRayTraceResult) result).getEntity();
            if (entity != player && !entity.isInvisible() &&
                    (mode == Mode.BOTH || (mode == Mode.PLAYERS && entity instanceof PlayerEntity) ||
                            (mode == Mode.MOBS && !(entity instanceof PlayerEntity)))) {
                return entity;
            }
        }

        // Fallback to nearest entity in line of sight
        Vector3d eye = player.getEyePosition(1.0F);
        Vector3d look = player.getViewVector(1.0F);
        LivingEntity best = null;
        double bestDist = range;

        net.minecraft.client.world.ClientWorld world = (net.minecraft.client.world.ClientWorld) player.level;
        for (LivingEntity e : world.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0D),
                ent -> ent != player && !ent.isInvisible() &&
                        (mode == Mode.BOTH || (mode == Mode.PLAYERS && ent instanceof PlayerEntity) ||
                                (mode == Mode.MOBS && !(ent instanceof PlayerEntity))))) {
            Vector3d targetPos = e.getEyePosition(1.0F);
            Vector3d toTarget = targetPos.subtract(eye);
            double dist = toTarget.length();
            if (dist < bestDist) {
                double dot = toTarget.normalize().dot(look);
                if (dot > 0.98) {
                    bestDist = dist;
                    best = e;
                }
            }
        }
        return best;
    }

    /**
     * Predict where to aim by iteratively solving the projectile intercept with
     * target motion and gravity. This yields a stable solution even at long
     * distances where arrow drop becomes significant.
     */
    private Vector3d predictPosition(LivingEntity target, PlayerEntity shooter) {
        Vector3d shooterPos = shooter.getEyePosition(1.0F);
        Vector3d targetPos = target.getEyePosition(1.0F);
        Vector3d velocity = target.getDeltaMovement();

        double arrowSpeed = 3.0D;
        double arrowGravity = 0.05D;

        double time = shooterPos.distanceTo(targetPos) / arrowSpeed;
        for (int i = 0; i < 5; i++) {
            Vector3d predicted = targetPos.add(velocity.scale(time));
            double drop = 0.5D * arrowGravity * time * time;
            predicted = predicted.add(0, drop, 0);
            double newTime = shooterPos.distanceTo(predicted) / arrowSpeed;
            if (Math.abs(newTime - time) < 0.01D) {
                time = newTime;
                break;
            }
            time = newTime;
        }
        return targetPos.add(velocity.scale(time)).add(0, 0.5D * arrowGravity * time * time, 0);
    }

    private void facePos(ClientPlayerEntity player, Vector3d pos) {
        double dx = pos.x - player.getX();
        double dy = pos.y - player.getEyeY();
        double dz = pos.z - player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, dist)));
        player.yRot = yaw;
        player.xRot = pitch;
    }
}
