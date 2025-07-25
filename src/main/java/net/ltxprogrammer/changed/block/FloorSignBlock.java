package net.ltxprogrammer.changed.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FloorSignBlock extends ChangedBlock {
    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    protected static final Map<Direction.Axis, VoxelShape> SHAPES = Util.make(new HashMap<>(), map -> {
        map.put(Direction.Axis.Z, Block.box(4, 0, 2, 12, 13.5, 14));
        map.put(Direction.Axis.X, Block.box(2, 0, 4, 14, 13.5, 12));
    });

    public FloorSignBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HORIZONTAL_AXIS, Direction.Axis.Z));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZONTAL_AXIS);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState.setValue(HORIZONTAL_AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return SHAPES.get(state.getValue(HORIZONTAL_AXIS));
    }
}
