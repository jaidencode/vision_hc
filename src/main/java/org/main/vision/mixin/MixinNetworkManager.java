package org.main.vision.mixin;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.IPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void vision$send(IPacket<?> packet, CallbackInfo ci) {
        if (org.main.vision.actions.BlinkHack.handleSend(packet)) {
            ci.cancel();
            return;
        }
        org.main.vision.UsernameOverride.handleOutgoingPacket(packet);
    }

    @Inject(method = "channelRead0", at = @At("HEAD"))
    private void vision$channelRead0(io.netty.channel.ChannelHandlerContext ctx, IPacket<?> packet, CallbackInfo ci) {
        org.main.vision.UsernameOverride.handleIncomingPacket(packet);
    }
}
