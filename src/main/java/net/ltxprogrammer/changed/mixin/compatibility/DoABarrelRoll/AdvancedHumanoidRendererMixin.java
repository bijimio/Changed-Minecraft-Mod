package net.ltxprogrammer.changed.mixin.compatibility.DoABarrelRoll;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.math.Axis;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.config.ModConfig;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AdvancedHumanoidRenderer.class)
@RequiredMods("do_a_barrel_roll")
public abstract class AdvancedHumanoidRendererMixin {
    @ModifyArg(
            method = {"setupRotations(Lnet/ltxprogrammer/changed/entity/ChangedEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    ordinal = 1
            ),
            index = 0
    )
    private Quaternionf doABarrelRoll$modifyRoll(Quaternionf original, @Local(argsOnly = true) ChangedEntity entity, @Local(argsOnly = true,ordinal = 2) float tickDelta) {
        if (ModConfig.INSTANCE.getModEnabled() && entity.maybeGetUnderlying() instanceof RollEntity rollEntity && rollEntity.doABarrelRoll$isRolling()) {
            float roll = rollEntity.doABarrelRoll$getRoll(tickDelta);
            return Axis.YP.rotationDegrees(roll);
        } else {
            return original;
        }
    }
}
