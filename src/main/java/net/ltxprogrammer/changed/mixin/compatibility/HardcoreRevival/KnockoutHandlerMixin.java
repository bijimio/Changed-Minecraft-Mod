package net.ltxprogrammer.changed.mixin.compatibility.HardcoreRevival;

import net.blay09.mods.balm.api.event.LivingDamageEvent;
import net.blay09.mods.hardcorerevival.handler.KnockoutHandler;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KnockoutHandler.class, remap = false)
@RequiredMods("hardcorerevival")
public abstract class KnockoutHandlerMixin {
    @Inject(method = "onPlayerDamage", at = @At("HEAD"), cancellable = true)
    private static void maybeIgnoreEvent(LivingDamageEvent event, CallbackInfo callback) {
        if (event.getDamageSource().is(ChangedTags.DamageTypes.IS_TRANSFUR))
            callback.cancel();
    }
}
