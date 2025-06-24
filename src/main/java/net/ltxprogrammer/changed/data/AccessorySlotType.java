package net.ltxprogrammer.changed.data;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.item.AccessoryItem;
import net.ltxprogrammer.changed.item.ExtendedItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AccessorySlotType {
    private TagKey<Item> itemTagKey = null;
    private ResourceLocation noItemIcon = null;
    private final EquipmentSlot equivalentSlot;

    public AccessorySlotType(EquipmentSlot equivalentSlot) {
        this.equivalentSlot = equivalentSlot;
    }

    public TagKey<Item> getItemTag() {
        if (itemTagKey != null)
            return itemTagKey;

        itemTagKey = TagKey.create(Registries.ITEM, ChangedRegistry.ACCESSORY_SLOTS.getKey(this));
        return itemTagKey;
    }

    public boolean canHoldItem(ItemStack itemStack, LivingEntity wearer) {
        if (!itemStack.is(this.getItemTag()))
            return false;
        if (itemStack.getItem() instanceof AccessoryItem accessoryItem && !accessoryItem.allowedInSlot(itemStack, wearer, this))
            return false;
        return true;
    }

    public ResourceLocation getNoItemIcon() {
        if (noItemIcon != null)
            return noItemIcon;

        ResourceLocation id = ChangedRegistry.ACCESSORY_SLOTS.getKey(this);
        noItemIcon = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "items/empty_" + id.getPath() + "_slot");
        return noItemIcon;
    }

    public EquipmentSlot getEquivalentSlot() {
        return equivalentSlot;
    }

    public void handleEvent(LivingEntity wearer, ItemStack itemStack, int event) {
        if (event == 1 && itemStack.getItem() instanceof AccessoryItem accessoryItem)
            accessoryItem.accessoryBreak(new AccessorySlotContext<>(wearer, this, itemStack));
        if (event == 2 && itemStack.getItem() instanceof AccessoryItem accessoryItem)
            accessoryItem.accessoryInteract(new AccessorySlotContext<>(wearer, this, itemStack));
    }
}
