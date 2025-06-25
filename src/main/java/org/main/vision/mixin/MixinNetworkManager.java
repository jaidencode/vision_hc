package org.main.vision.mixin;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.IPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts outgoing packets for Blink and RubberBander hacks.
 */
@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void vision$onSend(IPacket<?> packet, CallbackInfo ci) {
        if (org.main.vision.actions.BlinkHack.handleSend(packet)) {
            ci.cancel();
            return;
        }
        if (org.main.vision.actions.RubberBanderHack.handleSend(packet)) {
            ci.cancel();
        }
    }
}
