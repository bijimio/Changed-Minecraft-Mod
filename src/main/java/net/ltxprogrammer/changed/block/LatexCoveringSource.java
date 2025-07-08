package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface LatexCoveringSource {
    LatexCoverState getLatexCoverState(BlockState blockState, BlockPos blockPos);
}
