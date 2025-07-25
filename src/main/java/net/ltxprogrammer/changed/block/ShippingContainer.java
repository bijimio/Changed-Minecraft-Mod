package net.ltxprogrammer.changed.block;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.block.entity.OpenableDoor;
import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShippingContainer extends HorizontalDirectionalBlock/* implements PartialEntityBlock*/ {
    public static final EnumProperty<TwoXFourXTwoSection> SECTION = EnumProperty.create("section", TwoXFourXTwoSection.class);

    public static final VoxelShape SHAPE_FRAME = Shapes.or(
            Block.box(-31.0D, 0.0D, 0.0D, 31.0D, 2.0D, 32.0D),
            Block.box(-31.0D, 30.0D, 0.0D, 31.0D, 32.0D, 32.0D),
            Block.box(-31.0D, 0.0D, 0.0D, 31.0D, 32.0D, 2.0D),
            Block.box(-31.0D, 0.0D, 30.0D, 31.0D, 32.0D, 32.0D)
    );

    public static final VoxelShape SHAPE_DOORS = Shapes.or(
            Block.box(-31.0D, 2.0D, 2.0D, -29.0D, 30.0D, 30.0D),
            Block.box(29.0D, 2.0D, 2.0D, 31.0D, 30.0D, 30.0D)
    );

    public static final VoxelShape INTERACTION_SHAPE = Block.box(-31.0D, 0.0D, 0.0D, 31.0D, 32.0D, 32.0D);

    public static final VoxelShape SHAPE_COLLISION_CLOSED = Shapes.or(SHAPE_FRAME, SHAPE_DOORS);

    private final VoxelShape shapeFrame;
    private final VoxelShape shapeCollisionClosed;

    public ShippingContainer() {
        super(Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.5F, 9.0F));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SECTION, TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT));
        this.shapeFrame = SHAPE_FRAME;
        this.shapeCollisionClosed = SHAPE_COLLISION_CLOSED;
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), shapeCollisionClosed);

        var offset = state.getValue(SECTION).getOffset(state.getValue(FACING), TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT);
        return shape.move(offset.getX(), offset.getY(), offset.getZ());
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getOcclusionShape(state, level, pos);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getOcclusionShape(state, level, pos);
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), INTERACTION_SHAPE);

        var offset = state.getValue(SECTION).getOffset(state.getValue(FACING), TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT);
        return shape.move(offset.getX(), offset.getY(), offset.getZ());
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState p_60581_, BlockGetter p_60582_, BlockPos p_60583_) {
        return Shapes.block();
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
        if (blockpos.getY() < level.getMaxBuildHeight() - 2) {
            for (var sect : TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT.getOtherValues()) {
                if (!level.getBlockState(TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT.getRelative(blockpos, direction.getOpposite(), sect)).canBeReplaced(context))
                    return null;
            }

            return this.defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(SECTION, TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT);
        } else {
            return null;
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return state.getValue(SECTION) == TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT ?
                new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance())) :
                List.of();
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos blockpos, BlockState blockState) {
        if (blockState.getValue(SECTION) == TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT)
            super.spawnDestroyParticles(level, player, blockpos, blockState);
    }

    @Override
    public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    /*public BlockPos getBlockEntityPos(BlockState state, BlockPos pos) {
        return state.getValue(SECTION).getRelative(pos, state.getValue(FACING), ThreeXThreeSection.CENTER);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos bePos = getBlockEntityPos(state, pos);
        BlockState beState = level.getBlockState(bePos);
        StasisChamberBlockEntity blockEntity = level.getBlockEntity(pos, ChangedBlockEntities.STASIS_CHAMBER.get()).orElse(null);

        if (blockEntity != null && blockEntity.getChamberedEntity().map(chambered -> chambered == player).orElse(false))
            return InteractionResult.FAIL;

        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, getMenuProvider(beState, level, bePos), extra -> {
                extra.writeBlockPos(bePos);
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }*/

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        super.setPlacedBy(level, pos, state, entity, item);
        var thisSect = state.getValue(SECTION);
        for (var sect : thisSect.getOtherValues())
            level.setBlock(thisSect.getRelative(pos, state.getValue(FACING), sect), state.setValue(SECTION, sect), 18);
    }

    protected BlockState getBlockState(BlockState state, LevelReader level, BlockPos pos, TwoXFourXTwoSection otherSect) {
        if (state.getValue(SECTION) == otherSect)
            return state;
        return level.getBlockState(state.getValue(SECTION).getRelative(pos, state.getValue(FACING), otherSect));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos, Either<Boolean, Direction> allCheckOrDir) {
        if (allCheckOrDir.left().isPresent() && !allCheckOrDir.left().get() && state.getValue(SECTION) == TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT)
            return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);

        var thisSect = state.getValue(SECTION);
        for (var sect : allCheckOrDir.left().isPresent() && allCheckOrDir.left().get() ? Arrays.stream(TwoXFourXTwoSection.values()).toList() : thisSect.getOtherValues()) {
            if (allCheckOrDir.right().isPresent()) {
                if (!thisSect.isRelative(sect, state.getValue(FACING), allCheckOrDir.right().get()))
                    continue;
            }

            var other = level.getBlockState(thisSect.getRelative(pos, state.getValue(FACING), sect));
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
        if (section != TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT) {
            BlockPos blockpos = section.getRelative(pos, state.getValue(FACING), TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT);
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(SECTION) == TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT) {
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
            } else if (state.getValue(SECTION) != TwoXFourXTwoSection.FRONT_BOTTOM_MIDDLE_LEFT) {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return super.rotate(state, rotation);
    }

    /*@Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StasisChamberBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(level, type, ChangedBlockEntities.STASIS_CHAMBER.get());
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeE, BlockEntityTicker<? super E> ticker) {
        return typeE == typeA ? (BlockEntityTicker<A>)ticker : null;
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type, BlockEntityType<? extends StasisChamberBlockEntity> newType) {
        return level.isClientSide ? null : createTickerHelper(type, newType, StasisChamberBlockEntity::serverTick);
    }

    @Override
    public boolean stateHasBlockEntity(BlockState blockState) {
        return false;
    }*/

    /*@Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        if (this.stateHasBlockEntity(state))
            return level.getBlockEntity(pos, ChangedBlockEntities.STASIS_CHAMBER.get()).orElse(null);
        return null;
    }*/
}
