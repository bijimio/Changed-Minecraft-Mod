package net.ltxprogrammer.changed.extension.sodium;

import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;

public interface WorldSliceExtension {
    default LatexCoverState getLatexCoverState(BlockPos pos) {
        return this.getLatexCoverState(pos.getX(), pos.getY(), pos.getZ());
    }

    LatexCoverState getLatexCoverState(int x, int y, int z);
}