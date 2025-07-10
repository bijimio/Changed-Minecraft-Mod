package net.ltxprogrammer.changed.entity.latex;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.DarkLatexBlock;
import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.ltxprogrammer.changed.block.WhiteLatexBlock;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SpreadingLatexType extends LatexType {
    public static final IntegerProperty SATURATION = IntegerProperty.create("saturation", 0, 15);
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    public static final EnumMap<Direction, BooleanProperty> FACES = Util.make(new EnumMap<>(Direction.class), map -> {
        map.put(Direction.UP, UP);
        map.put(Direction.DOWN, DOWN);
        map.put(Direction.NORTH, NORTH);
        map.put(Direction.SOUTH, SOUTH);
        map.put(Direction.EAST, EAST);
        map.put(Direction.WEST, WEST);
    });

    private final Map<LatexCoverState, VoxelShape> cachedShapes;
    private final Map<LatexCoverState, VoxelShape> cachedShapesSwim;

    public SpreadingLatexType() {
        super();
        this.registerDefaultCoverState(this.coverStateDefinition.any().setValue(SATURATION, 0)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false));
        this.cachedShapes = Util.make(new HashMap<>(), map -> {
            this.coverStateDefinition.getPossibleStates().forEach(state -> {
                map.computeIfAbsent(getVisualState(state), this::computeShapeForState);
            });
        });
        this.cachedShapesSwim = Util.make(new HashMap<>(), map -> {
            this.coverStateDefinition.getPossibleStates().forEach(state -> {
                map.computeIfAbsent(getVisualState(state), this::computeSwimShapeForState);
            });
        });
    }

    // Used to more efficiently cache duplicate shapes
    protected LatexCoverState getVisualState(LatexCoverState state) {
        return state.setValue(SATURATION, 0);
    }

    protected VoxelShape computeShapeForState(LatexCoverState state) {
        return Shapes.or(
                state.getValue(UP) ? Block.box(0, 14, 0, 16, 16, 16) : Shapes.empty(),
                state.getValue(DOWN) ? Block.box(0, 0, 0, 16, 2, 16) : Shapes.empty(),
                state.getValue(NORTH) ? Block.box(0, 0, 0, 16, 16, 2) : Shapes.empty(),
                state.getValue(SOUTH) ? Block.box(0, 0, 14, 16, 16, 16) : Shapes.empty(),
                state.getValue(EAST) ? Block.box(14, 0, 0, 16, 16, 16) : Shapes.empty(),
                state.getValue(WEST) ? Block.box(0, 0, 0, 2, 16, 16) : Shapes.empty()
        );
    }

    protected VoxelShape computeSwimShapeForState(LatexCoverState state) {
        return Shapes.or(
                state.getValue(UP) ? Block.box(-2, 12, -2, 18, 18, 18) : Shapes.empty(),
                state.getValue(DOWN) ? Block.box(-2, -2, -2, 18, 4, 18) : Shapes.empty(),
                state.getValue(NORTH) ? Block.box(-2, -2, -2, 18, 18, 4) : Shapes.empty(),
                state.getValue(SOUTH) ? Block.box(-2, -2, 12, 18, 18, 18) : Shapes.empty(),
                state.getValue(EAST) ? Block.box(12, -2, -2, 18, 18, 18) : Shapes.empty(),
                state.getValue(WEST) ? Block.box(-2, -2, -2, 4, 18, 18) : Shapes.empty()
        );
    }

    @Override
    protected void buildStateDefinition(StateDefinition.Builder<LatexType, LatexCoverState> builder) {
        builder.add(SATURATION,
                UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    public void defaultCoverBehavior(CoveringBlockEvent event) {
        if (event.originalState.is(Blocks.GRASS))
            event.setPlannedState(Blocks.DEAD_BUSH.defaultBlockState());
        else if (event.originalState.is(BlockTags.SMALL_FLOWERS))
            event.setPlannedState(Blocks.DEAD_BUSH.defaultBlockState());
        else if (event.originalState.is(BlockTags.SAPLINGS))
            event.setPlannedState(Blocks.DEAD_BUSH.defaultBlockState());
        else if (event.originalState.is(Blocks.FERN))
            event.setPlannedState(Blocks.DEAD_BUSH.defaultBlockState());
    }

    public boolean canSpread(LatexCoverState state) {
        return state.getValue(SATURATION) < 15;
    }

    public boolean shouldDecay(LatexCoverState state, LevelReader level, BlockPos blockPos) {
        final var thisSaturation = state.getValue(SATURATION);
        if (level.getBlockState(blockPos).getBlock() instanceof LatexCoveringSource)
            return false;
        if (Arrays.stream(Direction.values()).map(FACES::get).noneMatch(state::getValue))
            return true;
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

    public LatexCoverState spreadState(LevelReader level, BlockPos blockPos, LatexCoverState state) {
        state = state.setValue(SATURATION, state.getValue(SATURATION) + 1);
        for (Direction direction : Direction.values()) {
            var face = FACES.get(direction);
            var checkPos = blockPos.relative(direction);
            state = state.setValue(face, level.getBlockState(checkPos).isFaceSturdy(level, checkPos, direction.getOpposite(), SupportType.FULL));
        }
        return state;
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

            var event = new CoveringBlockEvent(this,
                    checkState, checkState, this.spreadState(level, checkPos, state), checkPos, level);
            this.defaultCoverBehavior(event);
            if (Changed.postModEvent(event))
                return;

            level.setBlockAndUpdate(checkPos, event.getPlannedState());
            LatexCoverState.setAtAndUpdate(level, checkPos, event.plannedCoverState);

            event.getPostProcess().accept(level, checkPos);
        }
    }

    @Override
    public LatexCoverState updateShape(LatexCoverState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        return state.setValue(FACES.get(direction), level.getBlockState(neighborPos).isFaceSturdy(level, neighborPos, direction.getOpposite(), SupportType.FULL));
    }

    @Override
    public LatexCoverState updateShape(LatexCoverState state, Direction direction, LatexCoverState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        if (neighborState.getType() != this)
            return state;
        if (neighborState.getValue(SATURATION) < state.getValue(SATURATION))
            return this.spreadState(level, blockPos, neighborState);
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
        return cachedShapes.get(getVisualState(state));
    }

    @Override
    public boolean shouldResetFallDamage(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return super.shouldResetFallDamage(state, level, blockPos, context) ||
                context instanceof EntityCollisionContext entityCollisionContext &&
                LatexType.getEntityLatexType(entityCollisionContext.getEntity()) == this;
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

    @Override
    public Vec3 findClosestSurface(LatexCoverState state, Vec3 position, @Nullable Direction.Axis axis) {
        Vec3 normal = position.multiply(2, 2, 2).add(-1, -1, -1);

        Direction closest = null;
        double bestDot = Double.MIN_VALUE;

        for(Direction checkDir : Direction.values()) {
            if (!state.getValue(FACES.get(checkDir)))
                continue;

            double dotValue = normal.x * checkDir.getNormal().getX() + normal.y * checkDir.getNormal().getY() + normal.z * checkDir.getNormal().getZ();
            if (checkDir.getAxis() == axis)
                dotValue += 0.2; // Give preferred direction a boost
            if (dotValue > bestDot) {
                bestDot = dotValue;
                closest = checkDir;
            }
        }

        if (closest == null)
            return position;

        return new Vec3(
                Mth.lerp(Mth.abs(closest.getNormal().getX()), position.x, Mth.clamp(position.x + closest.getNormal().getX(), 0, 1)),
                Mth.lerp(Mth.abs(closest.getNormal().getY()), position.y, Mth.clamp(position.y + closest.getNormal().getY(), 0, 1)),
                Mth.lerp(Mth.abs(closest.getNormal().getZ()), position.z, Mth.clamp(position.z + closest.getNormal().getZ(), 0, 1))
        );
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

        @Override
        public void defaultCoverBehavior(CoveringBlockEvent event) {
            super.defaultCoverBehavior(event);
            if (event.originalState.is(Blocks.GRASS) || event.originalState.is(BlockTags.SMALL_FLOWERS) || event.originalState.is(Blocks.FERN) || event.originalState.is(BlockTags.SAPLINGS)) {
                event.setPlannedState(Util.getRandom(DarkLatexBlock.SMALL_CRYSTALS, event.level.getRandom()).get().defaultBlockState());
            }

            if (event.originalState.getProperties().contains(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
                    (event.originalState.is(Blocks.TALL_GRASS) || event.originalState.is(Blocks.LARGE_FERN) || event.originalState.is(BlockTags.TALL_FLOWERS))) {
                var crystal = Util.getRandom(DarkLatexBlock.CRYSTALS, event.level.getRandom()).get().defaultBlockState();
                switch (event.originalState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                    case UPPER -> {
                        event.setPlannedState(crystal.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), (level, where) -> {
                            level.setBlockAndUpdate(where.below(), crystal
                                    .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
                        });
                    }

                    case LOWER -> {
                        event.setPlannedState(crystal.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), (level, where) -> {
                            level.setBlockAndUpdate(where.above(), crystal
                                    .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                        });
                    }
                }
            }
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

        @Override
        public void defaultCoverBehavior(CoveringBlockEvent event) {
            super.defaultCoverBehavior(event);

            if (event.originalState.getProperties().contains(BlockStateProperties.DOUBLE_BLOCK_HALF) &&
                    (event.originalState.is(Blocks.TALL_GRASS) || event.originalState.is(Blocks.LARGE_FERN) || event.originalState.is(BlockTags.TALL_FLOWERS))) {
                var pillar = Util.getRandom(WhiteLatexBlock.PILLAR, event.level.getRandom()).get().defaultBlockState();
                switch (event.originalState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                    case UPPER -> {
                        event.setPlannedState(pillar.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), (level, where) -> {
                            level.setBlockAndUpdate(event.blockPos.below(), pillar
                                    .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
                        });
                    }

                    case LOWER -> {
                        event.setPlannedState(pillar.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), (level, where) -> {
                            level.setBlockAndUpdate(event.blockPos.above(), pillar
                                    .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                        });
                    }
                }
            }
        }
    }

    public static class CoveringBlockEvent extends Event {
        public final SpreadingLatexType latexType;
        public final BlockPos blockPos;
        public final BlockState originalState;
        public LatexCoverState plannedCoverState;
        public final LevelAccessor level;

        private BlockState plannedState;
        private BiConsumer<Level, BlockPos> postProcess;

        private static void defaultPostProcess(Level level, BlockPos blockPos) {

        }

        public CoveringBlockEvent(SpreadingLatexType latexType, BlockState originalState, BlockState coveredState, LatexCoverState coverState, BlockPos blockPos, LevelAccessor level) {
            this.latexType = latexType;
            this.blockPos = blockPos;
            this.originalState = originalState;
            this.level = level;
            plannedState = coveredState;
            plannedCoverState = coverState;
            postProcess = CoveringBlockEvent::defaultPostProcess;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }

        public void setPlannedState(BlockState blockState) {
            this.setPlannedState(blockState, CoveringBlockEvent::defaultPostProcess);
        }

        public void setPlannedState(BlockState blockState, BiConsumer<Level, BlockPos> postProcess) {
            this.plannedState = blockState;
            this.postProcess = postProcess;
        }

        public BlockState getPlannedState() {
            return plannedState;
        }

        public BiConsumer<Level, BlockPos> getPostProcess() {
            return postProcess;
        }
    }
}