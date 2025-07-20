package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class DarkLatexBlock extends AbstractLatexBlock {
    public DarkLatexBlock(Properties properties) {
        super(properties, ChangedLatexTypes.DARK_LATEX, ChangedItems.DARK_LATEX_GOO);
    }

    @Override
    public @NotNull LatexCoverState getLatexCoverState(BlockState blockState, BlockPos blockPos) {
        return ChangedLatexTypes.DARK_LATEX.get().sourceCoverState();
    }

    public static final List<Supplier<? extends TransfurCrystalBlock>> SMALL_CRYSTALS = List.of(
            ChangedBlocks.LATEX_CRYSTAL,
            ChangedBlocks.DARK_DRAGON_CRYSTAL,
            ChangedBlocks.BEIFENG_CRYSTAL_SMALL,
            ChangedBlocks.WOLF_CRYSTAL_SMALL
    );

    public static final List<Supplier<? extends TransfurCrystalBlock>> CRYSTALS = List.of(
            ChangedBlocks.DARK_LATEX_CRYSTAL_LARGE,
            ChangedBlocks.BEIFENG_CRYSTAL,
            ChangedBlocks.WOLF_CRYSTAL
    );

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        BlockState plant = plantable.getPlant(world, pos.relative(facing));
        if (plant.getBlock() instanceof TransfurCrystalBlock)
            return true;
        else
            return false;
    }
}
