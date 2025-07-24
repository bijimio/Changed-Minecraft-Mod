package net.ltxprogrammer.changed.client.renderer.animate.wing;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class BirdWingCreativeFlyAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractArmWingAnimator<T, M> {
    public BirdWingCreativeFlyAnimator(ModelPart leftWing, ModelPart leftSubWing, ModelPart rightWing, ModelPart rightSubWing) {
        super(leftWing, leftSubWing, rightWing, rightSubWing);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.CREATIVE_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftWing.yRot = Mth.lerp(core.flyAmount, leftWing.yRot, -75.0f * Mth.DEG_TO_RAD);
        leftSubWing.x = Mth.lerp(core.flyAmount, leftSubWing.x, 4.0f);
        leftSubWing.y = Mth.lerp(core.flyAmount, leftSubWing.y, -2.0f);

        rightWing.yRot = Mth.lerp(core.flyAmount, rightWing.yRot, 75.0f * Mth.DEG_TO_RAD);
        rightSubWing.x = Mth.lerp(core.flyAmount, leftSubWing.x, -4.0f);
        rightSubWing.y = Mth.lerp(core.flyAmount, leftSubWing.y, -2.0f);
    }
}
