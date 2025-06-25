package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


/**
 * Automatically adjusts aim for bow shots using simple prediction.
 * The improved version now predicts the target's future position based on
 * its current velocity and distance so that moving entities are hit more
 * consistently.
 */
public class BowAimbotHack extends ActionBase {
    public enum Mode { PLAYERS, MOBS, BOTH }
    private Mode mode = Mode.BOTH;

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
        if (!(stack.getItem() instanceof BowItem) || !player.isUsingItem()) return;

        LivingEntity target = findTarget(player);
        if (target == null) return;
        Vector3d pos = predictPosition(target, player);
        facePos(player, pos);
    }

    private LivingEntity findTarget(PlayerEntity player) {
        Vector3d eye = player.getEyePosition(1.0F);
        Vector3d look = player.getViewVector(1.0F);
        double range = 50.0D;
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
                if (dot > 0.99) {
                    bestDist = dist;
                    best = e;
                }
            }
        }
        return best;
    }

    /**
     * Estimate where the entity will be when an arrow reaches it.
     * This uses a simple linear prediction based on the entity's
     * current velocity and distance from the shooter.
     */
    private Vector3d predictPosition(LivingEntity target, PlayerEntity shooter) {
        Vector3d targetPos = target.getEyePosition(1.0F);
        Vector3d velocity = target.getDeltaMovement();
        double distance = targetPos.distanceTo(shooter.getEyePosition(1.0F));

        // Approximate arrow flight speed when fully charged.
        double arrowSpeed = 3.0D;
        double time = distance / arrowSpeed;

        return targetPos.add(velocity.scale(time));
    }

    private void facePos(ClientPlayerEntity player, Vector3d pos) {
        double dx = pos.x - player.getX();
        double dy = pos.y + 1.6 - player.getEyeY();
        double dz = pos.z - player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, dist)));
        player.yRot = yaw;
        player.xRot = pitch;
    }
}
