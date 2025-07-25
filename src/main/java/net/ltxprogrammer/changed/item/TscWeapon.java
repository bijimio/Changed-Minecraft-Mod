package net.ltxprogrammer.changed.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.ltxprogrammer.changed.entity.animation.StasisAnimationParameters;
import net.ltxprogrammer.changed.entity.animation.StunAnimationParameters;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;

public abstract class TscWeapon extends Item implements Vanishable {
    private final Cacheable<Multimap<Attribute, AttributeModifier>> defaultModifiers;

    public TscWeapon(Properties properties) {
        super(properties);
        this.defaultModifiers = new Cacheable<>() {
            @Override
            protected Multimap<Attribute, AttributeModifier> initialGet() {
                ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage(), AttributeModifier.Operation.ADDITION));
                builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed(), AttributeModifier.Operation.ADDITION));
                builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier("Weapon modifier", attackRange(), AttributeModifier.Operation.MULTIPLY_BASE));
                return builder.build();
            }
        };
    }

    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        return !player.isCreative();
    }

    public int attackStun() { return 0; }
    public abstract double attackDamage();
    public abstract double attackSpeed();
    public double attackRange() {
        return 1.0;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers.get() : super.getDefaultAttributeModifiers(slot);
    }

    public static void sweepWeapon(LivingEntity source, double attackRange) {
        double d0 = (double)(-Mth.sin(source.getYRot() * ((float)Math.PI / 180F))) * attackRange;
        double d1 = (double)Mth.cos(source.getYRot() * ((float)Math.PI / 180F)) * attackRange;
        if (source.level() instanceof ServerLevel serverLevel)
            serverLevel.sendParticles(ChangedParticles.TSC_SWEEP_ATTACK.get(),
                    source.getX() + d0, source.getY(0.5D),
                    source.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
    }

    public static void applyShock(LivingEntity enemy, int attackStun) {
        ChangedSounds.broadcastSound(enemy, ChangedSounds.PARALYZE1, 1, 1);
        enemy.addEffect(new MobEffectInstance(ChangedEffects.SHOCK.get(), attackStun, 0, false, false, true));
        ChangedAnimationEvents.broadcastEntityAnimation(enemy, ChangedAnimationEvents.SHOCK_STUN.get(), StunAnimationParameters.INSTANCE);
    }
}
