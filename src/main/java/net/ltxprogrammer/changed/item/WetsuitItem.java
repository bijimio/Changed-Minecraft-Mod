package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class WetsuitItem extends ClothingItem {
    @Override
    public boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, AccessorySlotType slot) {
        return super.allowedInSlot(itemStack, wearer, slot) &&
                AccessorySlots.isSlotAvailable(wearer, ChangedAccessorySlots.LEGS.get());
    }

    @Override
    public boolean shouldDisableSlot(AccessorySlotContext<?> slotContext, AccessorySlotType otherSlot) {
        return super.shouldDisableSlot(slotContext, otherSlot) || otherSlot == ChangedAccessorySlots.LEGS.get();
    }
}
