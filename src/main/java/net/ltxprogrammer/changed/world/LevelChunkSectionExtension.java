package net.ltxprogrammer.changed.world;

import net.minecraft.world.level.chunk.PalettedContainer;

public interface LevelChunkSectionExtension {
    LatexCoverState getLatexCoverState(int x, int y, int z);

    LatexCoverState setLatexCoverState(int x, int y, int z, LatexCoverState state, boolean unchecked);

    default LatexCoverState setLatexCoverState(int x, int y, int z, LatexCoverState state) {
        return this.setLatexCoverState(x, y, z, state, true);
    }

    void acceptLatexStates(PalettedContainer<LatexCoverState> container);

    PalettedContainer<LatexCoverState> getLatexStates();
}
