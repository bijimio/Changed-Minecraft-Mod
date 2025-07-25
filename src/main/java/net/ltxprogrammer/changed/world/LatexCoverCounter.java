package net.ltxprogrammer.changed.world;

import net.minecraft.world.level.chunk.PalettedContainer;

public class LatexCoverCounter implements PalettedContainer.CountConsumer<LatexCoverState> {
    public int nonEmptyBlockCount;
    public int tickingLatexCoverCount;

    public void accept(LatexCoverState state, int count) {
        if (state.isPresent()) {
            this.nonEmptyBlockCount += count;
            if (state.isRandomlyTicking()) {
                this.tickingLatexCoverCount += count;
            }
        }
    }
}
