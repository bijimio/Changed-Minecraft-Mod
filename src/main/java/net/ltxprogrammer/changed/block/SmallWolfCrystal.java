package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;


public class SmallWolfCrystal extends TransfurCrystalBlock {
    public SmallWolfCrystal(Properties p_53514_) {
        super(ChangedTransfurVariants.CRYSTAL_WOLF, ChangedItems.WOLF_CRYSTAL_FRAGMENT, p_53514_);
    }

    @Override
    protected boolean mayPlaceOn(BlockState otherBlock, BlockGetter level, BlockPos blockPos) {
        return otherBlock.getBlock() instanceof WolfCrystalBlock ||
                AbstractLatexBlock.isSurfaceOfType(LatexCoverGetter.extendDefault(level), blockPos, Direction.DOWN, SupportType.RIGID, ChangedLatexTypes.DARK_LATEX.get());
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        BlockState blockStateOn = level.getBlockState(blockPos.below());
        if (!canSupportRigidBlock(level, blockPos.below()))
            return false;
        return blockStateOn.getBlock() instanceof WolfCrystalBlock ||
                AbstractLatexBlock.isSurfaceOfType(level, blockPos, Direction.DOWN, SupportType.RIGID, ChangedLatexTypes.DARK_LATEX.get());
    }
}