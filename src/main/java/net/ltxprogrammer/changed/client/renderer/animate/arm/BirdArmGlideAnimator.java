package net.ltxprogrammer.changed.client.renderer.animate.arm;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class BirdArmGlideAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart leftArm;
    public final ModelPart rightArm;

    public BirdArmGlideAnimator(ModelPart leftArm, ModelPart rightArm) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.FALL_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA))
            return;

        InteractionHand leftInteractionArm = entity.getMainArm() == HumanoidArm.LEFT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        InteractionHand rightInteractionArm = entity.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

        boolean usingItem = entity.isUsingItem();
        boolean animateLeft = !usingItem || (entity.getUsedItemHand() != leftInteractionArm);
        boolean animateRight = !usingItem || (entity.getUsedItemHand() != rightInteractionArm);

        float fallFlyingTicks = (float)entity.getFallFlyingTicks();
        float fallFlyingAmount = Mth.clamp(fallFlyingTicks * fallFlyingTicks / 100.0F, 0.0F, 1.0F);

        if (animateLeft) {
            leftArm.xRot = HumanoidAnimator.rotlerpRad(fallFlyingAmount, leftArm.xRot, -90.0f * Mth.DEG_TO_RAD);
            leftArm.yRot = HumanoidAnimator.rotlerpRad(fallFlyingAmount, leftArm.yRot, -90.0f * Mth.DEG_TO_RAD);
            leftArm.zRot = HumanoidAnimator.rotlerpRad(fallFlyingAmount, leftArm.zRot, 0.0f);
        }

        if (animateRight) {
            rightArm.xRot = HumanoidAnimator.rotlerpRad(fallFlyingAmount, rightArm.xRot, -90.0f * Mth.DEG_TO_RAD);
            rightArm.yRot = HumanoidAnimator.rotlerpRad(fallFlyingAmount, rightArm.yRot, 90.0f * Mth.DEG_TO_RAD);
            rightArm.zRot = HumanoidAnimator.rotlerpRad(fallFlyingAmount, rightArm.zRot, 0.0f);
        }
    }
}
