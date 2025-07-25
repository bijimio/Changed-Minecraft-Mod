package net.ltxprogrammer.changed.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class LabSlabBlock extends SlabBlock {
    public LabSlabBlock(Supplier<BlockState> type, Properties properties) {
        super(properties.requiresCorrectToolForDrops());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDrops(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, state.getValue(TYPE) == SlabType.DOUBLE ? 2 : 1));
    }
}
