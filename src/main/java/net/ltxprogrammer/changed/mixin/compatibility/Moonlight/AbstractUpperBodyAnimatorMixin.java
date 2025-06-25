package net.ltxprogrammer.changed.mixin.compatibility.Moonlight;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.AbstractUpperBodyAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.mehvahdjukaar.moonlight.api.item.IThirdPersonAnimationProvider;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractUpperBodyAnimator.class, remap = false)
@RequiredMods("selene")
public abstract class AbstractUpperBodyAnimatorMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    @Unique
    public ItemStack getItemInHand(T entity, HumanoidArm arm) {
        return entity.getItemInHand(entity.getMainArm() == arm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    @Inject(method = "poseRightArmForItem", at = @At("HEAD"), cancellable = true, require = 0)
    public void poseRightArm(T entity, CallbackInfo ci) {
        HumanoidArm handSide = entity.getMainArm();
        ItemStack stack = getItemInHand(entity, HumanoidArm.RIGHT);
        if (stack.getItem() instanceof IThirdPersonAnimationProvider thirdPersonAnimationProvider) {
            if (thirdPersonAnimationProvider.poseRightArm(stack, this.core.entityModel, entity, handSide)) {
                this.core.applyPropertyModel(this.core.entityModel);
                ci.cancel();
            }
        }

        //cancel off hand animation if two handed so two handed animation always happens last
        if (getItemInHand(entity, HumanoidArm.LEFT).getItem() instanceof IThirdPersonAnimationProvider thirdPersonAnimationProvider &&
                thirdPersonAnimationProvider.isTwoHanded())
            ci.cancel();
    }

    @Inject(method = "poseLeftArmForItem", at = @At(value = "HEAD"), cancellable = true, require = 0)
    public void poseLeftArm(T entity, CallbackInfo ci) {
        HumanoidArm handSide = entity.getMainArm();
        ItemStack stack = getItemInHand(entity, HumanoidArm.LEFT);
        if (stack.getItem() instanceof IThirdPersonAnimationProvider thirdPersonAnimationProvider) {
            if (thirdPersonAnimationProvider.poseLeftArm(stack, this.core.entityModel, entity, handSide)) {
                this.core.applyPropertyModel(this.core.entityModel);
                ci.cancel();
            }
        }

        //cancel off hand animation if two handed so two handed animation always happens last
        if (getItemInHand(entity, HumanoidArm.RIGHT).getItem() instanceof IThirdPersonAnimationProvider thirdPersonAnimationProvider &&
                thirdPersonAnimationProvider.isTwoHanded())
            ci.cancel();
    }
}
