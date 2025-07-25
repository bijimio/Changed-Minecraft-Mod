package net.ltxprogrammer.changed.mixin.block;

import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour implements ItemLike, net.minecraftforge.common.extensions.IForgeBlock {
    private BlockMixin(Properties p_60452_) {
        super(p_60452_);
    }

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    public void fallOn(Level level, BlockState state, BlockPos blockPos, Entity entity, float distance, CallbackInfo callbackInfo) {
        if (state.getFluidState().is(ChangedTags.Fluids.LATEX))
            callbackInfo.cancel();
    }
}
