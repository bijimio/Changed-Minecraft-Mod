package net.ltxprogrammer.changed.data;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedBlockStateProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

import java.util.List;
import java.util.Random;

public class MixedStateProvider extends BlockStateProvider {
    public static final Codec<MixedStateProvider> CODEC = WeightedEntry.Wrapper.codec(BlockStateProvider.CODEC).listOf().xmap(MixedStateProvider::new, MixedStateProvider::unwrap);
    private final List<WeightedEntry.Wrapper<BlockStateProvider>> items;
    private final int totalWeight;

    public MixedStateProvider(List<WeightedEntry.Wrapper<BlockStateProvider>> items) {
        this.items = items;
        this.totalWeight = WeightedRandom.getTotalWeight(items);
    }

    public List<WeightedEntry.Wrapper<BlockStateProvider>> unwrap() {
        return this.items;
    }

    @Override
    protected BlockStateProviderType<?> type() {
        return ChangedBlockStateProviders.MIXED_STATE_PROVIDER.get();
    }

    @Override
    public BlockState getState(RandomSource random, BlockPos blockPos) {
        int i = random.nextInt(this.totalWeight);
        return WeightedRandom.getWeightedItem(this.items, i).get().getData().getState(random, blockPos);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ImmutableList.Builder<WeightedEntry.Wrapper<BlockStateProvider>> items = ImmutableList.builder();

        public Builder add(BlockStateProvider provider, int weight) {
            items.add(WeightedEntry.wrap(provider, weight));
            return this;
        }

        public MixedStateProvider build() {
            return new MixedStateProvider(items.build());
        }
    }
}
