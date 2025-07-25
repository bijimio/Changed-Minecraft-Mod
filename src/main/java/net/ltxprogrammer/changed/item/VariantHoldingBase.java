package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.Predicate;

public interface VariantHoldingBase {
    Item getOriginalItem();

    void fillItemList(Predicate<TransfurVariant<?>> predicate, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output);
}
