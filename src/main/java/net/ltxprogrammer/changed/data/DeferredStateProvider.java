package net.ltxprogrammer.changed.data;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedBlockStateProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Random;
import java.util.function.Supplier;

public class DeferredStateProvider extends BlockStateProvider {
    public static final Codec<DeferredStateProvider> CODEC = ResourceLocation.CODEC.fieldOf("state").xmap(
            (stateStr) -> new DeferredStateProvider(stateStr, () -> ForgeRegistries.BLOCKS.getValue(stateStr)),
            (p_68804_) -> p_68804_.id).codec();
    private final ResourceLocation id;
    private final Supplier<? extends Block> state;

    public DeferredStateProvider(ResourceLocation id, Supplier<? extends Block> p_68801_) {
        this.id = id;
        this.state = p_68801_;
    }

    protected BlockStateProviderType<?> type() {
        return ChangedBlockStateProviders.DEFERRED_STATE_PROVIDER.get();
    }

    public BlockState getState(RandomSource p_68806_, BlockPos p_68807_) {
        return this.state.get().defaultBlockState();
    }

    public static BlockStateProvider of(ResourceLocation blockLocation) {
        return new DeferredStateProvider(blockLocation, () -> ForgeRegistries.BLOCKS.getValue(blockLocation));
    }

    public static BlockStateProvider of(RegistryObject<? extends Block> blockSupplier) {
        return new DeferredStateProvider(blockSupplier.getId(), blockSupplier);
    }
}
