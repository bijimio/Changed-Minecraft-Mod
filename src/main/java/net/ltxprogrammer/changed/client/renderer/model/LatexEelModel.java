package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexEel;
import net.ltxprogrammer.changed.entity.beast.LatexShark;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LatexEelModel extends AdvancedHumanoidModel<LatexEel> implements AdvancedHumanoidModelInterface<LatexEel, LatexEelModel> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_eel"), "main");
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart Head;
	private final ModelPart Torso;
	private final ModelPart Tail;
	private final HumanoidAnimator<LatexEel, LatexEelModel> animator;

	// TODO finalize texture and model
	public LatexEelModel(ModelPart root) {
		super(root);
		this.RightLeg = root.getChild("RightLeg");
		this.LeftLeg = root.getChild("LeftLeg");
		this.Head = root.getChild("Head");
		this.Torso = root.getChild("Torso");
		this.Tail = Torso.getChild("Tail");
		this.RightArm = root.getChild("RightArm");
		this.LeftArm = root.getChild("LeftArm");

		var tailPrimary = Tail.getChild("TailPrimary");
		var tailSecondary = tailPrimary.getChild("TailSecondary");
		var tailTertiary = tailSecondary.getChild("TailTertiary");
		var tailQuaternary = tailTertiary.getChild("TailQuaternary");

		var leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
		var leftFoot = leftLowerLeg.getChild("LeftFoot");
		var rightLowerLeg = RightLeg.getChild("RightLowerLeg");
		var rightFoot = rightLowerLeg.getChild("RightFoot");

		animator = HumanoidAnimator.of(this).hipOffset(-1.5f).legSpacing(0.5f)
				.addPreset(AnimatorPresets.sharkLike(
						Head, Torso, LeftArm, RightArm,
						Tail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary),
						LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

		PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(52, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

		PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(56, 0).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

		PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

		PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(16, 64).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

		PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(46, 62).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

		PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 56).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

		PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(30, 61).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

		PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

		PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(64, 56).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

		PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(64, 49).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(48, 49).addBox(0.0F, -13.0F, -4.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(20, 46).addBox(0.0F, -13.0F, 4.0F, 0.0F, 13.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(68, 16).addBox(-1.5F, -3.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.01F))
				.texOffs(68, 45).addBox(-1.5F, -1.0F, -6.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(46, 69).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -3.01F, -7.0F, 0.0F, -0.2182F, 0.0F));

		PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(68, 39).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -3.01F, -7.0F, 0.0F, 0.2182F, 0.0F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
				.texOffs(0, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(64, 65).addBox(0.0F, 0.0F, 2.0F, 0.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));

		PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.75F));

		PartDefinition Fin_r1 = TailPrimary.addOrReplaceChild("Fin_r1", CubeListBuilder.create().texOffs(0, 67).addBox(-0.025F, 0.75F, 3.7F, 0.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(32, 16).addBox(-2.5F, 0.75F, -1.3F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 1.1781F, 0.0F, 0.0F));

		PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(56, 10).addBox(-2.5F, -2.1F, -0.95F, 5.0F, 3.0F, 3.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 0.875F, 0.85F, 1.9199F, 0.0F, 0.0F));

		PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 3.25F, 7.25F));

		PartDefinition Fin_r2 = TailSecondary.addOrReplaceChild("Fin_r2", CubeListBuilder.create().texOffs(8, 67).addBox(0.475F, -1.3563F, 2.8912F, 0.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(48, 29).addBox(-2.025F, -1.3563F, -2.1088F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-0.475F, 0.5F, 1.0F, 1.309F, 0.0F, 0.0F));

		PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, 4.5F));

		PartDefinition Fin_r3 = TailTertiary.addOrReplaceChild("Fin_r3", CubeListBuilder.create().texOffs(68, 30).addBox(-0.025F, -0.5949F, 2.7797F, 0.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.15F, 0.25F, 1.4835F, 0.0F, 0.0F));

		PartDefinition Base_r2 = TailTertiary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(48, 39).addBox(-2.5F, -0.5949F, -2.2203F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, -0.25F, 0.25F, 1.4835F, 0.0F, 0.0F));

		PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.25F, 5.0F));

		PartDefinition Fin_r4 = TailQuaternary.addOrReplaceChild("Fin_r4", CubeListBuilder.create().texOffs(68, 21).addBox(-0.025F, -0.5949F, 2.7797F, 0.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.025F, 0.0F, -0.175F, 1.5272F, 0.0F, 0.0F));

		PartDefinition Base_r3 = TailQuaternary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 46).addBox(-2.5F, -0.5949F, -2.2203F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-0.075F)), PartPose.offsetAndRotation(0.0F, -0.1F, -0.125F, 1.5272F, 0.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(32, 45).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(32, 29).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void prepareMobModel(LatexEel p_102861_, float p_102862_, float p_102863_, float p_102864_) {
		this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
	}

	public void setupHand(LatexEel entity) {
		animator.setupHand();
	}

	@Override
	public HumanoidAnimator<LatexEel, LatexEelModel> getAnimator(LatexEel entity) {
		return animator;
	}

	@Override
	public void setupAnim(LatexEel entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
		Head.render(poseStack, buffer, packedLight, packedOverlay);
		Torso.render(poseStack, buffer, packedLight, packedOverlay);
		RightArm.render(poseStack, buffer, packedLight, packedOverlay);
		LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
	}
}