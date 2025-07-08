package net.ltxprogrammer.changed.entity.latex;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

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

    public void onPlace(LatexCoverState state, Level level, BlockPos blockPos, LatexCoverState oldState, boolean flag) {

    }

    public void onRemove(LatexCoverState state, Level level, BlockPos blockPos, LatexCoverState oldState, boolean flag) {

    }

    public void animateTick(LatexCoverState state, Level level, BlockPos pos, RandomSource random) {

    }

    public abstract ResourceLocation getLootTable();

    public long getSeed(LatexCoverState state, BlockPos blockPos) {
        return Mth.getSeed(blockPos);
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

    public Object getRenderPropertiesInternal() {
        return renderProperties;
    }

    public void initializeClient(Consumer<IClientLatexTypeExtensions> consumer) {}

    public static class None extends LatexType {
        @Override
        public ResourceLocation getLootTable() {
            return BuiltInLootTables.EMPTY;
        }
    }

    public static class DarkLatex extends LatexType {
        public DarkLatex() {
            super();
            this.registerDefaultCoverState(this.coverStateDefinition.any().setValue(LatexCoverState.SATURATION, 0));
        }

        @Override
        protected void buildStateDefinition(StateDefinition.Builder<LatexType, LatexCoverState> builder) {
            builder.add(LatexCoverState.SATURATION);
        }

        @Override
        public ResourceLocation getLootTable() {
            return BuiltInLootTables.EMPTY;
        }

        @Override
        public void initializeClient(Consumer<IClientLatexTypeExtensions> consumer) {
            consumer.accept(new IClientLatexTypeExtensions() {
                private static final ResourceLocation DARK_LATEX_TEXTURE = Changed.modResource("block/dark_latex_block_top");

                @Override
                public ResourceLocation getTextureForFace(LatexCoverState state, Direction face) {
                    return DARK_LATEX_TEXTURE;
                }
            });
        }
    }

    public static class WhiteLatex extends LatexType {
        public WhiteLatex() {
            super();
            this.registerDefaultCoverState(this.coverStateDefinition.any().setValue(LatexCoverState.SATURATION, 0));
        }

        @Override
        protected void buildStateDefinition(StateDefinition.Builder<LatexType, LatexCoverState> builder) {
            builder.add(LatexCoverState.SATURATION);
        }

        @Override
        public ResourceLocation getLootTable() {
            return BuiltInLootTables.EMPTY;
        }

        @Override
        public void initializeClient(Consumer<IClientLatexTypeExtensions> consumer) {
            consumer.accept(new IClientLatexTypeExtensions() {
                private static final ResourceLocation WHITE_LATEX_TEXTURE = Changed.modResource("block/white_latex_block");

                @Override
                public ResourceLocation getTextureForFace(LatexCoverState state, Direction face) {
                    return WHITE_LATEX_TEXTURE;
                }
            });
        }
    }
}
