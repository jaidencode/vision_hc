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
        performPacketCrit(player);
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

    private void performPacketCrit(ClientPlayerEntity player) {
        sendOffset(player, 0.0625D);
        sendOffset(player, 0.0D);
        sendOffset(player, 0.0125D);
        sendOffset(player, 0.0D);
    }

    private void sendOffset(ClientPlayerEntity player, double y) {
        ClientPlayNetHandler conn = player.connection;
        if (conn != null) {
            conn.send(new CPlayerPacket.PositionRotationPacket(
                    player.getX(), player.getY() + y, player.getZ(),
                    player.yRot, player.xRot, false));
        }
    }
}
