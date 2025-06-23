package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.ItemUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public interface LatexFusingItem extends ExtendedItemProperties {
    TransfurVariant<?> getFusionVariant(TransfurVariant<?> currentVariant, LivingEntity livingEntity, ItemStack itemStack);

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    class Event {
        @SubscribeEvent
        static void onVariantAssigned(ProcessTransfur.EntityVariantAssigned event) {
            if (event.isRedundant())
                return;
            if (event.variant == null)
                return;

            final var oldVariant = event.variant;

            ItemUtil.getWearingItems(event.livingEntity).forEach(slottedItem -> {
                if (slottedItem.itemStack().getItem() instanceof LatexFusingItem fusingItem) {
                    var newVariant = fusingItem.getFusionVariant(event.variant, event.livingEntity, slottedItem.itemStack());
                    if (newVariant == null) {
                        return;
                    }
                    slottedItem.itemStack().shrink(1);
                    event.variant = newVariant;
                }
            });

            if (event.variant != oldVariant) {
                ChangedSounds.broadcastSound(event.livingEntity, event.variant.sound, 1, 1);
            }
        }
    }
}
