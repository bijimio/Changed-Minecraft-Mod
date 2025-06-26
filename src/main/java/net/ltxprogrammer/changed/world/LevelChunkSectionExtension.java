package net.ltxprogrammer.changed.world;

import net.minecraft.world.level.chunk.PalettedContainer;

public interface LevelChunkSectionExtension {
    LatexCoverState getLatexCoverState(int x, int y, int z);

    LatexCoverState setLatexCoverState(int x, int y, int z, LatexCoverState state);

    void acceptLatexStates(PalettedContainer<LatexCoverState> container);

    PalettedContainer<LatexCoverState> getLatexStates();
}
