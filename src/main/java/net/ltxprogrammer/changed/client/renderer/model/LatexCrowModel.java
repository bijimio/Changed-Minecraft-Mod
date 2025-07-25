package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexCrocodile;
import net.ltxprogrammer.changed.entity.beast.LatexCrow;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LatexCrowModel extends AdvancedHumanoidModel<LatexCrow> implements AdvancedHumanoidModelInterface<LatexCrow, LatexCrowModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_crow"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final HumanoidAnimator<LatexCrow, LatexCrowModel> animator;

    public LatexCrowModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        var tailPrimary = Tail.getChild("TailPrimary");

        var leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
        var leftFoot = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = RightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        var leftWing = LeftArm.getChild("LeftFlight");
        var leftSubWing = leftWing.getChild("LeftSubFlight");
        var rightWing = RightArm.getChild("RightFlight");
        var rightSubWing = rightWing.getChild("RightSubFlight");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.birdLike(
                        Head, Torso, LeftArm, RightArm,
                        Tail, List.of(tailPrimary),
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad"),
                        leftWing, leftSubWing, rightWing, rightSubWing));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-0.99F, 0.0168F, 0.0504F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, -1.025F, 0.45F, 0.7418F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.875F, 6.0F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(28, 45).addBox(-1.0F, -8.2F, -0.725F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 7.075F, -4.075F, -0.2618F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(46, 54).addBox(-0.5F, 0.0F, 1.65F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.025F))
                .texOffs(0, 56).addBox(-0.5F, 0.0F, -3.25F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.025F))
                .texOffs(48, 35).addBox(-1.5F, 0.0F, -1.3F, 3.0F, 2.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offset(0.0F, 4.275F, -2.925F));

        PartDefinition RightPad_r1 = RightPad.addOrReplaceChild("RightPad_r1", CubeListBuilder.create().texOffs(18, 55).addBox(-0.5F, -1.5F, -4.75F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 1.5F, 1.475F, 0.0F, -0.3927F, 0.0F));

        PartDefinition RightPad_r2 = RightPad.addOrReplaceChild("RightPad_r2", CubeListBuilder.create().texOffs(12, 55).addBox(-0.5F, -1.5F, -4.75F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 1.5F, 1.475F, 0.0F, 0.3927F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(48, 0).addBox(-0.99F, 0.0168F, 0.0504F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, -1.025F, 0.45F, 0.7418F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.875F, 6.0F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(40, 45).addBox(-1.0F, -8.2F, -0.725F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 7.075F, -4.075F, -0.2618F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(6, 56).addBox(-0.5F, 0.0F, 1.65F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.025F))
                .texOffs(58, 43).addBox(-0.5F, 0.0F, -3.25F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.025F))
                .texOffs(52, 14).addBox(-1.5F, 0.0F, -1.3F, 3.0F, 2.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offset(0.0F, 4.275F, -2.925F));

        PartDefinition LeftPad_r1 = LeftPad.addOrReplaceChild("LeftPad_r1", CubeListBuilder.create().texOffs(58, 23).addBox(-0.5F, -1.5F, -4.75F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 1.5F, 1.475F, 0.0F, -0.3927F, 0.0F));

        PartDefinition LeftPad_r2 = LeftPad.addOrReplaceChild("LeftPad_r2", CubeListBuilder.create().texOffs(58, 19).addBox(-0.5F, -1.5F, -4.75F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 1.5F, 1.475F, 0.0F, 0.3927F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(48, 30).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.0F, -4.75F, 0.0F, -0.7854F, 0.0F));

        PartDefinition Feathers = Head.addOrReplaceChild("Feathers", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -1.0F));

        PartDefinition Head_r1 = Feathers.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(32, 11).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -8.0F, -2.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition Head_r2 = Feathers.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(52, 43).addBox(0.0F, -2.0F, 0.0F, 3.0F, 8.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, -6.0F, -2.0F, 0.0F, -1.0472F, 0.0F));

        PartDefinition Head_r3 = Feathers.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(34, 54).addBox(-3.0F, -2.0F, 0.0F, 3.0F, 8.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, -6.0F, 4.0F, 0.0F, 1.3963F, 0.0F));

        PartDefinition Head_r4 = Feathers.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(28, 42).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -8.0F, 4.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Head_r5 = Feathers.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(40, 54).addBox(0.0F, -2.0F, 0.0F, 3.0F, 8.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, -6.0F, 4.0F, 0.0F, -1.3963F, 0.0F));

        PartDefinition Head_r6 = Feathers.addOrReplaceChild("Head_r6", CubeListBuilder.create().texOffs(52, 51).addBox(-3.0F, -2.0F, 0.0F, 3.0F, 8.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, -6.0F, 1.0F, 0.0F, 1.2217F, 0.0F));

        PartDefinition Head_r7 = Feathers.addOrReplaceChild("Head_r7", CubeListBuilder.create().texOffs(40, 27).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -8.0F, 1.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition Head_r8 = Feathers.addOrReplaceChild("Head_r8", CubeListBuilder.create().texOffs(28, 54).addBox(0.0F, -2.0F, 0.0F, 3.0F, 8.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, -6.0F, 1.0F, 0.0F, -1.2217F, 0.0F));

        PartDefinition Head_r9 = Feathers.addOrReplaceChild("Head_r9", CubeListBuilder.create().texOffs(52, 19).addBox(-3.0F, -2.0F, 0.0F, 3.0F, 8.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, -6.0F, -2.0F, 0.0F, 1.0472F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 10.5F, 0.0F, -0.4363F, 0.0F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, 0.75F, -0.5F, 8.0F, 11.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.9635F, 0.0F, 3.1416F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition RightArm_r1 = RightArm.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(48, 8).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, -2.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition RightFlight = RightArm.addOrReplaceChild("RightFlight", CubeListBuilder.create().texOffs(40, 14).addBox(-6.0F, -6.0F, 0.0F, 6.0F, 13.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, 3.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        PartDefinition RightSubFlight = RightFlight.addOrReplaceChild("RightSubFlight", CubeListBuilder.create().texOffs(40, 14).addBox(-6.0F, -6.0F, 0.25F, 6.0F, 13.0F, 0.0F, CubeDeformation.NONE), PartPose.offset(-3.0F, -2.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(24, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 1.5F, 0.0F));

        PartDefinition LeftArm_r1 = LeftArm.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(50, 40).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, -2.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition LeftFlight = LeftArm.addOrReplaceChild("LeftFlight", CubeListBuilder.create().texOffs(16, 42).addBox(0.0F, -6.0F, 0.0F, 6.0F, 13.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, 3.0F, 0.0F, 0.0F, -1.309F, 0.0F));

        PartDefinition LeftSubFlight = LeftFlight.addOrReplaceChild("LeftSubFlight", CubeListBuilder.create().texOffs(16, 42).addBox(0.0F, -6.0F, 0.25F, 6.0F, 13.0F, 0.0F, CubeDeformation.NONE), PartPose.offset(4.0F, -2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    @Override
    public void prepareMobModel(LatexCrow p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand(LatexCrow entity) {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexCrow entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getLeg(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        Torso.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public HumanoidAnimator<LatexCrow, LatexCrowModel> getAnimator(LatexCrow entity) {
        return animator;
    }
}
