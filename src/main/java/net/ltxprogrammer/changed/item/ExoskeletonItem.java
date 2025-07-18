package net.ltxprogrammer.changed.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.entity.robot.AbstractRobot;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.UUID;
import java.util.function.Supplier;

public class ExoskeletonItem<T extends AbstractRobot> extends PlaceableEntity<T> implements AccessoryItem, ExtendedItemProperties {
    protected static final UUID MECH_ATTACK_DAMAGE_UUID = UUID.fromString("bfed474a-d281-4102-9b5f-cd785026b1d5");
    protected static final UUID MECH_ATTACK_SPEED_UUID = UUID.fromString("8c461d33-f151-4c32-a2d7-e76593ce5a35");
    protected static final UUID MECH_MOVEMENT_SPEED_UUID = UUID.fromString("97790787-d3fe-47bd-90eb-86c63164f131");
    protected static final UUID MECH_ARMOR_UUID = UUID.fromString("40845805-4dde-4c45-8eb7-defe001f9035");
    protected static final UUID MECH_KNOCKBACK_UUID = UUID.fromString("494836c5-32c2-4b38-9ae3-261d295389e3");

    private static final Cacheable<Multimap<Attribute, AttributeModifier>> DEFAULT_MODIFIERS = Cacheable.of(() -> {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(MECH_ATTACK_DAMAGE_UUID, "Weapon modifier", 2, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(MECH_ATTACK_SPEED_UUID, "Weapon modifier", -1, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MECH_MOVEMENT_SPEED_UUID, "Movement modifier", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
        builder.put(Attributes.ARMOR, new AttributeModifier(MECH_ARMOR_UUID, "Armor modifier", 20, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(MECH_KNOCKBACK_UUID, "Armor modifier", 2, AttributeModifier.Operation.MULTIPLY_BASE));
        return builder.build();
    });

    public ExoskeletonItem(Properties builder, Supplier<EntityType<T>> entityType) {
        super(builder, entityType);
        DispenserBlock.registerBehavior(this, AccessoryItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, AccessorySlotType slot) {
        boolean isTransfurring = ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(wearer)).map(variant -> variant.transfurProgression)
                .map(progress -> progress < 1f).orElse(false);

        return !isTransfurring && EntityUtil.maybeGetOverlaying(wearer).getType().is(ChangedTags.EntityTypes.CAN_WEAR_EXOSKELETON);
    }

    // TODO: extend functionality to allow custom values
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        return DEFAULT_MODIFIERS.get();
    }

    public float getJumpStrengthMultiplier(ItemStack stack) {
        return 1.25f;
    }

    public float getFallDamageMultiplier(ItemStack stack) {
        return 0.4f;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.CHEST) {
            return getAttributeModifiers(stack);
        }

        return ImmutableMultimap.of();
    }
}
