package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class LaserEmitterBlock extends DirectionalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public LaserEmitterBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(5.5F, 6.0F).requiresCorrectToolForDrops());
    }

    public BlockState rotate(BlockState p_55115_, Rotation p_55116_) {
        return p_55115_.setValue(FACING, p_55116_.rotate(p_55115_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_55112_, Mirror p_55113_) {
        return p_55112_.rotate(p_55113_.getRotation(p_55112_.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED, FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(POWERED, Boolean.FALSE);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        this.checkIfExtend(level, pos, state);
    }

    private boolean getNeighborSignal(Level level, BlockPos blockPos, Direction originalDirection) {
        for(Direction direction : Direction.values()) {
            if (direction != originalDirection && level.hasSignal(blockPos.relative(direction), direction)) {
                return true;
            }
        }

        if (level.hasSignal(blockPos, Direction.DOWN)) {
            return true;
        } else {
            BlockPos blockpos = blockPos.above();

            for(Direction direction1 : Direction.values()) {
                if (direction1 != Direction.DOWN && level.hasSignal(blockpos.relative(direction1), direction1)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static final int MAX_DISTANCE = 24;
    private void checkIfExtend(Level level, BlockPos blockPos, BlockState blockState) {
        Direction direction = blockState.getValue(FACING);
        boolean shouldPower = this.getNeighborSignal(level, blockPos, direction);
        if (shouldPower && !blockState.getValue(POWERED)) {
            level.setBlock(blockPos, blockState.setValue(POWERED, Boolean.TRUE), 3);
            int distance = 0;
            while (distance < MAX_DISTANCE) {
                BlockPos nextPos = blockPos.relative(direction, ++distance);
                BlockState nextState = level.getBlockState(nextPos);
                if (!nextState.isAir() && !nextState.is(ChangedTags.Blocks.LASER_TRANSLUCENT))
                    break;
                if (!nextState.isAir() && nextState.is(ChangedTags.Blocks.LASER_TRANSLUCENT))
                    continue;
                level.setBlockAndUpdate(nextPos, ChangedBlocks.LASER_BEAM.get().defaultBlockState().setValue(FACING, direction)
                        .setValue(LaserBeamBlock.DISTANCE, distance));
            }

            if (level instanceof ServerLevel serverLevel)
                ChangedSounds.broadcastSound(serverLevel, ChangedSounds.SHOT1, blockPos, 1, 1);
        } else if (!shouldPower && blockState.getValue(POWERED)) {
            level.setBlockAndUpdate(blockPos, blockState.setValue(POWERED, Boolean.FALSE));
            int distance = 0;
            while (distance < MAX_DISTANCE) {
                BlockPos nextPos = blockPos.relative(direction, ++distance);
                BlockState nextState = level.getBlockState(nextPos);
                if (!nextState.is(ChangedTags.Blocks.LASER_TRANSLUCENT))
                    break;
                if (!nextState.is(ChangedBlocks.LASER_BEAM.get()))
                    continue;
                level.setBlockAndUpdate(nextPos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        Direction facing = state.getValue(FACING);
        BlockState laserState = ChangedBlocks.LASER_BEAM.get().defaultBlockState().setValue(FACING, facing);
        if (facing == direction && state.getValue(POWERED)) {
            int distance = 0;
            while (distance < LaserEmitterBlock.MAX_DISTANCE) {
                BlockPos nextPos = pos.relative(direction, ++distance);
                BlockState nextState = level.getBlockState(nextPos);
                if (!nextState.isAir() && !nextState.is(ChangedTags.Blocks.LASER_TRANSLUCENT))
                    break;
                if (!nextState.isAir() && nextState.is(ChangedTags.Blocks.LASER_TRANSLUCENT))
                    continue;
                level.setBlock(nextPos, laserState.setValue(LaserBeamBlock.DISTANCE, distance), 3);
            }
        }
        return super.updateShape(state, direction, otherState, level, pos, otherPos);
    }

    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPosOther, boolean p_60203_) {
        if (!level.isClientSide) {
            this.checkIfExtend(level, blockPos, blockState);
        }
    }
}
