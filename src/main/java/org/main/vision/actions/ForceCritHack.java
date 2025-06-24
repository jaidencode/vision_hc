package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Forces every attack to be a critical hit when enabled. */
public class ForceCritHack extends ActionBase {
    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if (!isEnabled()) return;
        if (!(event.getPlayer() instanceof ClientPlayerEntity)) return;
        ClientPlayerEntity player = (ClientPlayerEntity) event.getPlayer();
        player.fallDistance = 1.0F;
        player.setDeltaMovement(player.getDeltaMovement().x, -0.1D, player.getDeltaMovement().z);
        sendMovement(player);
    }

    @SubscribeEvent
    public void onCriticalHit(CriticalHitEvent event) {
        if (!isEnabled()) return;
        event.setDamageModifier(1.5F);
        event.setResult(Event.Result.ALLOW);
    }

    private void sendMovement(ClientPlayerEntity player) {
        ClientPlayNetHandler conn = player.connection;
        if (conn != null) {
            conn.send(new CPlayerPacket.PositionRotationPacket(
                    player.getX(), player.getY(), player.getZ(),
                    player.yRot, player.xRot, false));
        }
    }
}
