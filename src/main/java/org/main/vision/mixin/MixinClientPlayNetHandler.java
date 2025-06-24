package org.main.vision.mixin;

import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels velocity updates when AntiKnockback is enabled.
 */
@Mixin(ClientPlayNetHandler.class)
public class MixinClientPlayNetHandler {
    @Inject(method = "handleSetEntityMotion", at = @At("HEAD"), cancellable = true)
    private void vision$onVelocity(SEntityVelocityPacket packet, CallbackInfo ci) {
        if (org.main.vision.VisionClient.getAntiKnockbackHack().isEnabled()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && packet.getId() == mc.player.getId()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "handleExplosion", at = @At("RETURN"))
    private void vision$onExplosion(SExplosionPacket packet, CallbackInfo ci) {
        if (org.main.vision.VisionClient.getAntiKnockbackHack().isEnabled()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.setDeltaMovement(0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Inject(method = "handlePlayerPosition", at = @At("HEAD"))
    private void vision$onPosition(SPlayerPositionLookPacket packet, CallbackInfo ci) {
        org.main.vision.net.PredictionManager.handleInbound(packet);
    }

    @Inject(method = "handleMovePlayer", at = @At("HEAD"))
    private void vision$onPositionAlt(SPlayerPositionLookPacket packet, CallbackInfo ci) {
        org.main.vision.net.PredictionManager.handleInbound(packet);
    }
}
