package net.ltxprogrammer.changed.block;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ConnectedFloorBlock extends ChangedBlock {
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 46);
    private int getRotateC90(int state) {
        return switch (state) {
            case 1 -> 12;
            case 2 -> 24;
            case 3 -> 36;
            case 4 -> 5;
            case 5 -> 17;
            case 6 -> 7;
            case 7 -> 19;
            case 8 -> 9;
            case 9 -> 21;
            case 10 -> 11;
            case 11 -> 23;
            case 12 -> 3;
            case 13 -> 15;
            case 14 -> 27;
            case 15 -> 39;
            case 16 -> 4;
            case 17 -> 16;
            case 18 -> 6;
            case 19 -> 18;
            case 20 -> 8;
            case 21 -> 20;
            case 22 -> 10;
            case 23 -> 22;
            case 24 -> 2;
            case 25 -> 14;
            case 27 -> 38;
            case 28 -> 29;
            case 29 -> 41;
            case 30 -> 31;
            case 31 -> 43;
            case 32 -> 33;
            case 33 -> 45;
            case 34 -> 35;
            case 35 -> 34;
            case 36 -> 1;
            case 37 -> 13;
            case 38 -> 25;
            case 39 -> 37;
            case 40 -> 28;
            case 41 -> 40;
            case 42 -> 30;
            case 43 -> 41;
            case 44 -> 32;
            case 45 -> 44;
            default -> state;
        };
    }

    private int getRotate180(int state) {
        return getRotateC90(getRotateC90(state));
    }

    private int getRotateCC90(int state) {
        return getRotate180(getRotateC90(state));
    }

    private int calculateRotate(Rotation rotation, int state) {
        if (rotation == Rotation.NONE)
            return state;

        return switch (rotation) {
            case CLOCKWISE_90 -> getRotateC90(state);
            case CLOCKWISE_180 -> getRotate180(state);
            case COUNTERCLOCKWISE_90 -> getRotateCC90(state);
            default -> state;
        };
    }

    private int getMirrorAcrossX(int state) {
        return switch (state) {
            case 4 -> 16;
            case 5 -> 17;
            case 7 -> 18;
            case 8 -> 9;
            case 9 -> 8;
            case 11 -> 22;
            case 12 -> 36;
            case 13 -> 37;
            case 14 -> 38;
            case 15 -> 39;
            case 16 -> 4;
            case 17 -> 5;
            case 18 -> 7;
            case 20 -> 21;
            case 21 -> 20;
            case 22 -> 11;
            case 28 -> 30;
            case 29 -> 42;
            case 30 -> 28;
            case 31 -> 40;
            case 32 -> 44;
            case 33 -> 45;
            case 34 -> 35;
            case 35 -> 34;
            case 36 -> 12;
            case 37 -> 13;
            case 38 -> 14;
            case 39 -> 15;
            case 40 -> 31;
            case 41 -> 43;
            case 42 -> 29;
            case 43 -> 41;
            case 44 -> 32;
            case 45 -> 33;
            default -> state;
        };
    }

    private int getMirrorAcrossY(int state) {
        return getRotateCC90(getMirrorAcrossX(getRotateC90(state)));
    }

    private int calculateMirror(Mirror mirror, int state) {
        return switch (mirror) {
            case FRONT_BACK -> getMirrorAcrossX(state);
            case LEFT_RIGHT -> getMirrorAcrossY(state);
            default -> state;
        };
    }

    public enum CornerState { // Based on ctm_compact
        INNER(0),
        NONE(1),
        VERTICAL(2),
        HORIZONTAL(3),
        OUTER(4);

        public final int stateIndex;

        CornerState(int stateIndex) {
            this.stateIndex = stateIndex;
        }

        public static CornerState fromBoolean(boolean vertical, boolean horizontal, boolean corner) {
            if (vertical && horizontal)
                return corner ? NONE : OUTER;
            if (vertical)
                return VERTICAL;
            return horizontal ? HORIZONTAL : INNER;
        }

        public CornerState setVertical(boolean vertical) {
            return switch (this) {
                case INNER, VERTICAL -> vertical ? VERTICAL : INNER;
                case OUTER, HORIZONTAL -> vertical ? OUTER : HORIZONTAL;
                case NONE -> vertical ? NONE : HORIZONTAL;
            };
        }

        public CornerState setHorizontal(boolean horizontal) {
            return switch (this) {
                case INNER, HORIZONTAL -> horizontal ? HORIZONTAL : INNER;
                case OUTER, VERTICAL -> horizontal ? OUTER : VERTICAL;
                case NONE -> horizontal ? NONE : VERTICAL;
            };
        }

        public CornerState setCorner(boolean corner) {
            return switch (this) {
                case NONE, OUTER -> corner ? NONE : OUTER;
                default -> this;
            };
        }

        public boolean isHorizontal() {
            return switch (this) {
                case HORIZONTAL, OUTER, NONE -> true;
                default -> false;
            };
        }

        public boolean isVertical() {
            return switch (this) {
                case VERTICAL, OUTER, NONE -> true;
                default -> false;
            };
        }

        public boolean isCorner() {
            return this == NONE;
        }

        public boolean wantsCorner() {
            return switch (this) {
                case OUTER, NONE -> true;
                default -> false;
            };
        }
    }

    public record CornerSet(CornerState topLeft, CornerState topRight, CornerState bottomLeft, CornerState bottomRight) {
        public static CornerSet of(CornerState topLeft, CornerState topRight, CornerState bottomLeft, CornerState bottomRight) {
            return new CornerSet(topLeft, topRight, bottomLeft, bottomRight);
        }
    }

    public static final List<CornerSet> STATE_CORNERS = Util.make(new ImmutableList.Builder<CornerSet>(), builder -> {
        // Index based on https://optifine.readthedocs.io/ctm.html#ctm-standard-8-way
        builder.add(CornerSet.of(CornerState.INNER, CornerState.INNER, CornerState.INNER, CornerState.INNER));
        builder.add(CornerSet.of(CornerState.INNER, CornerState.HORIZONTAL, CornerState.INNER, CornerState.HORIZONTAL));
        builder.add(CornerSet.of(CornerState.HORIZONTAL, CornerState.HORIZONTAL, CornerState.HORIZONTAL, CornerState.HORIZONTAL));
        builder.add(CornerSet.of(CornerState.HORIZONTAL, CornerState.INNER, CornerState.HORIZONTAL, CornerState.INNER));

        builder.add(CornerSet.of(CornerState.INNER, CornerState.HORIZONTAL, CornerState.VERTICAL, CornerState.OUTER));
        builder.add(CornerSet.of(CornerState.HORIZONTAL, CornerState.INNER, CornerState.OUTER, CornerState.VERTICAL));
        builder.add(CornerSet.of(CornerState.VERTICAL, CornerState.OUTER, CornerState.VERTICAL, CornerState.OUTER));
        builder.add(CornerSet.of(CornerState.HORIZONTAL, CornerState.HORIZONTAL, CornerState.OUTER, CornerState.OUTER));

        builder.add(CornerSet.of(CornerState.OUTER, CornerState.NONE, CornerState.OUTER, CornerState.OUTER));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.OUTER, CornerState.OUTER, CornerState.NONE));
        builder.add(CornerSet.of(CornerState.NONE, CornerState.OUTER, CornerState.NONE, CornerState.OUTER));
        builder.add(CornerSet.of(CornerState.NONE, CornerState.NONE, CornerState.OUTER, CornerState.OUTER));

        // 12
        builder.add(CornerSet.of(CornerState.INNER, CornerState.INNER, CornerState.VERTICAL, CornerState.VERTICAL));
        builder.add(CornerSet.of(CornerState.INNER, CornerState.HORIZONTAL, CornerState.VERTICAL, CornerState.NONE));
        builder.add(CornerSet.of(CornerState.HORIZONTAL, CornerState.HORIZONTAL, CornerState.NONE, CornerState.NONE));
        builder.add(CornerSet.of(CornerState.HORIZONTAL, CornerState.INNER, CornerState.NONE, CornerState.VERTICAL));

        builder.add(CornerSet.of(CornerState.VERTICAL, CornerState.OUTER, CornerState.INNER, CornerState.HORIZONTAL));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.VERTICAL, CornerState.HORIZONTAL, CornerState.INNER));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.OUTER, CornerState.HORIZONTAL, CornerState.HORIZONTAL));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.VERTICAL, CornerState.OUTER, CornerState.VERTICAL));

        builder.add(CornerSet.of(CornerState.NONE, CornerState.OUTER, CornerState.OUTER, CornerState.OUTER));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.OUTER, CornerState.NONE, CornerState.OUTER));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.OUTER, CornerState.NONE, CornerState.NONE));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.NONE, CornerState.OUTER, CornerState.NONE));

        // 24
        builder.add(CornerSet.of(CornerState.VERTICAL, CornerState.VERTICAL, CornerState.VERTICAL, CornerState.VERTICAL));
        builder.add(CornerSet.of(CornerState.VERTICAL, CornerState.NONE, CornerState.VERTICAL, CornerState.NONE));
        builder.add(CornerSet.of(CornerState.NONE, CornerState.NONE, CornerState.NONE, CornerState.NONE));
        builder.add(CornerSet.of(CornerState.NONE, CornerState.VERTICAL, CornerState.NONE, CornerState.VERTICAL));

        builder.add(CornerSet.of(CornerState.VERTICAL, CornerState.OUTER, CornerState.VERTICAL, CornerState.NONE));
        builder.add(CornerSet.of(CornerState.HORIZONTAL, CornerState.HORIZONTAL, CornerState.NONE, CornerState.OUTER));
        builder.add(CornerSet.of(CornerState.VERTICAL, CornerState.NONE, CornerState.VERTICAL, CornerState.OUTER));
        builder.add(CornerSet.of(CornerState.HORIZONTAL, CornerState.HORIZONTAL, CornerState.OUTER, CornerState.NONE));

        builder.add(CornerSet.of(CornerState.NONE, CornerState.NONE, CornerState.NONE, CornerState.OUTER));
        builder.add(CornerSet.of(CornerState.NONE, CornerState.NONE, CornerState.OUTER, CornerState.NONE));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.NONE, CornerState.NONE, CornerState.OUTER));
        builder.add(CornerSet.of(CornerState.NONE, CornerState.OUTER, CornerState.OUTER, CornerState.NONE));

        // 36
        builder.add(CornerSet.of(CornerState.VERTICAL, CornerState.VERTICAL, CornerState.INNER, CornerState.INNER));
        builder.add(CornerSet.of(CornerState.VERTICAL, CornerState.NONE, CornerState.INNER, CornerState.HORIZONTAL));
        builder.add(CornerSet.of(CornerState.NONE, CornerState.NONE, CornerState.HORIZONTAL, CornerState.HORIZONTAL));
        builder.add(CornerSet.of(CornerState.NONE, CornerState.VERTICAL, CornerState.HORIZONTAL, CornerState.INNER));

        builder.add(CornerSet.of(CornerState.OUTER, CornerState.NONE, CornerState.HORIZONTAL, CornerState.HORIZONTAL));
        builder.add(CornerSet.of(CornerState.NONE, CornerState.VERTICAL, CornerState.OUTER, CornerState.VERTICAL));
        builder.add(CornerSet.of(CornerState.NONE, CornerState.OUTER, CornerState.HORIZONTAL, CornerState.HORIZONTAL));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.VERTICAL, CornerState.NONE, CornerState.VERTICAL));

        builder.add(CornerSet.of(CornerState.NONE, CornerState.OUTER, CornerState.NONE, CornerState.NONE));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.NONE, CornerState.NONE, CornerState.NONE));
        builder.add(CornerSet.of(CornerState.OUTER, CornerState.OUTER, CornerState.OUTER, CornerState.OUTER));
    }).build();

    private int computeFromCornerStates(CornerState topLeft, CornerState topRight, CornerState bottomLeft, CornerState bottomRight) {
        for (int index = 0; index < STATE_CORNERS.size(); ++index) {
            final var set = STATE_CORNERS.get(index);
            if (set.topLeft == topLeft && set.topRight == topRight &&
                    set.bottomLeft == bottomLeft && set.bottomRight == bottomRight)
                return index;
        }

        throw new IllegalStateException();
    }

    private CornerSet getCornerSet(int state) {
        return STATE_CORNERS.get(state);
    }

    private boolean isConnected(int state, BlockPos thisPos, BlockPos otherPos) {
        final var set = getCornerSet(state);
        CornerState topLeft = set.topLeft;
        CornerState topRight = set.topRight;
        CornerState bottomLeft = set.bottomLeft;
        CornerState bottomRight = set.bottomRight;

        int zOffset = otherPos.getZ() - thisPos.getZ();
        int xOffset = otherPos.getX() - thisPos.getX();

        if (zOffset == Direction.NORTH.getStepZ() && xOffset == 0) {
            return topLeft.isVertical() || topRight.isVertical();
        }

        else if (zOffset == 0 && xOffset == Direction.EAST.getStepX()) {
            return topRight.isHorizontal() || bottomRight.isHorizontal();
        }

        else if (zOffset == Direction.SOUTH.getStepZ() && xOffset == 0) {
            return bottomLeft.isVertical() || bottomRight.isVertical();
        }

        else if (zOffset == 0 && xOffset == Direction.WEST.getStepX()) {
            return topLeft.isHorizontal() || bottomLeft.isHorizontal();
        }

        else if (zOffset == Direction.NORTH.getStepZ() && xOffset == Direction.WEST.getStepX()) {
            return topLeft.isCorner();
        }

        else if (zOffset == Direction.NORTH.getStepZ() && xOffset == Direction.EAST.getStepX()) {
            return topRight.isCorner();
        }

        else if (zOffset == Direction.SOUTH.getStepZ() && xOffset == Direction.EAST.getStepX()) {
            return bottomRight.isCorner();
        }

        else if (zOffset == Direction.SOUTH.getStepZ() && xOffset == Direction.WEST.getStepX()) {
            return bottomLeft.isCorner();
        }

        return false;
    }

    private Stream<BlockPos> getConnected(BlockState state, BlockPos thisPos) {
        final var north = thisPos.north();
        final var east = thisPos.east();
        final var south = thisPos.south();
        final var west = thisPos.west();

        return Stream.of(north, north.east(), east, south.east(), south, south.west(), west, north.west())
                .filter(otherPos -> this.isConnected(state.getValue(STATE), thisPos, otherPos));
    }

    private Stream<BlockPos> getNotConnected(BlockState state, BlockPos thisPos) {
        final var north = thisPos.north();
        final var east = thisPos.east();
        final var south = thisPos.south();
        final var west = thisPos.west();

        return Stream.of(north, north.east(), east, south.east(), south, south.west(), west, north.west())
                .filter(otherPos -> !this.isConnected(state.getValue(STATE), thisPos, otherPos));
    }

    private boolean shouldConsiderBlock(BlockPos thisPos, BlockPos otherPos) {
        int zOffset = otherPos.getZ() - thisPos.getZ();
        int xOffset = otherPos.getX() - thisPos.getX();

        if (zOffset == 0 && xOffset == 0)
            return false;
        return Mth.abs(zOffset) <= 1 && Mth.abs(xOffset) <= 1;
    }

    private int setBlockToState(int state, BlockPos thisPos, BlockPos otherPos, boolean value) {
        final var set = getCornerSet(state);
        CornerState topLeft = set.topLeft;
        CornerState topRight = set.topRight;
        CornerState bottomLeft = set.bottomLeft;
        CornerState bottomRight = set.bottomRight;

        int zOffset = otherPos.getZ() - thisPos.getZ();
        int xOffset = otherPos.getX() - thisPos.getX();

        if (zOffset == Direction.NORTH.getStepZ() && xOffset == 0) {
            topLeft = topLeft.setVertical(value);
            topRight = topRight.setVertical(value);
        }

        else if (zOffset == 0 && xOffset == Direction.EAST.getStepX()) {
            topRight = topRight.setHorizontal(value);
            bottomRight = bottomRight.setHorizontal(value);
        }

        else if (zOffset == Direction.SOUTH.getStepZ() && xOffset == 0) {
            bottomLeft = bottomLeft.setVertical(value);
            bottomRight = bottomRight.setVertical(value);
        }

        else if (zOffset == 0 && xOffset == Direction.WEST.getStepX()) {
            topLeft = topLeft.setHorizontal(value);
            bottomLeft = bottomLeft.setHorizontal(value);
        }

        else if (zOffset == Direction.NORTH.getStepZ() && xOffset == Direction.WEST.getStepX()) {
            topLeft = topLeft.setCorner(value);
        }

        else if (zOffset == Direction.NORTH.getStepZ() && xOffset == Direction.EAST.getStepX()) {
            topRight = topRight.setCorner(value);
        }

        else if (zOffset == Direction.SOUTH.getStepZ() && xOffset == Direction.EAST.getStepX()) {
            bottomRight = bottomRight.setCorner(value);
        }

        else if (zOffset == Direction.SOUTH.getStepZ() && xOffset == Direction.WEST.getStepX()) {
            bottomLeft = bottomLeft.setCorner(value);
        }

        return computeFromCornerStates(topLeft, topRight, bottomLeft, bottomRight);
    }

    public ConnectedFloorBlock(Properties properties) {
        super(properties.hasPostProcess(ChangedBlocks::always));
        this.registerDefaultState(this.stateDefinition.any().setValue(STATE, 0));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STATE);
    }

    private boolean connectsTo(BlockState blockState) {
        return blockState.is(this);
    }

    private BlockState attachToOtherState(BlockState current, BlockPos thisPos, BlockState otherState, BlockPos blockPosOther) {
        AtomicInteger atomic = new AtomicInteger(current.getValue(STATE));
        if (this.connectsTo(otherState))
            atomic.getAndUpdate(state -> this.setBlockToState(state, thisPos, blockPosOther, true));
        else
            return current;

        this.getNotConnected(otherState, blockPosOther) // Remove corner pieces
                .filter(other -> this.shouldConsiderBlock(thisPos, other))
                .filter(other -> Mth.abs((other.getX() - thisPos.getX()) * (other.getZ() - thisPos.getZ())) == 1)
                .forEach(otherPos -> {
                    atomic.getAndUpdate(state -> this.setBlockToState(state, thisPos, otherPos, false));
                });
        this.getConnected(otherState, blockPosOther)
                .filter(other -> this.shouldConsiderBlock(thisPos, other))
                .forEach(otherPos -> {
                    atomic.getAndUpdate(state -> this.setBlockToState(state, thisPos, otherPos, true));
                });

        return current.setValue(STATE, atomic.getAcquire());
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!Direction.Plane.HORIZONTAL.test(context.getClickedFace()) || (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()))
            return super.getStateForPlacement(context);

        final var clickPos = context.getClickedPos();
        final var blockPosOther = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        final var otherState = context.getLevel().getBlockState(blockPosOther);

        return this.attachToOtherState(this.defaultBlockState(), clickPos, otherState, blockPosOther);
    }

    public BlockState rotate(BlockState state, Rotation direction) {
        switch(direction) {
            case NONE:
                return state;
            default:
                return state.setValue(STATE, calculateRotate(direction, state.getValue(STATE)));
        }
    }

    public BlockState mirror(BlockState blockState, Mirror mirror) {
        switch(mirror) {
            case NONE:
                return blockState;
            default:
                return blockState.setValue(STATE, calculateMirror(mirror, blockState.getValue(STATE)));
        }
    }

    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockStateOther, LevelAccessor level, BlockPos blockPos, BlockPos blockPosOther) {
        if (direction.getAxis() == Direction.Axis.Y)
            return super.updateShape(blockState, direction, blockStateOther, level, blockPos, blockPosOther);

        int currentState = blockState.getValue(STATE);
        if (this.isConnected(currentState, blockPos, blockPosOther)) {
            if (!this.connectsTo(blockStateOther)) {
                currentState = this.setBlockToState(currentState, blockPos, blockPosOther, false);
            }

            else { // Connect to other blocks the other state is connected to
                currentState = this.attachToOtherState(blockState, blockPos, blockStateOther, blockPosOther).getValue(STATE);
            }
        }

        else if (this.connectsTo(blockStateOther) && this.getConnected(blockStateOther, blockPosOther).anyMatch(blockPos::equals)) { // Other block is connected to this, so connect back
            currentState = this.attachToOtherState(blockState, blockPos, blockStateOther, blockPosOther).getValue(STATE);
        }

        return super.updateShape(blockState.setValue(STATE, currentState), direction, blockStateOther, level, blockPos, blockPosOther);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        // This is to allow the player to connect a tile to another, manually
        if (toolAction != ToolActions.AXE_SCRAPE)
            return super.getToolModifiedState(state, context, toolAction, simulate);

        BlockState nextState = state;
        for (var direction : Direction.Plane.HORIZONTAL) {
            final var otherPos = context.getClickedPos().relative(direction);
            nextState = this.attachToOtherState(nextState, context.getClickedPos(), context.getLevel().getBlockState(otherPos), otherPos);
        }

        return state != nextState ? nextState : super.getToolModifiedState(state, context, toolAction, simulate);
    }
}
