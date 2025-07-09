package net.ltxprogrammer.changed.world;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class LatexCoverState extends StateHolder<LatexType, LatexCoverState> {
    protected static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};

    public static final LootContextParam<LatexCoverState> LOOT_CONTEXT_PARAM = new LootContextParam<>(Changed.modResource("latex_cover_state"));

    public static final Cacheable<Codec<LatexCoverState>> CODEC = Cacheable.of(() -> codec(ChangedRegistry.LATEX_TYPE.get().getCodec(), LatexType::defaultCoverState).stable());

    public LatexCoverState(LatexType type, ImmutableMap<Property<?>, Comparable<?>> properties, MapCodec<LatexCoverState> codec) {
        super(type, properties, codec);
    }

    public boolean isPresent() {
        return this.owner != ChangedLatexTypes.NONE.get();
    }

    public boolean isAir() {
        return this.owner == ChangedLatexTypes.NONE.get();
    }

    public LatexType getType() {
        return this.owner;
    }

    public static LatexCoverState getAt(LevelAccessor level, BlockPos blockPos) {
        return getAt(level.getChunk(blockPos).getSection(level.getSectionIndex(blockPos.getY())), blockPos);
    }

    public static LatexCoverState getAt(ChunkAccess chunk, BlockPos blockPos) {
        return getAt(chunk.getSection(chunk.getSectionIndex(blockPos.getY())), blockPos);
    }

    public static LatexCoverState getAt(LevelChunkSection section, BlockPos blockPos) {
        return getAt(section, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static LatexCoverState getAt(LevelChunkSection section, int x, int y, int z) {
        return ((LevelChunkSectionExtension)section)
                .getLatexCoverState(x & 15, y & 15, z & 15);
    }

    public static void markAndNotifyAt(Level level, BlockPos blockPos, @Nullable LevelChunk levelchunk, LatexCoverState oldState, LatexCoverState newState, int flags, int timeToLive) {
        LatexType block = newState.getType();
        LatexCoverState recordedState = getAt(level, blockPos);
        {
            {
                if (recordedState == newState) {
                    LevelExtension levelExtension = UniversalDist.getLevelExtension(level);

                    if (oldState != recordedState) {
                        levelExtension.setCoversDirty(level, blockPos, oldState, recordedState);
                    }

                    if ((flags & 2) != 0 && (!level.isClientSide || (flags & 4) == 0) && (level.isClientSide || levelchunk.getFullStatus() != null && levelchunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))) {
                        levelExtension.sendCoverUpdated(level, blockPos, oldState, newState, flags);
                    }

                    if ((flags & 1) != 0) {
                        levelExtension.coverUpdated(level, blockPos, oldState.getType());
                    }

                    if ((flags & 16) == 0 && timeToLive > 0) {
                        int nextFlags = flags & -34;
                        oldState.updateIndirectNeighbourShapes(level, blockPos, nextFlags, timeToLive - 1);
                        newState.updateNeighbourShapes(level, blockPos, nextFlags, timeToLive - 1);
                        newState.updateIndirectNeighbourShapes(level, blockPos, nextFlags, timeToLive - 1);
                    }

                    levelExtension.onLatexCoverStateChange(level, blockPos, oldState, recordedState);
                    newState.onLatexCoverStateChange(level, blockPos, oldState);
                }
            }
        }
    }

    public static boolean setAt(LevelWriter level, BlockPos blockPos, LatexCoverState state, int flags, int timeToLive) {
        if (level instanceof Level casted)
            return setAt(casted, blockPos, state, flags, timeToLive);
        return false;
    }

    public static boolean setAt(Level level, BlockPos blockPos, LatexCoverState state, int flags, int timeToLive) {
        if (level.isOutsideBuildHeight(blockPos)) {
            return false;
        } else if (!level.isClientSide && level.isDebug()) {
            return false;
        } else {
            LevelChunk levelchunk = level.getChunkAt(blockPos);

            blockPos = blockPos.immutable(); // Forge - prevent mutable BlockPos leaks
            net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
            if (level.captureBlockSnapshots && !level.isClientSide) {
                blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.create(level.dimension(), level, blockPos, flags);
                level.capturedBlockSnapshots.add(blockSnapshot);
            }

            LatexCoverState oldState = setAt(levelchunk, blockPos, state, (flags & 64) != 0);
            if (oldState == null) {
                if (blockSnapshot != null) level.capturedBlockSnapshots.remove(blockSnapshot);
                return false;
            } else {
                if (blockSnapshot == null) { // Don't notify clients or update physics while capturing blockstates
                    markAndNotifyAt(level, blockPos, levelchunk, oldState, state, flags, timeToLive);
                }

                return true;
            }
        }
    }

    public static boolean setAt(Level level, BlockPos blockPos, LatexCoverState state, int flags) {
        return setAt(level, blockPos, state, flags, 512);
    }

    public static boolean setAtAndUpdate(Level level, BlockPos blockPos, LatexCoverState state) {
        return setAt(level, blockPos, state, 3);
    }

    public static LatexCoverState setAt(LevelChunk chunk, BlockPos blockPos, LatexCoverState state, boolean unknown) {
        int i = blockPos.getY();
        LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(i));
        boolean hasOnlyAir = section.hasOnlyAir();
        if (hasOnlyAir && state.isPresent()) {
            return null;
        } else {
            int j = blockPos.getX() & 15;
            int k = i & 15;
            int l = blockPos.getZ() & 15;
            LatexCoverState oldState = setAt(section, j, k, l, state);
            if (oldState == state) {
                return null;
            } else {
                boolean flag1 = section.hasOnlyAir();
                if (hasOnlyAir != flag1) {
                    chunk.getLevel().getChunkSource().getLightEngine().updateSectionStatus(blockPos, flag1);
                }

                if (!chunk.getLevel().isClientSide) {
                    oldState.onRemove(chunk.getLevel(), blockPos, state, unknown);
                }

                if (!getAt(section, j, k, l).is(state.getType())) {
                    return null;
                } else {
                    if (!chunk.getLevel().isClientSide && !chunk.getLevel().captureBlockSnapshots) {
                        state.onPlace(chunk.getLevel(), blockPos, oldState, unknown);
                    }

                    chunk.setUnsaved(true);
                    return oldState;
                }
            }
        }
    }

    public static void setServerVerifiedAt(Level level, BlockPos blockPos, LatexCoverState latexCoverState, int flags) {
        if (level.isClientSide)
            setAt(level, blockPos, latexCoverState, flags);
    }

    private boolean is(LatexType type) {
        return getType() == type;
    }

    public LatexCoverState asState() {
        return this;
    }

    public final void updateNeighbourShapes(LevelAccessor level, BlockPos p_60703_, int flags) {
        this.updateNeighbourShapes(level, p_60703_, flags, 512);
    }

    static void updateOrDestroy(LatexCoverState prevState, LatexCoverState nextState, LevelAccessor level, BlockPos blockPos, int flags, int timeToLive) {
        if (nextState != prevState) {
            if (nextState.isAir()) {
                if (!level.isClientSide()) {
                    UniversalDist.getLevelExtension(level).destroyLatexCover(level, blockPos, (flags & 32) == 0, (Entity)null, timeToLive);
                }
            } else {
                setAt(level, blockPos, nextState, flags & -33, timeToLive);
            }
        }

    }

    public static void executeShapeUpdate(LevelAccessor level, Direction direction, LatexCoverState neighborState, BlockPos blockPos, BlockPos neighborPos, int flags, int timeToLive) {
        LatexCoverState prevState = getAt(level, blockPos);
        LatexCoverState nextState = prevState.updateShape(direction, neighborState, level, blockPos, neighborPos);
        LatexCoverState.updateOrDestroy(prevState, nextState, level, blockPos, flags, timeToLive);
    }

    public static void executeInPlaceUpdate(LevelAccessor level, BlockState oldState, BlockState newState, BlockPos blockPos, int flags, int timeToLive) {
        LatexCoverState prevState = getAt(level, blockPos);
        LatexCoverState nextState = prevState.updateInPlace(oldState, newState, level, blockPos);
        LatexCoverState.updateOrDestroy(prevState, nextState, level, blockPos, flags, timeToLive);
    }

    public final void updateNeighbourShapes(LevelAccessor level, BlockPos blockPos, int flags, int timeToLive) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(Direction direction : UPDATE_SHAPE_ORDER) {
            blockpos$mutableblockpos.setWithOffset(blockPos, direction);
            LatexCoverState.executeShapeUpdate(level, direction.getOpposite(), this.asState(), blockpos$mutableblockpos, blockPos, flags, timeToLive - 1);
        }

    }

    public final void updateIndirectNeighbourShapes(LevelAccessor level, BlockPos blockPos, int flags) {
        this.updateIndirectNeighbourShapes(level, blockPos, flags, 512);
    }

    public void updateIndirectNeighbourShapes(LevelAccessor level, BlockPos blockPos, int flags, int timeToLive) {
        this.getType().updateIndirectNeighbourShapes(this.asState(), level, blockPos, flags, timeToLive);
    }

    public void onPlace(Level level, BlockPos blockPos, LatexCoverState oldState, boolean flag) {
        this.getType().onPlace(this.asState(), level, blockPos, oldState, flag);
    }

    public void onRemove(Level level, BlockPos blockPos, LatexCoverState oldState, boolean flag) {
        this.getType().onRemove(this.asState(), level, blockPos, oldState, flag);
    }

    public void onLatexCoverStateChange(Level level, BlockPos blockPos, LatexCoverState oldState) {

    }

    public static LatexCoverState setAt(LevelChunkSection section, BlockPos blockPos, LatexCoverState state) {
        return setAt(section, blockPos.getX(), blockPos.getY(), blockPos.getZ(), state);
    }

    public static LatexCoverState setAt(LevelChunkSection section, int x, int y, int z, LatexCoverState state) {
        return ((LevelChunkSectionExtension)section)
                .setLatexCoverState(x & 15, y & 15, z & 15, state);
    }

    public void randomTick(ServerLevel level, BlockPos position, RandomSource random) {
        this.getType().randomTick(this.asState(), level, position, random);
    }

    public void entityInside(Level level, BlockPos blockPos, Entity entity) {
        this.getType().entityInside(this.asState(), level, blockPos, entity);
    }

    public void spawnAfterBreak(ServerLevel level, BlockPos blockPos, ItemStack itemStack, boolean dropXp) {
        this.getType().spawnAfterBreak(this.asState(), level, blockPos, itemStack, dropXp);
    }

    public List<ItemStack> getDrops(LootParams.Builder builder) {
        return this.getType().getDrops(this.asState(), builder);
    }

    public void animateTick(Level level, BlockPos pos, RandomSource random) {
        this.getType().animateTick(this.asState(), level, pos, random);
    }

    public boolean isRandomlyTicking() {
        return !isAir();
    }

    public long getSeed(BlockPos blockPos) {
        return this.getType().getSeed(this.asState(), blockPos);
    }

    public LatexCoverState updateShape(Direction direction, LatexCoverState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        return this.getType().updateShape(this.asState(), direction, neighborState, level, blockPos, neighborPos);
    }

    public LatexCoverState updateInPlace(BlockState oldState, BlockState newState, LevelAccessor level, BlockPos pos) {
        return this.getType().updateInPlace(this.asState(), oldState, newState, level, pos);
    }

    public void initCache() {
        /*this.fluidState = this.owner.getFluidState(this.asState());
        this.isRandomlyTicking = this.owner.isRandomlyTicking(this.asState());
        if (!this.getBlock().hasDynamicShape()) {
            this.cache = new BlockBehaviour.BlockStateBase.Cache(this.asState());
        }

        this.legacySolid = this.calculateSolid();*/
    }
}
