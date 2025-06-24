package org.main.vision.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.common.ForgeMod;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.main.vision.VisionClient;

import java.util.UUID;

/**
 * Extends player reach distance when enabled.
 */
public class ReachHack extends ActionBase {
    private static final UUID MODIFIER_ID = UUID.fromString("8a2b3bec-bd8f-4c46-a829-222b5c9db630");

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;
        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        if (isEnabled()) {
            apply(player);
        } else {
            remove(player);
        }
    }

    @Override
    protected void onEnable() {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) apply(player);
    }

    @Override
    protected void onDisable() {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) remove(player);
    }

    private void apply(ClientPlayerEntity player) {
        ModifiableAttributeInstance attr = player.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (attr != null) {
            double target = VisionClient.getSettings().reachDistance;
            AttributeModifier mod = attr.getModifier(MODIFIER_ID);
            double diff = target - attr.getBaseValue();
            if (mod == null || mod.getAmount() != diff) {
                if (mod != null) attr.removeModifier(mod);
                attr.addTransientModifier(new AttributeModifier(MODIFIER_ID, "ReachHack", diff, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    private void remove(ClientPlayerEntity player) {
        ModifiableAttributeInstance attr = player.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (attr != null && attr.getModifier(MODIFIER_ID) != null) {
            attr.removeModifier(MODIFIER_ID);
        }
    }
}
