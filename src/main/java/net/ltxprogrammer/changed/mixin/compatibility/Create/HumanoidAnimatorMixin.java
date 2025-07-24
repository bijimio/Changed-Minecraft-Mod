package net.ltxprogrammer.changed.mixin.compatibility.Create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.foundation.render.PlayerSkyhookRenderer;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(value = HumanoidAnimator.class, remap = false)
@RequiredMods("create")
public abstract class HumanoidAnimatorMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> {
    @Shadow @Final public M entityModel;

    @Shadow public abstract void applyPropertyModel(HumanoidModel<?> propertyModel);

    @Shadow public boolean crouching;
    @Shadow public float swimAmount;
    @Shadow public float flyAmount;
    @Shadow public float fallFlyingAmount;

    @WrapMethod(method = "setupAnim")
    private void create$beforeSetupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Operation<Void> original) {
        if (entity.getUnderlyingPlayer() instanceof AbstractClientPlayer player) {
            // Resets to initial pose, but included anyway
            PlayerSkyhookRenderer.beforeSetupAnim(player, this.entityModel.preparePropertyModel(entity));
            this.applyPropertyModel(this.entityModel);

            if (ChangedCompatibility.com_simibubi_create_foundation_render_PlayerSkyhookRenderer$hangingPlayers.getOr(Set.of()).contains(player.getUUID())) {
                limbSwing = 0.0f;
                limbSwingAmount = 0.0f;
                this.crouching = false;
                this.swimAmount = 0.0f;
                this.flyAmount = 0.0f;
                this.fallFlyingAmount = 0.0f;
            }
        }

        original.call(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (entity.getUnderlyingPlayer() instanceof AbstractClientPlayer player) {
            PlayerSkyhookRenderer.afterSetupAnim(player, this.entityModel.preparePropertyModel(entity));
            this.applyPropertyModel(this.entityModel);
        }
    }
}
