package org.main.vision.mixin;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.main.vision.VisionClient;

/** Adds a collision box to water when Jesus hack is enabled. */
@Mixin(FlowingFluidBlock.class)
public abstract class MixinFlowingFluidBlock {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void vision$collision(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (VisionClient.getJesusHack().isEnabled()) {
            cir.setReturnValue(VoxelShapes.block());
        }
    }
}
