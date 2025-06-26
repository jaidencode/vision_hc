package org.main.vision.actions;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.main.vision.VisionClient;

/** Restores fast attack timing similar to Minecraft 1.8 and allows damage scaling. */
public class LegacyPvpHack extends ActionBase {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isEnabled()) return;
        if (!(event.player instanceof ClientPlayerEntity)) return;
        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        try {
            player.resetAttackStrengthTicker();
            java.lang.reflect.Field f = PlayerEntity.class.getDeclaredField("attackStrengthTicker");
            f.setAccessible(true);
            int full = (int) (player.getCurrentItemAttackStrengthDelay() + 1);
            f.setInt(player, full);
        } catch (Throwable ignored) {}
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!isEnabled()) return;
        if (event.getSource().getEntity() instanceof PlayerEntity) {
            double mult = VisionClient.getSettings().pvpDamageMultiplier;
            event.setAmount((float) (event.getAmount() * mult));
        }
    }
}
