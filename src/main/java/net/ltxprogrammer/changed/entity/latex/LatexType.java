package net.ltxprogrammer.changed.entity.latex;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedLootContextParamSets;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class LatexType {
    protected final StateDefinition<LatexType, LatexCoverState> coverStateDefinition;
    private LatexCoverState defaultCoverState;

    private Object renderProperties;

    protected LatexType() {
        this.coverStateDefinition = createStateDefinition();
        this.defaultCoverState = coverStateDefinition.any();

        this.initClient();
    }

    public String toString() {
        return "LatexType{" + ChangedRegistry.LATEX_TYPE.getKey(this) + "}";
    }

    protected void registerDefaultCoverState(LatexCoverState state) {
        this.defaultCoverState = state;
    }

    private StateDefinition<LatexType, LatexCoverState> createStateDefinition() {
        return Util.make(new StateDefinition.Builder<>(this), this::buildStateDefinition).create(LatexType::defaultCoverState, LatexCoverState::new);
    }

    protected void buildStateDefinition(StateDefinition.Builder<LatexType, LatexCoverState> builder) {

    }

    public StateDefinition<LatexType, LatexCoverState> getStateDefinition() {
        return this.coverStateDefinition;
    }

    public LatexCoverState defaultCoverState() {
        return defaultCoverState;
    }

    public LatexCoverState sourceCoverState() {
        return defaultCoverState();
    }

    public void updateIndirectNeighbourShapes(LatexCoverState state, LevelAccessor level, BlockPos blockPos, int flags, int timeToLive) {}

    public void onPlace(LatexCoverState state, Level level, BlockPos blockPos, LatexCoverState oldState, boolean flag) {}

    public void onRemove(LatexCoverState state, Level level, BlockPos blockPos, LatexCoverState oldState, boolean flag) {}

    public void animateTick(LatexCoverState state, Level level, BlockPos pos, RandomSource random) {}

    public abstract ResourceLocation getLootTable();

    public long getSeed(LatexCoverState state, BlockPos blockPos) {
        return Mth.getSeed(blockPos);
    }

    public static List<ItemStack> getDrops(LatexCoverState state, ServerLevel level, BlockPos blockPos) {
        LootParams.Builder builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);
        return state.getDrops(builder);
    }

    public static List<ItemStack> getDrops(LatexCoverState state, ServerLevel level, BlockPos blockPos, @Nullable Entity source, ItemStack tool) {
        LootParams.Builder builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, source).withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);
        return state.getDrops(builder);
    }

    public static void dropResources(LatexCoverState state, Level level, BlockPos blockPos) {
        if (level instanceof ServerLevel) {
            getDrops(state, (ServerLevel)level, blockPos).forEach((p_152406_) -> {
                Block.popResource(level, blockPos, p_152406_);
            });
            state.spawnAfterBreak((ServerLevel)level, blockPos, ItemStack.EMPTY, true);
        }

    }

    public static void dropResources(LatexCoverState state, LevelAccessor level, BlockPos blockPos) {
        if (level instanceof ServerLevel) {
            getDrops(state, (ServerLevel)level, blockPos).forEach((p_49859_) -> {
                Block.popResource((ServerLevel)level, blockPos, p_49859_);
            });
            state.spawnAfterBreak((ServerLevel)level, blockPos, ItemStack.EMPTY, true);
        }

    }

    public static void dropResources(LatexCoverState state, Level level, BlockPos blockPos, @Nullable Entity source, ItemStack tool) {
        dropResources(state, level, blockPos, source, tool, true);
    }

    public static void dropResources(LatexCoverState state, Level level, BlockPos blockPos, @Nullable Entity source, ItemStack tool, boolean dropXp) {
        if (level instanceof ServerLevel) {
            getDrops(state, (ServerLevel)level, blockPos, source, tool).forEach((p_49944_) -> {
                Block.popResource(level, blockPos, p_49944_);
            });
            state.spawnAfterBreak((ServerLevel)level, blockPos, tool, dropXp);
        }

    }

    private void initClient() {
        // Minecraft instance isn't available in datagen, so don't call initializeClient if in datagen
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT && !net.minecraftforge.fml.loading.FMLLoader.getLaunchHandler().isData()) {
            initializeClient(properties -> {
                if (properties == this)
                    throw new IllegalStateException("Don't extend IClientLatexTypeProperties in your latex type, use an anonymous class instead.");
                this.renderProperties = properties;
            });
        }
    }

    public LatexCoverState updateShape(LatexCoverState state, Direction direction, LatexCoverState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        return state;
    }

    public LatexCoverState updateInPlace(LatexCoverState state, BlockState oldState, BlockState newState, LevelAccessor level, BlockPos pos) {
        return state;
    }

    public VoxelShape getShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return Shapes.block();
    }

    public VoxelShape getCollisionShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getShape(state, level, blockPos, context);
    }

    public VoxelShape getVisualShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getCollisionShape(state, level, blockPos, context);
    }

    public VoxelShape getInteractionShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos) {
        return Shapes.empty();
    }

    public Object getRenderPropertiesInternal() {
        return renderProperties;
    }

    public void initializeClient(Consumer<IClientLatexTypeExtensions> consumer) {}

    public void randomTick(LatexCoverState state, ServerLevel level, BlockPos blockPos, RandomSource random) {}
    
    public void entityInside(LatexCoverState state, Level level, BlockPos blockPos, Entity entity) {}

    public void spawnAfterBreak(LatexCoverState state, ServerLevel level, BlockPos blockPos, ItemStack itemStack, boolean dropXp) {}
    
    public List<ItemStack> getDrops(LatexCoverState coverState, LootParams.Builder builder) {
        ResourceLocation resourcelocation = this.getLootTable();
        if (resourcelocation == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootParams lootparams = builder.withParameter(LatexCoverState.LOOT_CONTEXT_PARAM, coverState).create(ChangedLootContextParamSets.LATEX_COVER);
            ServerLevel serverlevel = lootparams.getLevel();
            LootTable loottable = serverlevel.getServer().getLootData().getLootTable(resourcelocation);
            return loottable.getRandomItems(lootparams);
        }
    }

    public InteractionResult use(LatexCoverState state, Level level, Player player, InteractionHand hand, BlockHitResult hitVec) {
        return InteractionResult.CONSUME;
    }

    public static class None extends LatexType {
        @Override
        public ResourceLocation getLootTable() {
            return BuiltInLootTables.EMPTY;
        }

        @Override
        public VoxelShape getShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
            return Shapes.empty();
        }

        @Override
        public InteractionResult use(LatexCoverState state, Level level, Player player, InteractionHand hand, BlockHitResult hitVec) {
            return InteractionResult.PASS;
        }
    }
}
