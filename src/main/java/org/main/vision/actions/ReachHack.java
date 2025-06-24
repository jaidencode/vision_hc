package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

/**
 * Extends the player's interaction reach distance.
 */
public class ReachHack extends ActionBase {
    private static final UUID MODIFIER_ID = UUID.fromString("2c7c77bb-7e7b-4de2-b742-89c810fb3cd3");

    private void apply(PlayerEntity player) {
        ModifiableAttributeInstance attr = player.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (attr != null && attr.getModifier(MODIFIER_ID) == null) {
            attr.addTransientModifier(new AttributeModifier(MODIFIER_ID, "ReachHack", 3.0D, AttributeModifier.Operation.ADDITION));
        }
    }

    private void remove(PlayerEntity player) {
        ModifiableAttributeInstance attr = player.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (attr != null && attr.getModifier(MODIFIER_ID) != null) {
            attr.removeModifier(MODIFIER_ID);
        }
    }

    @Override
    protected void onEnable() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) apply(player);
    }

    @Override
    protected void onDisable() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) remove(player);
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
}
