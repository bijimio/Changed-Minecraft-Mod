package net.ltxprogrammer.changed.mixin.compatibility.Create;

import com.simibubi.create.foundation.item.CustomArmPoseItem;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AdvancedHumanoidRenderer.class, remap = false)
public abstract class AdvancedHumanoidRendererMixin {
    @Inject(
            method = {"getArmPose"},
            at = {@At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/ltxprogrammer/changed/entity/ChangedEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"
            )},
            cancellable = true
    )
    private static void create$onGetArmPose(ChangedEntity entity, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        ItemStack stack = entity.getItemInHand(hand);
        Item heldItem = stack.getItem();
        if (heldItem instanceof CustomArmPoseItem armPoseProvider && entity.getUnderlyingPlayer() != null) {
            HumanoidModel.ArmPose pose = armPoseProvider.getArmPose(stack, (AbstractClientPlayer) entity.getUnderlyingPlayer(), hand);
            if (pose != null) {
                cir.setReturnValue(pose);
            }
        }

    }
}
