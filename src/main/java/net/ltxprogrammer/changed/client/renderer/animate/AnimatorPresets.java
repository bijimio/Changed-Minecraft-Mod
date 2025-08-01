package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.client.renderer.animate.arm.*;
import net.ltxprogrammer.changed.client.renderer.animate.armsets.*;
import net.ltxprogrammer.changed.client.renderer.animate.bipedal.*;
import net.ltxprogrammer.changed.client.renderer.animate.camera.DragonCameraCreativeFlyAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.camera.OrcaCameraSwimAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.camera.SharkCameraSwimAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.camera.TaurCameraJumpAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.ears.BeeAntennaeInitAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.ears.CatEarsInitAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.ears.WolfEarsInitAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.legless.*;
import net.ltxprogrammer.changed.client.renderer.animate.misc.SquidDogTentaclesBobAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.misc.SquidDogTentaclesInitAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.misc.SquidDogTentaclesSwimAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.quadrupedal.*;
import net.ltxprogrammer.changed.client.renderer.animate.tail.*;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.*;
import net.ltxprogrammer.changed.client.renderer.animate.wing.*;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;
import java.util.function.Consumer;

public class AnimatorPresets {
    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> bipedal(ModelPart leftLeg, ModelPart rightLeg) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalInitAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalSwimAnimator<>(leftLeg, rightLeg));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> humanBipedal(ModelPart leftLeg, ModelPart rightLeg) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new HumanBipedalInitAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new HumanBipedalSwimAnimator<>(leftLeg, rightLeg));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> wolfBipedal(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                              ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new WolfBipedalInitAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new WolfBipedalSwimAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new ExoskeletonBipedalAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> catBipedal(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                              ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new CatBipedalInitAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new CatBipedalSwimAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new ExoskeletonBipedalAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> dragonBipedal(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                              ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new DragonBipedalInitAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new DragonBipedalFallFlyAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new DragonBipedalSwimAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new ExoskeletonBipedalAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> sharkBipedal(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                              ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new SharkBipedalInitAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new SharkBipedalSwimAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new ExoskeletonBipedalAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> orcaBipedal(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                              ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new SharkBipedalInitAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new OrcaBipedalSwimAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new ExoskeletonBipedalAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> quadrupedal(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        return animator -> {
            animator
                    .addAnimator(new QuadrupedalCrouchAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalInitAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalRideAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalSwimAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalStandAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalFallFlyAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalSleepAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> legless(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addAnimator(new LeglessInitAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessRideAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessCrouchAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessFallFlyAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessStandAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSwimAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSleepAnimator<>(abdomen, lowerAbdomen, tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> leglessV2(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addAnimator(new LeglessInitAnimatorV2<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessRideAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessCrouchAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessFallFlyAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessStandAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSwimAnimatorV2<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSleepAnimator<>(abdomen, lowerAbdomen, tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> leglessV2Snake(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addAnimator(new LeglessInitAnimatorV2<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessRideAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessCrouchAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessFallFlyAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessStandAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSlitherAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSleepAnimator<>(abdomen, lowerAbdomen, tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> leglessV2VerticalSwim(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addAnimator(new LeglessInitAnimatorV2<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessRideAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessCrouchAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessFallFlyAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessStandAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSwimAnimatorV2Vertical<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSleepAnimator<>(abdomen, lowerAbdomen, tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> taurUpperBodyOld(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHandsOld(1, leftArm, rightArm)
                    .addAnimator(new UpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> upperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHandsOld(1, leftArm, rightArm)
                    .addAnimator(new UpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> humanUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> wolfUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> catUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new CatUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new CatUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new CatUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new CatUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> dragonUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> dragonWingedUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new WingedDragonUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyCreativeFlyAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> sharkUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodySwimAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> orcaUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new OrcaUpperBodySwimAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> snakeUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> doubleArmUpperBody(ModelPart head, ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm) {
        return animator -> {
            animator.setupHands(1, upperLeftArm, upperRightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, upperLeftArm, upperRightArm))
                    .addAnimator(new HoldEntityAnimator<>(head, torso, lowerLeftArm, lowerRightArm))
                    .addAnimator(new DoubleArmUpperBodyInitAnimator<>(head, torso, upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new DoubleArmUpperBodyCrouchAnimator<>(head, torso, upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new DoubleArmUpperBodyAttackAnimator<>(head, torso, upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new DoubleArmUpperBodyStandAnimator<>(head, torso, upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> tripleArmUpperBody(ModelPart head, ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart middleLeftArm, ModelPart middleRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm) {
        return animator -> {
            animator.setupHands(1, upperLeftArm, upperRightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, upperLeftArm, upperRightArm))
                    .addAnimator(new HoldEntityAnimator<>(head, torso, middleLeftArm, middleRightArm))
                    .addAnimator(new HoldEntityAnimator<>(head, torso, lowerLeftArm, lowerRightArm))
                    .addAnimator(new TripleArmUpperBodyInitAnimator<>(head, torso, upperLeftArm, upperRightArm, middleLeftArm, middleRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new TripleArmUpperBodyCrouchAnimator<>(head, torso, upperLeftArm, upperRightArm, middleLeftArm, middleRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new TripleArmUpperBodyAttackAnimator<>(head, torso, upperLeftArm, upperRightArm, middleLeftArm, middleRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new TripleArmUpperBodyStandAnimator<>(head, torso, upperLeftArm, upperRightArm, middleLeftArm, middleRightArm, lowerLeftArm, lowerRightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> aquaticUpperBody(ModelPart head, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator
                    .addAnimator(new AquaticArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new AquaticHeadInitAnimator<>(head));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> taurUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new HoldEntityAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new TaurUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new TaurUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new TaurUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new TaurUpperBodyStandAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new TaurUpperBodyJumpAnimator<>(head, torso, leftArm, rightArm))
                    .addCameraAnimator(new TaurCameraJumpAnimator<>());
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> aquaticTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new AquaticTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new AquaticTailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> noSwimOrSleepTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new TailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> standardTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new TailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> wolfTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new WolfTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> catTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new CatTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> dragonTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new DragonTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> sharkTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new SharkTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new SharkTailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> orcaTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new SharkTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new OrcaTailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> squidDogTentacles(List<ModelPart> upperLeftTentacle, List<ModelPart> upperRightTentacle, List<ModelPart> lowerLeftTentacle, List<ModelPart> lowerRightTentacle) {
        return animator -> {
            animator.addAnimator(new SquidDogTentaclesInitAnimator<>(upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle))
                    .addAnimator(new SquidDogTentaclesSwimAnimator<>(upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle))
                    .addAnimator(new SquidDogTentaclesBobAnimator<>(upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> wolfEars(ModelPart leftEar, ModelPart rightEar) {
        return animator -> {
            animator
                    .addAnimator(new WolfEarsInitAnimator<>(leftEar, rightEar));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> catEars(ModelPart leftEar, ModelPart rightEar) {
        return animator -> {
            animator
                    .addAnimator(new CatEarsInitAnimator<>(leftEar, rightEar));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> beeAntennae(ModelPart leftAntennae, ModelPart rightAntennae) {
        return animator -> {
            animator
                    .addAnimator(new BeeAntennaeInitAnimator<>(leftAntennae, rightAntennae));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> wingedOld(ModelPart leftWing, ModelPart rightWing) {
        return animator -> {
            animator
                    .addAnimator(new WingInitAnimator<>(leftWing, rightWing))
                    .addAnimator(new WingFallFlyAnimator<>(leftWing, rightWing));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> wingedV2(ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                           ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator
                    .addAnimator(new WingInitAnimatorV2<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new WingFallFlyAnimatorV2<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> legacyWinged(ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                           ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator
                    .addAnimator(new LegacyWingInitAnimator<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new LegacyWingFallFlyAnimator<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> dragonWinged(ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                               ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator
                    .addAnimator(new DragonWingInitAnimator<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new DragonWingCreativeFlyAnimator<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new DragonWingFallFlyAnimator<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> armSetTwo(ModelPart leftArm, ModelPart rightArm,
                                                                                                            ModelPart leftArm2, ModelPart rightArm2) {
        return animator -> {
            animator.setupHandsOld(2, leftArm2, rightArm2)
                    .addAnimator(new ArmSetTwoBobAnimator<>(leftArm, rightArm, leftArm2, rightArm2))
                    .addAnimator(new ArmSetTwoCrouchAnimator<>(leftArm, rightArm, leftArm2, rightArm2))
                    .addAnimator(new ArmSetTwoFinalAnimator<>(leftArm, rightArm, leftArm2, rightArm2))
                    .addAnimator(new ArmSetTwoStandAnimator<>(leftArm, rightArm, leftArm2, rightArm2));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> armSetThree(ModelPart leftArm, ModelPart rightArm,
                                                                                                              ModelPart leftArm3, ModelPart rightArm3) {
        return animator -> {
            animator.setupHandsOld(3, leftArm3, rightArm3)
                    .addAnimator(new ArmSetThreeBobAnimator<>(leftArm, rightArm, leftArm3, rightArm3))
                    .addAnimator(new ArmSetThreeFinalAnimator<>(leftArm, rightArm, leftArm3, rightArm3));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> wolfLikeOld(ModelPart head, ModelPart torso,
                                                                                                              ModelPart leftArm, ModelPart rightArm,
                                                                                                              ModelPart tail, List<ModelPart> tailJoints,
                                                                                                              ModelPart leftLeg, ModelPart rightLeg) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(standardTail(tail, tailJoints))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> wolfLikeArmor(ModelPart head,
                                                                                                                ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                                ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                                ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(wolfBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(wolfUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> humanLike(ModelPart head,
                                                                                                           ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                           ModelPart leftLeg,
                                                                                                           ModelPart rightLeg) {
        return animator -> {
            animator.addPreset(humanBipedal(leftLeg, rightLeg))
                    .addPreset(humanUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> wolfLike(ModelPart head, ModelPart leftEar, ModelPart rightEar,
                                                                                                           ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints,
                                                                                                           ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                           ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(wolfBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(wolfUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(wolfTail(tail, tailJoints))
                    .addPreset(wolfEars(leftEar, rightEar))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> catLikeArmor(ModelPart head,
                                                                                                               ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                               ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                               ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(catBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(catUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new CatHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> catLike(ModelPart head, ModelPart leftEar, ModelPart rightEar,
                                                                                                           ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints,
                                                                                                           ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                           ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(catBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(catUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(catTail(tail, tailJoints))
                    .addPreset(catEars(leftEar, rightEar))
                    .addAnimator(new CatHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> deerLikeArmor(ModelPart head,
                                                                                                                ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                                ModelPart tail, List<ModelPart> tailJoints,
                                                                                                                ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                                ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(wolfBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(wolfUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(wolfTail(tail, tailJoints))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> deerLike(ModelPart head, ModelPart leftEar, ModelPart rightEar,
                                                                                                           ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints,
                                                                                                           ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                           ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(wolfBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(wolfUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(wolfTail(tail, tailJoints))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> dragonLike(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                             ModelPart tail, List<ModelPart> tailJoints,
                                                                                                             ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                             ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(dragonUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addAnimator(new DragonHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> bigTailDragonLike(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                             ModelPart tail, List<ModelPart> tailJoints,
                                                                                                             ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                             ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(dragonUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addAnimator(new DragonHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> wingedDragonLike(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                                   ModelPart tail, List<ModelPart> tailJoints,
                                                                                                                   ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                                   ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad,

                                                                                                                   ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                                   ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(dragonWingedUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addPreset(dragonWinged(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new DragonBipedalCreativeFlyAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new DragonTailCreativeFlyAnimator<>(tail, tailJoints))
                    .addAnimator(new DragonHeadCreativeFlyAnimator<>(head))
                    .addAnimator(new DragonHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm))
                    .addCameraAnimator(new DragonCameraCreativeFlyAnimator<>());
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> squidDogLikeArmor(ModelPart head,
                                                                                                                    ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm,
                                                                                                                    List<ModelPart> upperLeftTentacle, List<ModelPart> upperRightTentacle, List<ModelPart> lowerLeftTentacle, List<ModelPart> lowerRightTentacle,
                                                                                                                    ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                                    ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(wolfBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(doubleArmUpperBody(head, torso, upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addPreset(squidDogTentacles(upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(upperLeftArm, upperRightArm))
                    .addAnimator(new DoubleArmBobAnimator<>(upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new ArmRideAnimator<>(upperLeftArm, upperRightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> squidDogLike(ModelPart head, ModelPart leftEar, ModelPart rightEar,
                                                                                                           ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints, List<ModelPart> upperLeftTentacle, List<ModelPart> upperRightTentacle, List<ModelPart> lowerLeftTentacle, List<ModelPart> lowerRightTentacle,
                                                                                                           ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                           ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(wolfBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(doubleArmUpperBody(head, torso, upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addPreset(wolfTail(tail, tailJoints))
                    .addPreset(wolfEars(leftEar, rightEar))
                    .addPreset(squidDogTentacles(upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(upperLeftArm, upperRightArm))
                    .addAnimator(new DoubleArmBobAnimator<>(upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new ArmRideAnimator<>(upperLeftArm, upperRightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> mothLike(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                                          ModelPart tail, List<ModelPart> tailJoints, ModelPart leftWing, ModelPart rightWing,
                                                                                                                          ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                                          ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(dragonUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new BeeWingInitAnimator<>(leftWing, rightWing))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> mothLikeArmor(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                                          ModelPart tail, List<ModelPart> tailJoints,
                                                                                                                          ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                                          ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(dragonUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> beeLike(ModelPart head, ModelPart leftAntennae, ModelPart rightAntennae,
                                                                                                           ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints, ModelPart leftWing, ModelPart rightWing,
                                                                                                           ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                           ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(doubleArmUpperBody(head, torso, upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addPreset(beeAntennae(leftAntennae, rightAntennae))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new BeeWingInitAnimator<>(leftWing, rightWing))
                    .addAnimator(new ArmSwimAnimator<>(upperLeftArm, upperRightArm))
                    .addAnimator(new DoubleArmBobAnimator<>(upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new ArmRideAnimator<>(upperLeftArm, upperRightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> beeLikeArmor(ModelPart head,
                                                                                                               ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm,
                                                                                                               ModelPart tail, List<ModelPart> tailJoints, ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                               ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(doubleArmUpperBody(head, torso, upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(upperLeftArm, upperRightArm))
                    .addAnimator(new DoubleArmBobAnimator<>(upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new ArmRideAnimator<>(upperLeftArm, upperRightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> stigerLike(ModelPart head, ModelPart leftEar, ModelPart rightEar,
                                                                                                                  ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm,
                                                                                                                  ModelPart middleLeftArm, ModelPart middleRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm,
                                                                                                                  ModelPart tail, List<ModelPart> tailJoints,
                                                                                                                  ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                                  ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(tripleArmUpperBody(head, torso, upperLeftArm, upperRightArm, middleLeftArm, middleRightArm, lowerLeftArm, lowerRightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addPreset(catEars(leftEar, rightEar))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(upperLeftArm, upperRightArm))
                    .addAnimator(new DoubleArmBobAnimator<>(upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new ArmRideAnimator<>(upperLeftArm, upperRightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> stigerLikeArmor(ModelPart head,
                                                                                                                  ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm,
                                                                                                                  ModelPart middleLeftArm, ModelPart middleRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm,
                                                                                                                  ModelPart tail, List<ModelPart> tailJoints,
                                                                                                                  ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                                  ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(tripleArmUpperBody(head, torso, upperLeftArm, upperRightArm, middleLeftArm, middleRightArm, lowerLeftArm, lowerRightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(upperLeftArm, upperRightArm))
                    .addAnimator(new DoubleArmBobAnimator<>(upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new ArmRideAnimator<>(upperLeftArm, upperRightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> dragonLikeOld(ModelPart head, ModelPart torso,
                                                                                                                ModelPart leftArm, ModelPart rightArm,
                                                                                                                ModelPart tail, List<ModelPart> tailJoints,
                                                                                                                ModelPart leftLeg, ModelPart rightLeg,
                                                                                                                ModelPart leftWing, ModelPart rightWing) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(standardTail(tail, tailJoints))
                    .addPreset(wingedOld(leftWing, rightWing))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> dragonLikeRemodel(ModelPart head, ModelPart torso,
                                                                                                                    ModelPart leftArm, ModelPart rightArm,
                                                                                                                    ModelPart tail, List<ModelPart> tailJoints,
                                                                                                                    ModelPart leftLeg, ModelPart rightLeg,

                                                                                                                    ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                                    ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(standardTail(tail, tailJoints))
                    .addPreset(wingedV2(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> legacyDragonLike(ModelPart head, ModelPart torso,
                                                                                                               ModelPart leftArm, ModelPart rightArm,
                                                                                                               ModelPart tail, List<ModelPart> tailJoints,
                                                                                                               ModelPart leftLeg, ModelPart rightLeg,

                                                                                                               ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                               ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(standardTail(tail, tailJoints))
                    .addPreset(legacyWinged(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> sharkLikeOld(ModelPart head, ModelPart torso,
                                                                                                               ModelPart leftArm, ModelPart rightArm,
                                                                                                               ModelPart tail, List<ModelPart> tailJoints,
                                                                                                               ModelPart leftLeg, ModelPart rightLeg) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(aquaticUpperBody(head, leftArm, rightArm))
                    .addPreset(aquaticTail(tail, tailJoints))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> sharkLike(ModelPart head, ModelPart torso,
                                                                                                            ModelPart leftArm, ModelPart rightArm,
                                                                                                            ModelPart tail, List<ModelPart> tailJoints,

                                                                                                            ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                            ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(sharkBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(sharkUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(sharkTail(tail, tailJoints))
                    .addAnimator(new SharkHeadInitAnimator<>(head))
                    .addAnimator(new SharkHeadSwimAnimator<>(head))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm))
                    .addCameraAnimator(new SharkCameraSwimAnimator<>());
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> orcaLike(ModelPart head, ModelPart torso,
                                                                                                            ModelPart leftArm, ModelPart rightArm,
                                                                                                            ModelPart tail, List<ModelPart> tailJoints,

                                                                                                            ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                            ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(orcaBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(orcaUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(orcaTail(tail, tailJoints))
                    .addAnimator(new SharkHeadInitAnimator<>(head))
                    .addAnimator(new OrcaHeadSwimAnimator<>(head))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm))
                    .addCameraAnimator(new OrcaCameraSwimAnimator<>());
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> snakeLike(ModelPart head, ModelPart torso,
                                                                                                            ModelPart leftArm, ModelPart rightArm,
                                                                                                            ModelPart abdomen, ModelPart lowerAbdomen,
                                                                                                            ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addPreset(leglessV2Snake(abdomen, lowerAbdomen, tail, tailJoints))
                    .addPreset(snakeUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkHeadInitAnimator<>(head))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> snakeAbdomenArmor(ModelPart abdomen, ModelPart lowerAbdomen,
                                                                                                                                   ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addPreset(leglessV2Snake(abdomen, lowerAbdomen, tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> snakeUpperBodyArmor(ModelPart head, ModelPart torso,
                                                                                                                                     ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.addPreset(snakeUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkHeadInitAnimator<>(head))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> leglessShark(ModelPart head, ModelPart torso,
                                                                                                               ModelPart leftArm, ModelPart rightArm,
                                                                                                               ModelPart abdomen, ModelPart lowerAbdomen,
                                                                                                               ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addPreset(leglessV2(abdomen, lowerAbdomen, tail, tailJoints))
                    .addPreset(sharkUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkHeadInitAnimator<>(head))
                    .addAnimator(new SharkHeadSwimAnimator<>(head))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm))
                    .addCameraAnimator(new SharkCameraSwimAnimator<>());
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> leglessSharkUpperBodyArmor(ModelPart head, ModelPart torso,
                                                                                                                           ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.addPreset(sharkUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkHeadInitAnimator<>(head))
                    .addAnimator(new SharkHeadSwimAnimator<>(head))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> leglessSharkAbdomenArmor(ModelPart abdomen, ModelPart lowerAbdomen,
                                                                                                                         ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addPreset(leglessV2(abdomen, lowerAbdomen, tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> leglessMantaRay(ModelPart head, ModelPart torso,
                                                                                                                       ModelPart leftArm, ModelPart rightArm,
                                                                                                                       ModelPart abdomen, ModelPart lowerAbdomen,
                                                                                                                       ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addPreset(leglessV2VerticalSwim(abdomen, lowerAbdomen, tail, tailJoints))
                    .addPreset(orcaUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkHeadInitAnimator<>(head))
                    .addAnimator(new OrcaHeadSwimAnimator<>(head))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm))
                    .addCameraAnimator(new OrcaCameraSwimAnimator<>());
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> leglessMantaRayUpperBodyArmor(ModelPart head, ModelPart torso,
                                                                                                                            ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.addPreset(orcaUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkHeadInitAnimator<>(head))
                    .addAnimator(new OrcaHeadSwimAnimator<>(head))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> leglessMantaRayAbdomenArmor(ModelPart abdomen, ModelPart lowerAbdomen,
                                                                                                                            ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addPreset(leglessV2VerticalSwim(abdomen, lowerAbdomen, tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> taurLegsOld(ModelPart tail, List<ModelPart> tailJoints,
                                                                                                              ModelPart frontLeftLeg, ModelPart frontRightLeg,
                                                                                                              ModelPart lowerTorso, ModelPart backLeftLeg, ModelPart backRightLeg) {
        return animator -> {
            animator.addPreset(quadrupedal(lowerTorso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addPreset(noSwimOrSleepTail(tail, tailJoints));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> taurLegs(ModelPart lowerTorso, ModelPart frontLeftLeg, ModelPart frontLeftLegLower, ModelPart frontLeftFoot,
                                                                                                           ModelPart frontRightLeg, ModelPart frontRightLegLower, ModelPart frontRightFoot,
                                                                                                           ModelPart backLeftLeg, ModelPart backLeftLegLower, ModelPart backLeftFoot, ModelPart backLeftPad,
                                                                                                           ModelPart backRightLeg, ModelPart backRightLegLower, ModelPart backRightFoot, ModelPart backRightPad) {
        return animator -> {
            animator.addAnimator(new TaurQuadrupedalInitAnimator<>(lowerTorso,  frontLeftLeg,  frontLeftLegLower,  frontLeftFoot,
                             frontRightLeg,  frontRightLegLower,  frontRightFoot,
                             backLeftLeg,  backLeftLegLower,  backLeftFoot,  backLeftPad,
                             backRightLeg,  backRightLegLower,  backRightFoot,  backRightPad))
                    .addAnimator(new TaurQuadrupedalSwimAnimator<>(lowerTorso,  frontLeftLeg,  frontLeftLegLower,  frontLeftFoot,
                             frontRightLeg,  frontRightLegLower,  frontRightFoot,
                             backLeftLeg,  backLeftLegLower,  backLeftFoot,  backLeftPad,
                             backRightLeg,  backRightLegLower,  backRightFoot,  backRightPad))
                    .addAnimator(new QuadrupedalRideAnimator<>(lowerTorso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalSleepAnimator<>(lowerTorso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new TaurQuadrupedalStandAnimator<>(lowerTorso,  frontLeftLeg,  frontLeftLegLower,  frontLeftFoot,
                            frontRightLeg,  frontRightLegLower,  frontRightFoot,
                            backLeftLeg,  backLeftLegLower,  backLeftFoot,  backLeftPad,
                            backRightLeg,  backRightLegLower,  backRightFoot,  backRightPad))
                    .addAnimator(new TaurQuadrupedalCrouchAnimator<>(lowerTorso,  frontLeftLeg,  frontLeftLegLower,  frontLeftFoot,
                            frontRightLeg,  frontRightLegLower,  frontRightFoot,
                            backLeftLeg,  backLeftLegLower,  backLeftFoot,  backLeftPad,
                            backRightLeg,  backRightLegLower,  backRightFoot,  backRightPad))
                    .addAnimator(new QuadrupedalFallFlyAnimator<>(lowerTorso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalJumpAnimator<>(lowerTorso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> taurLikeOld(ModelPart head, ModelPart torso,
                                                                                                              ModelPart leftArm, ModelPart rightArm,
                                                                                                              ModelPart tail, List<ModelPart> tailJoints,
                                                                                                              ModelPart frontLeftLeg, ModelPart frontRightLeg,
                                                                                                              ModelPart lowerTorso, ModelPart backLeftLeg, ModelPart backRightLeg) {
        return animator -> {
            animator.addPreset(taurLegsOld(tail, tailJoints, frontLeftLeg, frontRightLeg, lowerTorso, backLeftLeg, backRightLeg))
                    .addPreset(taurUpperBodyOld(head, torso, leftArm, rightArm))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> Consumer<HumanoidAnimator<T, M>> taurLike(ModelPart head, ModelPart leftEar, ModelPart rightEar,
                                                                                                           ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                           ModelPart lowerTorso, ModelPart frontLeftLeg, ModelPart frontLeftLegLower, ModelPart frontLeftFoot,
                                                                                                           ModelPart frontRightLeg, ModelPart frontRightLegLower, ModelPart frontRightFoot,
                                                                                                           ModelPart backLeftLeg, ModelPart backLeftLegLower, ModelPart backLeftFoot, ModelPart backLeftPad,
                                                                                                           ModelPart backRightLeg, ModelPart backRightLegLower, ModelPart backRightFoot, ModelPart backRightPad) {
        return animator -> {
            animator.addPreset(taurLegs(lowerTorso, frontLeftLeg, frontLeftLegLower, frontLeftFoot, frontRightLeg, frontRightLegLower, frontRightFoot, backLeftLeg, backLeftLegLower, backLeftFoot, backLeftPad, backRightLeg, backRightLegLower, backRightFoot, backRightPad))
                    .addPreset(taurUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }
}
