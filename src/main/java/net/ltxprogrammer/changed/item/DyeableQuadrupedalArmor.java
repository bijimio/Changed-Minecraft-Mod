package net.ltxprogrammer.changed.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;

public class DyeableQuadrupedalArmor extends QuadrupedalArmor implements DyeableLeatherItem {
    public DyeableQuadrupedalArmor(ArmorMaterial material, ArmorItem.Type slot) {
        super(material, slot);
    }
}
