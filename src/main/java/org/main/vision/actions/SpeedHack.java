package org.main.vision.actions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

import java.util.UUID;

/**
 * Simple speed hack that increases player movement speed and FOV when enabled.
 */
public class SpeedHack extends ActionBase {

    private static final UUID MODIFIER_ID = UUID.fromString("96d899a2-1e4c-4e03-b1d0-596bcabc0123");
    private static final double SPEED_MULTIPLIER = 1.5D;
    private static final float FOV_MULTIPLIER = 1.2F;

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    private void apply(PlayerEntity player) {
        ModifiableAttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.getModifier(MODIFIER_ID) == null) {
            attr.addPermanentModifier(new AttributeModifier(MODIFIER_ID, "SpeedHack", SPEED_MULTIPLIER - 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    private void remove(PlayerEntity player) {
        ModifiableAttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.getModifier(MODIFIER_ID) != null) {
            attr.removeModifier(MODIFIER_ID);
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
