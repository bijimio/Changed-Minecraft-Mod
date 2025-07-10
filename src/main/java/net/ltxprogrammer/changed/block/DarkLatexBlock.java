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
    public void latexTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull RandomSource random) {
        if (level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE) == 0 ||
                random.nextInt(5000) > level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE))
            return;

        BlockPos above = position.above();
        BlockPos above2 = above.above();
        boolean isAboveAir = level.getBlockState(above).is(Blocks.AIR);
        boolean isAbove2Air = level.getBlockState(above2).is(Blocks.AIR);
        if (isAboveAir && canSupportRigidBlock(level, position)) { // Do growth event
            long crystalCount = level.getBlockStates(new AABB(position).inflate(3.0))
                    .filter(neighbor -> neighbor.is(ChangedTags.Blocks.LATEX_CRYSTAL))
                    .count();

            if (crystalCount > 6) return;

            if (random.nextFloat() < 0.75f || !isAbove2Air) {
                level.setBlockAndUpdate(above, Util.getRandom(SMALL_CRYSTALS, random).get().defaultBlockState());
            } else {
                final var newBlockState = Util.getRandom(CRYSTALS, random).get().defaultBlockState();
                level.setBlockAndUpdate(above, newBlockState.setValue(AbstractDoubleTransfurCrystal.HALF, DoubleBlockHalf.LOWER));
                level.setBlockAndUpdate(above2, newBlockState.setValue(AbstractDoubleTransfurCrystal.HALF, DoubleBlockHalf.UPPER));
            }
        }
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        BlockState plant = plantable.getPlant(world, pos.relative(facing));
        if (plant.getBlock() instanceof TransfurCrystalBlock)
            return true;
        else
            return false;
    }
}
