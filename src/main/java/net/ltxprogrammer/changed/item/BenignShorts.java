package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

public class BenignShorts extends ClothingItem implements Shorts {
    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return Changed.modResourceStr("textures/models/benign_shorts_" + Mth.clamp(stack.getDamageValue() - 1, 0, 4) + ".png");
    }

    public static TransfurVariant<?> getBenignTransfurVariant(LivingEntity entity) {
        if (entity.isInWater()) {
            // TODO: return ChangedTransfurVariants.BENIGN_ORCA.get()
        }

        if (entity.getRandom().nextBoolean()) {
            // TODO: return ChangedTransfurVariants.BENIGN_DRAGON.get()
        }

        return ChangedTransfurVariants.LATEX_BENIGN_WOLF.get();
    }
}
