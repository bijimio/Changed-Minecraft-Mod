package net.ltxprogrammer.changed.mixin.compatibility.PresenceFootsteps;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import eu.ha3.presencefootsteps.sound.PFIsolator;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = SoundEngine.class, remap = false)
@RequiredMods("presencefootsteps")
public abstract class SoundEngineMixin {
    @WrapMethod(method = "getLocomotion")
    public Locomotion getLocomotion(LivingEntity entity, Operation<Locomotion> original) {
        return original.call(EntityUtil.maybeGetOverlaying(entity));
    }
}
