package net.ltxprogrammer.changed.mixin.server;

import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.LevelChunkSectionExtension;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements LevelChunkSectionExtension {
    @Unique private PalettedContainer<LatexCoverState> coverStates
            = new PalettedContainer<>(LatexCoverState.PERMUTATIONS, LatexCoverState.defaultState(), PalettedContainer.Strategy.SECTION_STATES);

    @Override
    public LatexCoverState getLatexCoverState(int x, int y, int z) {
        return coverStates.get(x, y, z);
    }

    @Override
    public LatexCoverState setLatexCoverState(int x, int y, int z, LatexCoverState state) {
        return coverStates.getAndSet(x, y, z, state);
    }

    @Override
    public void acceptLatexStates(PalettedContainer<LatexCoverState> container) {
        coverStates = container;
    }

    @Override
    public PalettedContainer<LatexCoverState> getLatexStates() {
        return coverStates;
    }
}
