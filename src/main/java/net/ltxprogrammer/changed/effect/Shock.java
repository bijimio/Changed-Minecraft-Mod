package net.ltxprogrammer.changed.effect;

import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class Shock extends MobEffect {
    public static void setNoControlTicks(LivingEntity entity, int ticks) {
        if (entity instanceof LivingEntityDataExtension ext)
            ext.setNoControlTicks(ticks);
    }

    public static int getNoControlTicks(LivingEntity entity) {
        if (entity instanceof LivingEntityDataExtension ext)
            return ext.getNoControlTicks();
        return 0;
    }

    public Shock() {
        super(MobEffectCategory.HARMFUL, 14688288);
    }

    @Override
    public String getDescriptionId() {
        return "effect.changed.shock";
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        super.applyEffectTick(livingEntity, amplifier);
        setNoControlTicks(livingEntity, 5);
    }
}
