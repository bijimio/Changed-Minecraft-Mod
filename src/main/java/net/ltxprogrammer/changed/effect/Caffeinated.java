package net.ltxprogrammer.changed.effect;

import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class Caffeinated extends MobEffect {
    public Caffeinated() {
        super(MobEffectCategory.NEUTRAL, 0x6F4E37);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);

        // Should never be null, but compat may mess with it
        final int duration = Optional.ofNullable(entity.getEffect(this)).map(MobEffectInstance::getDuration).orElse(0);
        int benefitAmplifier = Mth.clamp(amplifier, 0, 2);
        if (entity instanceof ServerPlayer serverPlayer) { // Effectively slow TIME_SINCE_REST
            final var statRef = Stats.CUSTOM.get(Stats.TIME_SINCE_REST);
            final var stats = serverPlayer.getStats();
            final var timeSinceRest = stats.getValue(statRef);
            if (timeSinceRest % (3 - benefitAmplifier) == 0)
                stats.setValue(serverPlayer, statRef, timeSinceRest - 1);
        }

        int downsideAmplifier = Mth.clamp(amplifier, 3, 5) - 3;
        if (amplifier > 2 && duration % (3 - downsideAmplifier) * 40 == 0) {
            entity.hurt(ChangedDamageSources.HEART_ATTACK.source(entity.level().registryAccess()), 1);
        }
    }

    public void stackEffect(LivingEntity user, int tickDuration, int upgradeTickThreshold) {
        final var existing = user.getEffect(this);
        if (existing != null) {
            if (existing.getDuration() > upgradeTickThreshold)
                user.addEffect(new MobEffectInstance(this, tickDuration, existing.getAmplifier() + 1));
            else
                user.addEffect(new MobEffectInstance(this, tickDuration, existing.getAmplifier()));
        }

        user.addEffect(new MobEffectInstance(this, tickDuration));
    }
}
