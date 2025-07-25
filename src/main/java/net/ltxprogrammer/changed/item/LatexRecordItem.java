package net.ltxprogrammer.changed.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class LatexRecordItem extends LoopedRecordItem {
    private static final Component NAME = Component.translatable("item.changed.latex_music_disc");

    public LatexRecordItem(int comparatorValue, Supplier<SoundEvent> soundSupplier, Properties builder, int lengthInTicks) {
        super(comparatorValue, soundSupplier, builder, lengthInTicks);
    }

    @Override
    public Component getName(ItemStack stack) {
        return NAME;
    }
}
