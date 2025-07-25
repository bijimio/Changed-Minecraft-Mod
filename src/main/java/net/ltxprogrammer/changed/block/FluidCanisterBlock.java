package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.GasCanisterBlockEntity;
import net.ltxprogrammer.changed.fluid.Gas;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.item.GasCanister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.ltxprogrammer.changed.item.GasCanister.CAPACITY;

public class FluidCanisterBlock extends AbstractCustomShapeTallEntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final VoxelShape SHAPE_WHOLE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 28.0D, 12.0D);
    private final @Nullable Supplier<? extends Gas> gas;
    private FluidState stateOpen;
    private FluidState stateClosed;

    private void maybeInitializeFluidStates() {
        if (stateOpen == null)
            stateOpen = gas != null ? gas.get().getSource(false) : Fluids.EMPTY.defaultFluidState();
        if (stateClosed == null)
            stateClosed = Fluids.EMPTY.defaultFluidState();
    }

    public FluidCanisterBlock(@Nullable Supplier<? extends Gas> gas) {
        super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(0.7F));
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
        this.gas = gas;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (!(stack.getItem() instanceof GasCanister))
            return;
        if (!(level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below()) instanceof GasCanisterBlockEntity lBlockEntity))
            return;
        if (!(level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos) instanceof GasCanisterBlockEntity uBlockEntity))
            return;

        lBlockEntity.setUsage(stack.getDamageValue());
        uBlockEntity.setUsage(stack.getDamageValue());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        var blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        var canisterEntity = blockEntity instanceof GasCanisterBlockEntity ? (GasCanisterBlockEntity)blockEntity : null;
        return super.getDrops(state, builder).stream().peek(itemStack -> {
            if (canisterEntity != null && itemStack.getItem() instanceof GasCanister)
                itemStack.setDamageValue(canisterEntity.getUsage());
        }).collect(Collectors.toList());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER)
            return SHAPE_WHOLE.move(0, -1, 0);
        else
            return SHAPE_WHOLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GasCanisterBlockEntity(pos, state);
    }

    @Override
    public boolean stateHasBlockEntity(BlockState blockState) {
        return true;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
            return super.use(state, level, pos, player, hand, hitResult);

        var beBottom = level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), ChangedBlockEntities.GAS_CANISTER.get());
        var beTop = level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, ChangedBlockEntities.GAS_CANISTER.get());
        if (beBottom.isEmpty() || beTop.isEmpty())
            return super.use(state, level, pos, player, hand, hitResult);

        if (beTop.get().getUsage() < CAPACITY && !state.getValue(OPEN)) {
            level.setBlockAndUpdate(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, true));
            level.setBlockAndUpdate(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(OPEN, true));
            level.scheduleTick(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, this, 1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        else if (state.getValue(OPEN)) {
            level.setBlockAndUpdate(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
            level.setBlockAndUpdate(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(OPEN, false));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        var beBottom = level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), ChangedBlockEntities.GAS_CANISTER.get());
        var beTop = level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, ChangedBlockEntities.GAS_CANISTER.get());
        if (beBottom.isEmpty() || beTop.isEmpty())
            return;

        if (beTop.get().getUsage() >= CAPACITY && state.getValue(OPEN)) {
            level.setBlockAndUpdate(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
            level.setBlockAndUpdate(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(OPEN, false));
            return;
        }

        if (beTop.get().getUsage() >= CAPACITY || !state.getValue(OPEN))
            return;

        int usage = beTop.get().getUsage() + 1;
        beTop.get().setUsage(usage);
        beBottom.get().setUsage(usage);

        level.scheduleTick(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, this, 1);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        maybeInitializeFluidStates();
        return blockState.getValue(OPEN) ? stateOpen : stateClosed;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        if (state.getValue(OPEN) && gas != null) {
            level.scheduleTick(pos, gas.get(), gas.get().getTickDelay(level));
            level.scheduleTick(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below(), gas.get(), gas.get().getTickDelay(level));
        }

        return super.updateShape(state, direction, otherState, level, pos, otherPos);
    }
}
