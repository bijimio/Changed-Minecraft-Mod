package net.ltxprogrammer.changed.mixin.compatibility.CarryOn;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.Constants;
import tschipp.carryon.common.carry.PickupHandler;

import java.util.function.Function;

@Mixin(value = PickupHandler.class, remap = false)
@RequiredMods("carryon")
public class PickupHandlerMixin {
    @Inject(method = "tryPickupEntity", at = @At("HEAD"), cancellable = true)
    private static void handleChangedEntities(ServerPlayer player, Entity entity, Function<Entity, Boolean> pickupCallback, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof ChangedEntity changedEntity)) return;
        if (Constants.COMMON_CONFIG.settings.pickupHostileMobs) return;

        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            if (variant.getLatexType() != changedEntity.getLatexType())
                cir.setReturnValue(false);
        }, () -> cir.setReturnValue(false));
    }
}
