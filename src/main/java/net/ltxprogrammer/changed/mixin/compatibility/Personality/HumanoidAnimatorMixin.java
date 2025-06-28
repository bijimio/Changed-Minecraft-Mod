package net.ltxprogrammer.changed.mixin.compatibility.Personality;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.personality.core.Personality;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = HumanoidAnimator.class, remap = false)
@RequiredMods("personality")
public abstract class HumanoidAnimatorMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> {
    @WrapOperation(method = "setupVariables", at = @At(value = "INVOKE", target = "Lnet/ltxprogrammer/changed/client/renderer/model/AdvancedHumanoidModel;shouldModelSit(Lnet/ltxprogrammer/changed/entity/ChangedEntity;)Z"))
    public boolean allowPersonalitySit(M instance, T entity, Operation<Boolean> original) {
        if (entity.maybeGetUnderlying() instanceof Player player) {
            if (Personality.SYNCED_SITTING_PLAYERS.contains(player.getUUID()))
                return true;
        }

        return original.call(instance, entity);
    }
}
