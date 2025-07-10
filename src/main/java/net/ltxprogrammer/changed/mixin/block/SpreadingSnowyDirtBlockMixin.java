package net.ltxprogrammer.changed.mixin.block;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpreadingSnowyDirtBlock.class)
public abstract class SpreadingSnowyDirtBlockMixin extends SnowyDirtBlock {
    public SpreadingSnowyDirtBlockMixin(Properties p_56640_) {
        super(p_56640_);
    }

    @WrapMethod(method = "canBeGrass")
    private static boolean canBeGrass(BlockState blockState, LevelReader level, BlockPos blockPos, Operation<Boolean> original) {
        return AbstractLatexBlock.getSurfaceType(level, blockPos.above(), Direction.DOWN).isAir() &&
                original.call(blockState, level, blockPos);
    }

    @WrapMethod(method = "canPropagate")
    private static boolean canPropagate(BlockState blockState, LevelReader level, BlockPos blockPos, Operation<Boolean> original) {
        return AbstractLatexBlock.getSurfaceType(level, blockPos.above(), Direction.DOWN).isAir() &&
                original.call(blockState, level, blockPos);
    }
}
