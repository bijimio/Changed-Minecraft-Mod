package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import static net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingCreativeFlyAnimator.BODY_FLY_SCALE;
import static net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingCreativeFlyAnimator.WING_FLAP_RATE;

public class BirdUpperBodyCreativeFlyAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public BirdUpperBodyCreativeFlyAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.CREATIVE_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float flapAmount = Mth.cos(ageInTicks * WING_FLAP_RATE);
        flapAmount = Mth.map(flapAmount * flapAmount, 0.0f, 1.0f, -BODY_FLY_SCALE, BODY_FLY_SCALE);
        torso.y = Mth.lerp(core.flyAmount, torso.y, flapAmount - 1);
        torso.z += Mth.lerp(core.flyAmount, 0.0f, -1.0f);
        leftArm.y = Mth.lerp(core.flyAmount, leftArm.y, flapAmount + 1);
        leftArm.z += Mth.lerp(core.flyAmount, 0.0f, 1.0f);
        rightArm.y = Mth.lerp(core.flyAmount, rightArm.y, flapAmount + 1);
        rightArm.z += Mth.lerp(core.flyAmount, 0.0f, 1.0f);

        torso.xRot = Mth.lerp(core.flyAmount, torso.xRot, Mth.DEG_TO_RAD * 45.0f);
        leftArm.xRot += Mth.lerp(core.flyAmount, 0.0f, Mth.DEG_TO_RAD * 30.0f);
        rightArm.xRot += Mth.lerp(core.flyAmount, 0.0f, Mth.DEG_TO_RAD * 30.0f);

        InteractionHand leftInteractionArm = entity.getMainArm() == HumanoidArm.LEFT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        InteractionHand rightInteractionArm = entity.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

        boolean usingItem = entity.isUsingItem();
        boolean animateLeft = !usingItem || (entity.getUsedItemHand() != leftInteractionArm);
        boolean animateRight = !usingItem || (entity.getUsedItemHand() != rightInteractionArm);

        flapAmount = Mth.cos(ageInTicks * WING_FLAP_RATE);
        flapAmount = flapAmount * flapAmount;
        float flapRotate = Mth.map(flapAmount, 0.0f, 1.0f, Mth.DEG_TO_RAD * -20.0f, Mth.DEG_TO_RAD * 32.0f);

        if (animateLeft) {
            leftArm.xRot = HumanoidAnimator.rotlerpRad(core.flyAmount, leftArm.xRot, 0.0f);
            leftArm.yRot = HumanoidAnimator.rotlerpRad(core.flyAmount, leftArm.yRot, -30.0f * Mth.DEG_TO_RAD);
            leftArm.zRot = HumanoidAnimator.rotlerpRad(core.flyAmount, leftArm.zRot, -(flapRotate + 80.0f * Mth.DEG_TO_RAD));
        }

        if (animateRight) {
            rightArm.xRot = HumanoidAnimator.rotlerpRad(core.flyAmount, rightArm.xRot, 0.0f);
            rightArm.yRot = HumanoidAnimator.rotlerpRad(core.flyAmount, rightArm.yRot, 30.0f * Mth.DEG_TO_RAD);
            rightArm.zRot = HumanoidAnimator.rotlerpRad(core.flyAmount, rightArm.zRot, (flapRotate + 80.0f * Mth.DEG_TO_RAD));
        }
    }
}
