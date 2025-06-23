package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NitrileGloves extends ClothingItem implements Gloves {
    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return Changed.modResourceStr("textures/models/nitrile_gloves_" + Mth.clamp(stack.getDamageValue() - 1, 0, 4) + ".png");
    }
}
