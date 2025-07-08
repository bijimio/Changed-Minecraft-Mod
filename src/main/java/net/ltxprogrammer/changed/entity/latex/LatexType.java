package net.ltxprogrammer.changed.entity.latex;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public abstract class LatexType {
    protected final StateDefinition<LatexType, LatexCoverState> coverStateDefinition;
    private LatexCoverState defaultCoverState;

    protected LatexType() {
        this.coverStateDefinition = createStateDefinition();
        this.defaultCoverState = coverStateDefinition.any();
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
    }
}
