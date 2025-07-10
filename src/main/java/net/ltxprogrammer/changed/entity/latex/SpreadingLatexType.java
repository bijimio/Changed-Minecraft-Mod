package net.ltxprogrammer.changed.entity.latex;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.item.AbstractLatexBucket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SpreadingLatexType extends LatexType {
    public static final IntegerProperty SATURATION = IntegerProperty.create("saturation", 0, 15);
    // TODO surface properties to cache collision shape

    private final Map<LatexCoverState, VoxelShape> cachedShapes;

    public SpreadingLatexType() {
        super();
        this.registerDefaultCoverState(this.coverStateDefinition.any().setValue(SATURATION, 0));
        this.cachedShapes = Util.make(new HashMap<>(), map -> {
            this.coverStateDefinition.getPossibleStates().forEach(state -> {
                map.put(state, this.computeShapeForState(state));
            });
        });
    }

    protected VoxelShape computeShapeForState(LatexCoverState state) {
        return Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    }

    @Override
    protected void buildStateDefinition(StateDefinition.Builder<LatexType, LatexCoverState> builder) {
        builder.add(SATURATION);
    }

    public LatexCoverState spreadState(LatexCoverState state) {
        return state.setValue(SATURATION, state.getValue(SATURATION) + 1);
    }

    public boolean canSpread(LatexCoverState state) {
        return state.getValue(SATURATION) < 15;
    }

    public boolean shouldDecay(LatexCoverState state, LevelReader level, BlockPos blockPos) {
        final var thisSaturation = state.getValue(SATURATION);
        if (level.getBlockState(blockPos).getBlock() instanceof LatexCoveringSource)
            return false;
        return Arrays.stream(Direction.values())
                .map(blockPos::relative)
                .map(checkPos -> LatexCoverState.getAt(level, checkPos))
                .filter(otherState -> otherState.is(this))
                .noneMatch(otherState -> otherState.getValue(SATURATION) < thisSaturation);
    }

    private static boolean isValidSurface(BlockGetter level, BlockPos toCover, BlockPos neighbor, Direction coverNormal) {
        BlockState state = level.getBlockState(neighbor);
        return state.isFaceSturdy(level, neighbor, coverNormal.getOpposite(), SupportType.FULL);
    }

    @Override
    public void randomTick(LatexCoverState state, ServerLevel level, BlockPos blockPos, RandomSource random) {
        if (this.shouldDecay(state, level, blockPos)) {
            LatexCoverState.setAtAndUpdate(level, blockPos, ChangedLatexTypes.NONE.get().defaultCoverState());
            return;
        }
        if (!this.canSpread(state)) return;
        if (level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE) == 0) return;
        if (!level.isAreaLoaded(blockPos, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
        if (random.nextInt(10 * level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE)) < 600) return;

        Direction checkDir = Direction.getRandom(random);
        BlockPos.MutableBlockPos checkPos = blockPos.relative(checkDir).mutable();

        BlockState checkState = level.getBlockState(checkPos);
        LatexCoverState checkCoverState = LatexCoverState.getAt(level, checkPos);

        boolean isAirOrLessThanSpread = checkCoverState.isAir() ||
                (checkCoverState.is(this) && checkCoverState.getValue(SATURATION) > state.getValue(SATURATION) + 1);

        if (!checkState.isCollisionShapeFullBlock(level, checkPos) && isAirOrLessThanSpread) {
            if (checkPos.subtract(blockPos).getY() > 0 && random.nextInt(3) > 0) // Reduced chance of spreading up
                return;

            if (Arrays.stream(Direction.values()).noneMatch(direction -> isValidSurface(level, checkPos, checkPos.relative(direction), direction)))
                return;

            LatexCoverState.setAtAndUpdate(level, checkPos, this.spreadState(state));

            /*var event = new LatexCoveredBlocks.CoveringBlockEvent(latexType, checkState, checkPos, level);
            if (Changed.postModEvent(event))
                return;
            if (event.originalState == event.plannedState)
                return;
            level.setBlockAndUpdate(event.blockPos, event.plannedState);*/
        }
    }

    @Override
    public LatexCoverState updateShape(LatexCoverState state, Direction direction, LatexCoverState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        if (neighborState.getType() != this)
            return state;
        if (neighborState.getValue(SATURATION) < state.getValue(SATURATION))
            return neighborState.setValue(SATURATION, neighborState.getValue(SATURATION) + 1);
        return state;
    }

    @Override
    public LatexCoverState updateInPlace(LatexCoverState state, BlockState oldState, BlockState newState, LevelAccessor level, BlockPos pos) {
        if (newState.isAir())
            return state;
        if (newState.getBlock() instanceof LatexCoveringSource source)
            return source.getLatexCoverState(newState, pos);
        if (newState.isCollisionShapeFullBlock(level, pos))
            return ChangedLatexTypes.NONE.get().defaultCoverState();

        return state;
    }

    @Override
    public VoxelShape getShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return cachedShapes.get(state);
    }

    @Override
    public InteractionResult use(LatexCoverState state, Level level, Player player, InteractionHand hand, BlockHitResult hitVec) {
        final ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(ItemTags.SHOVELS)) {
            if (UniversalDist.getLevelExtension(player.level()).destroyLatexCover(level, hitVec.getBlockPos(), true, player)) {
                itemStack.hurtAndBreak(1, player, (p_43122_) -> {
                    p_43122_.broadcastBreakEvent(hand);
                });

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.use(state, level, player, hand, hitVec);
    }

    public static class DarkLatex extends SpreadingLatexType {
        private static final List<Supplier<? extends TransfurVariant<?>>> VARIANTS = Util.make(new ArrayList<>(), list -> {
            list.add(ChangedTransfurVariants.DARK_LATEX_WOLF_MALE);
            list.add(ChangedTransfurVariants.DARK_LATEX_WOLF_FEMALE);
        });

        @Override
        public ResourceLocation getLootTable() {
            return BuiltInLootTables.EMPTY;
        }

        @Override
        public void initializeClient(Consumer<IClientLatexTypeExtensions> consumer) {
            consumer.accept(new IClientLatexTypeExtensions() {
                private static final ResourceLocation DARK_LATEX_TEXTURE = Changed.modResource("block/dark_latex_block_top");

                @Override
                public ResourceLocation getTextureForFace(Direction face) {
                    return DARK_LATEX_TEXTURE;
                }

                @Override
                public Color3 getColor() {
                    return Color3.DARK;
                }
            });
        }

        @Override
        public Item getGooItem() {
            return ChangedItems.DARK_LATEX_GOO.get();
        }

        @Override
        public AbstractLatexBucket getBucketItem() {
            return ChangedItems.DARK_LATEX_BUCKET.get();
        }

        @Override
        public @Nullable Block getBlock() {
            return ChangedBlocks.DARK_LATEX_BLOCK.get();
        }

        @Override
        public @Nullable Block getWallSplotch() {
            return ChangedBlocks.DARK_LATEX_WALL_SPLOTCH.get();
        }

        @Override
        public @Nullable EntityType<?> getPupEntityType(RandomSource random) {
            return ChangedEntities.DARK_LATEX_WOLF_PUP.get();
        }

        @Override
        public @Nullable TransfurVariant<?> getTransfurVariant(TransfurCause cause, RandomSource random) {
            return cause == TransfurCause.LATEX_CONTAINER_FELL ?
                    ChangedTransfurVariants.DARK_LATEX_WOLF_PARTIAL.get() : Util.getRandom(VARIANTS, random).get();
        }

        @Override
        public boolean isHostileTo(LatexType otherType) {
            return super.isHostileTo(otherType) || otherType == ChangedLatexTypes.WHITE_LATEX.get();
        }

        @Override
        public boolean isFriendlyTo(LatexType otherType) {
            return super.isFriendlyTo(otherType) || otherType == this;
        }
    }

    public static class WhiteLatex extends SpreadingLatexType {
        private static final List<Supplier<? extends TransfurVariant<?>>> VARIANTS = Util.make(new ArrayList<>(), list -> {
            list.add(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF);
            list.add(ChangedTransfurVariants.LATEX_MUTANT_BLODDCELL_WOLF);
        });

        @Override
        public ResourceLocation getLootTable() {
            return BuiltInLootTables.EMPTY;
        }

        @Override
        public void initializeClient(Consumer<IClientLatexTypeExtensions> consumer) {
            consumer.accept(new IClientLatexTypeExtensions() {
                private static final ResourceLocation WHITE_LATEX_TEXTURE = Changed.modResource("block/white_latex_block");

                @Override
                public ResourceLocation getTextureForFace(Direction face) {
                    return WHITE_LATEX_TEXTURE;
                }

                @Override
                public Color3 getColor() {
                    return Color3.WHITE;
                }
            });
        }

        @Override
        public InteractionResult use(LatexCoverState state, Level level, Player player, InteractionHand hand, BlockHitResult hitVec) {
            if (player.getItemInHand(hand).isEmpty() && ProcessTransfur.getPlayerTransfurVariantSafe(player)
                    .map(variant -> variant.getLatexType() == ChangedLatexTypes.WHITE_LATEX.get())
                    .orElse(false)) {
                WhiteLatexTransportInterface.entityEnterLatex(player, hitVec.getBlockPos());
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            return super.use(state, level, player, hand, hitVec);
        }

        @Override
        public Item getGooItem() {
            return ChangedItems.WHITE_LATEX_GOO.get();
        }

        @Override
        public AbstractLatexBucket getBucketItem() {
            return ChangedItems.WHITE_LATEX_BUCKET.get();
        }

        @Override
        public @Nullable Block getBlock() {
            return ChangedBlocks.WHITE_LATEX_BLOCK.get();
        }

        @Override
        public @Nullable Block getWallSplotch() {
            return ChangedBlocks.WHITE_LATEX_WALL_SPLOTCH.get();
        }

        @Override
        public @Nullable EntityType<?> getPupEntityType(RandomSource random) {
            return ChangedEntities.PURE_WHITE_LATEX_WOLF_PUP.get();
        }

        @Override
        public @Nullable TransfurVariant<?> getTransfurVariant(TransfurCause cause, RandomSource random) {
            return Util.getRandom(VARIANTS, random).get();
        }

        @Override
        public boolean isHostileTo(LatexType otherType) {
            return super.isHostileTo(otherType) || otherType == ChangedLatexTypes.DARK_LATEX.get();
        }

        @Override
        public boolean isFriendlyTo(LatexType otherType) {
            return super.isFriendlyTo(otherType) || otherType == this;
        }
    }
}