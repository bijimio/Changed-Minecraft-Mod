package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedFluids;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;

public class DarkLatexFluidBlock extends AbstractLatexFluidBlock {
    public DarkLatexFluidBlock() {
        super(ChangedFluids.DARK_LATEX, BlockBehaviour.Properties.of().strength(100f));
    }

    @Override
    public @NotNull LatexCoverState getLatexCoverState(BlockState blockState, BlockPos blockPos) {
        return blockState.getValue(GROUNDED) ? ChangedLatexTypes.DARK_LATEX.get().sourceCoverState() : ChangedLatexTypes.NONE.get().defaultCoverState();
    }
}