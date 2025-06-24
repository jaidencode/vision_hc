package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Helps mimic vanilla packet behaviour by sending regular rotation
 * updates before interactions and during movement.
 */
public class AntiCheatHack extends ActionBase {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;
        sendRotation((ClientPlayerEntity) event.player);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if (!isEnabled()) return;
        if (event.getPlayer() instanceof ClientPlayerEntity) {
            sendRotation((ClientPlayerEntity) event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (!isEnabled()) return;
        if (event.getPlayer() instanceof ClientPlayerEntity) {
            sendRotation((ClientPlayerEntity) event.getPlayer());
        }
    }

    private void sendRotation(ClientPlayerEntity player) {
        ClientPlayNetHandler conn = player.connection;
        if (conn != null) {
            conn.send(new CPlayerPacket.PositionRotationPacket(
                    player.getX(), player.getY(), player.getZ(),
                    player.yRot, player.xRot, player.isOnGround()));
        }
    }
}
