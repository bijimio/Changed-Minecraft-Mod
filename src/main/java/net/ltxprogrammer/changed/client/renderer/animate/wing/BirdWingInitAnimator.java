package net.ltxprogrammer.changed.client.renderer.animate.wing;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class BirdWingInitAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractArmWingAnimator<T, M> {
    public BirdWingInitAnimator(ModelPart leftWing, ModelPart leftSubWing, ModelPart rightWing, ModelPart rightSubWing) {
        super(leftWing, leftSubWing, rightWing, rightSubWing);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftWing.xRot = 0.0f;
        leftWing.yRot = -60.0f * Mth.DEG_TO_RAD;
        leftWing.zRot = 0.0f;
        leftSubWing.x = 0.0f;
        leftSubWing.y = 0.0f;
        leftSubWing.z = 0.0f;

        rightWing.xRot = 0.0f;
        rightWing.yRot = 60.0f * Mth.DEG_TO_RAD;
        rightWing.zRot = 0.0f;
        rightSubWing.x = 0.0f;
        rightSubWing.y = 0.0f;
        rightSubWing.z = 0.0f;
    }
}
