package net.ltxprogrammer.changed.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public interface LatexCoverGetter {
    LatexCoverState getLatexCover(BlockPos blockPos);

    public static LatexCoverGetter wrap(Level level) {
        return blockPos -> LatexCoverState.getAt(level, blockPos);
    }

    public static LatexCoverGetter wrap(LevelChunk chunk) {
        return blockPos -> LatexCoverState.getAt(chunk, blockPos);
    }
}
