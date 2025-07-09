package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor, AutoCloseable, net.minecraftforge.common.extensions.IForgeLevel {
    @Inject(method = "markAndNotifyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;updateNeighbourShapes(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;II)V"))
    public void updateLatexCover(BlockPos blockPos, LevelChunk chunk, BlockState oldState, BlockState newState, int flags, int timeToLive, CallbackInfo ci) {
        LatexCoverState.executeInPlaceUpdate(this, oldState, newState, blockPos, flags, timeToLive);
    }
}
