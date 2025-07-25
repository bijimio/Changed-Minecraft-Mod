package net.ltxprogrammer.changed.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;

public class DyeableAbdomenArmor extends AbdomenArmor implements DyeableLeatherItem {
    public DyeableAbdomenArmor(ArmorMaterial material, ArmorItem.Type slot) {
        super(material, slot);
    }
}
