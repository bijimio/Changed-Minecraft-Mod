package net.ltxprogrammer.changed.extension.sodium;

import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.world.level.chunk.PalettedContainerRO;

public interface ClonedChunkSectionExtension {
    PalettedContainerRO<LatexCoverState> getLatexCoverData();
}
