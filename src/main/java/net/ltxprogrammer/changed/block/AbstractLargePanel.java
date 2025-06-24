package net.ltxprogrammer.changed.block;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AbstractLargePanel extends HorizontalDirectionalBlock implements NonLatexCoverableBlock {
    public static final EnumProperty<NineSection> SECTION = EnumProperty.create("section", NineSection.class);
    public static final VoxelShape SHAPE_FRAME = Block.box(-8.0D, 0.0D, 12.0D, 24.0D, 40.0D, 16.0D);

    public AbstractLargePanel(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SECTION, NineSection.CENTER));
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), SHAPE_FRAME);

        double x = 0.0D;
        double z = 0.0D;

        switch (state.getValue(FACING)) {
            case NORTH -> x = 1.0D;
            case EAST -> z = 1.0D;
            case SOUTH -> x = -1.0D;
            case WEST -> z = -1.0D;
        }

        switch (state.getValue(SECTION)) {
            case BOTTOM_LEFT -> { return shape.move(-x, 0.0D, -z); }
            case MIDDLE_LEFT -> { return shape.move(-x, -1.0D, -z); }
            case TOP_LEFT -> { return shape.move(-x, -2.0D, -z); }

            case BOTTOM_MIDDLE -> { return shape.move(0, 0.0D, 0); }
            case CENTER -> { return shape.move(0, -1.0D, 0); }
            case TOP_MIDDLE -> { return shape.move(0, -2.0D, 0); }

            case BOTTOM_RIGHT -> { return shape.move(x, 0.0D, z); }
            case MIDDLE_RIGHT -> { return shape.move(x, -1.0D, z); }
            case TOP_RIGHT -> { return shape.move(x, -2.0D, z); }
        }

        return shape;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, SECTION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Direction direction = context.getHorizontalDirection();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1) {
            for (var sect : NineSection.CENTER.getOtherValues()) {
                if (!level.getBlockState(NineSection.CENTER.getRelative(blockpos, direction.getOpposite(), sect)).canBeReplaced(context))
                    return null;
            }

            return this.defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(SECTION, NineSection.CENTER);
        } else {
            return null;
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return state.getValue(SECTION) == NineSection.CENTER ?
                new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance())) :
                List.of();
    }

    @Override
    public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        super.setPlacedBy(level, pos, state, entity, item);
        var thisSect = state.getValue(SECTION);
        for (var sect : thisSect.getOtherValues())
            level.setBlockAndUpdate(thisSect.getRelative(pos, state.getValue(FACING), sect), state.setValue(SECTION, sect));
    }

    protected BlockState getBlockState(BlockState state, LevelReader level, BlockPos pos, NineSection otherSect) {
        if (state.getValue(SECTION) == otherSect)
            return state;
        return level.getBlockState(state.getValue(SECTION).getRelative(pos, state.getValue(FACING), otherSect));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos, Either<Boolean, Direction> allCheckOrDir) {
        final Direction facing = state.getValue(FACING);
        if (allCheckOrDir.left().isPresent() && !allCheckOrDir.left().get() && state.getValue(SECTION) == NineSection.CENTER)
            return level.getBlockState(pos.relative(facing.getOpposite())).isFaceSturdy(level, pos.relative(facing.getOpposite()), facing);

        var thisSect = state.getValue(SECTION);
        for (var sect : allCheckOrDir.left().isPresent() && allCheckOrDir.left().get() ? Arrays.stream(NineSection.values()).toList() : thisSect.getOtherValues()) {
            if (allCheckOrDir.right().isPresent()) {
                if (!thisSect.isRelative(sect, facing, allCheckOrDir.right().get()))
                    continue;
            }

            var other = level.getBlockState(thisSect.getRelative(pos, facing, sect));
            if (other.is(this) && other.getValue(SECTION) == sect)
                continue;
            return false;
        }

        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canSurvive(state, level, pos, Either.left(false));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherBlockPos) {
        if (!this.canSurvive(state, level, pos, Either.right(direction)))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, otherState, level, pos, otherBlockPos);
    }

    protected void preventCreativeDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        var section = state.getValue(SECTION);
        if (section != NineSection.CENTER) {
            BlockPos blockpos = section.getRelative(pos, state.getValue(FACING), NineSection.CENTER);
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(SECTION) == NineSection.CENTER) {
                BlockState blockstate1 = blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                preventCreativeDropFromBottomPart(level, pos, state, player);
            } else if (state.getValue(SECTION) != NineSection.CENTER) {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return super.rotate(state, rotation);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE)
            return super.mirror(state, mirror);
        else {
            return super.mirror(state, mirror).setValue(SECTION, state.getValue(SECTION).getHorizontalNeighbor());
        }
    }
}
