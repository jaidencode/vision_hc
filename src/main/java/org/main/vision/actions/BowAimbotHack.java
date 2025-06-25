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

import java.util.Comparator;

/**
 * Automatically adjusts aim for bow shots using simple prediction.
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
        Vector3d pos = predictPosition(player, target);
        facePos(player, pos);
    }

    private LivingEntity findTarget(PlayerEntity player) {
        net.minecraft.client.world.ClientWorld world = (net.minecraft.client.world.ClientWorld) player.level;
        return java.util.stream.StreamSupport.stream(world.entitiesForRendering().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity)e)
                .filter(e -> !e.isInvisible())
                .filter(e -> e != player)
                .filter(e -> mode == Mode.BOTH ||
                        (mode == Mode.PLAYERS && e instanceof PlayerEntity) ||
                        (mode == Mode.MOBS && !(e instanceof PlayerEntity)))
                .min(Comparator.comparingDouble(e -> player.distanceToSqr(e)))
                .orElse(null);
    }

    private Vector3d predictPosition(PlayerEntity shooter, LivingEntity target) {
        Vector3d tv = target.getDeltaMovement();
        double dist = shooter.distanceTo(target);
        double t = dist / 1.5D;
        return target.position().add(tv.scale(t));
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
