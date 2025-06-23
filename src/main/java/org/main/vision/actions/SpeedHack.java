package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.main.vision.VisionClient;

import java.util.UUID;

/**
 * Simple speed hack that increases player movement speed and FOV when enabled.
 */
public class SpeedHack extends ActionBase {

    private static final UUID MODIFIER_ID = UUID.fromString("96d899a2-1e4c-4e03-b1d0-596bcabc0123");
    private static final float FOV_MULTIPLIER = 1.2F;

    private int packetBurst = 2; // number of additional movement packets per tick

    /** @return number of extra movement packets sent each tick. */
    public int getPacketBurst() {
        return packetBurst;
    }

    /** Set the number of extra movement packets sent each tick. */
    public void setPacketBurst(int burst) {
        packetBurst = Math.max(0, burst);
    }

    @Override
    protected void onEnable() {
        PlayerEntity player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null) {
            apply(player);
        }
    }

    @Override
    protected void onDisable() {
        PlayerEntity player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null) {
            remove(player);
        }
    }

    private void apply(PlayerEntity player) {
        ModifiableAttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        double multiplier = VisionClient.getSettings().speedMultiplier;
        if (attr != null && attr.getModifier(MODIFIER_ID) == null) {
            attr.addTransientModifier(new AttributeModifier(MODIFIER_ID, "SpeedHack", multiplier - 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        if (player instanceof ClientPlayerEntity) {
            sendBurstPackets((ClientPlayerEntity) player);
        }
    }

    private void remove(PlayerEntity player) {
        ModifiableAttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.getModifier(MODIFIER_ID) != null) {
            attr.removeModifier(MODIFIER_ID);
        }
    }

    private void sendBurstPackets(ClientPlayerEntity player) {
        ClientPlayNetHandler conn = player.connection;
        if (conn != null) {
            for (int i = 0; i < packetBurst; i++) {
                conn.send(new CPlayerPacket.PositionRotationPacket(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot, player.isOnGround()));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        PlayerEntity player = event.player;
        if (isEnabled()) {
            apply(player);
        } else {
            remove(player);
        }
    }

    @SubscribeEvent
    public void onFov(FOVUpdateEvent event) {
        if (isEnabled()) {
            event.setNewfov(event.getFov() * FOV_MULTIPLIER);
        }
    }
}
