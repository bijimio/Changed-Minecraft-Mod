package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TaurUpperBodyInitAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public TaurUpperBodyInitAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean fallFlying = entity.getFallFlyingTicks() > 4;
        torso.yRot = 0.0F;
        torso.zRot = 0.0F;
        torso.x = 0F;
        rightArm.z = 0.0F;
        rightArm.x = -core.torsoWidth;
        leftArm.z = 0.0F;
        leftArm.x = core.torsoWidth;
        float f = 1.0F;
        if (fallFlying) {
            f = (float)entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        rightArm.zRot = 0.0F;
        leftArm.zRot = 0.0F;

        rightArm.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;

        rightArm.zRot += Mth.lerp(core.reachOut, 0.0F, 0.1745329f); // 10 Degrees
        leftArm.zRot += Mth.lerp(core.reachOut, 0.0F, -0.1745329f); // 10 Degrees
        rightArm.xRot = Mth.lerp(core.reachOut, rightArm.xRot, -0.5235988f); // 30 Degrees
        leftArm.xRot = Mth.lerp(core.reachOut, leftArm.xRot, -0.5235988f);   // 30 Degrees
    }
}