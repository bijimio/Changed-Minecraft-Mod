package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedFluids;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public class DarkLatexFluidBlock extends AbstractLatexFluidBlock {
    public DarkLatexFluidBlock() {
        super(() -> (FlowingFluid)ChangedFluids.DARK_LATEX.get(), BlockBehaviour.Properties.of().strength(100f));
    }
}