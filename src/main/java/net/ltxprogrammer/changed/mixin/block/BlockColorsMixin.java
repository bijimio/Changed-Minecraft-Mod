package net.ltxprogrammer.changed.mixin.block;

import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {
    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I", at = @At("HEAD"), cancellable = true)
    public void getColor(BlockState blockState, Level level, BlockPos blockPos, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        if (!AbstractLatexBlock.getSurfaceType(level, blockPos, Direction.DOWN).isAir()) {
            callbackInfoReturnable.setReturnValue(0xFFFFFFFF);
        }
    }

    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I", at = @At("HEAD"), cancellable = true)
    public void getColor(BlockState blockState, @Nullable BlockAndTintGetter level, @Nullable BlockPos blockPos, int p_92581_, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        if (blockPos != null && !AbstractLatexBlock.getSurfaceType(LatexCoverGetter.extendDefault(level), blockPos, Direction.DOWN).isAir()) {
            callbackInfoReturnable.setReturnValue(0xFFFFFFFF);
        }
    }
}
