package net.ltxprogrammer.changed.client.renderer;

import com.mojang.math.Axis;
import org.joml.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.ExoskeletonModel;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.lang.Math;

public class ExoskeletonRenderer extends MobRenderer<Exoskeleton, ExoskeletonModel> {
    private static final RenderType BEAM_RENDER_TYPE = RenderType.eyes(Changed.modResource("textures/block/laser_beam.png"));

    public ExoskeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new ExoskeletonModel(context.bakeLayer(ExoskeletonModel.LAYER_LOCATION_SUIT)), 0.4f);
        this.addLayer(new VisorLayer(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Exoskeleton entity) {
        return model.getTexture(entity);
    }

    @Override
    protected void setupRotations(Exoskeleton exoskeleton, PoseStack poseStack, float bob, float bodyYRot, float partialTicks) {
        super.setupRotations(exoskeleton, poseStack, bob, bodyYRot, partialTicks);
        if (exoskeleton.isCharging() && exoskeleton.getSleepingPos().isPresent()) {
            poseStack.translate(0.0D, 1 / 16.0, 0.0D);
        }

    }

    @Override
    protected float getFlipDegrees(Exoskeleton exoskeleton) {
        return exoskeleton.isCharging() ? 0.0F : super.getFlipDegrees(exoskeleton);
    }

    private Vec3 getPosition(LivingEntity entity, double eyeHeight, float partialTicks) {
        double d0 = Mth.lerp((double)partialTicks, entity.xOld, entity.getX());
        double d1 = Mth.lerp((double)partialTicks, entity.yOld, entity.getY()) + eyeHeight;
        double d2 = Mth.lerp((double)partialTicks, entity.zOld, entity.getZ());
        return new Vec3(d0, d1, d2);
    }

    @Override
    public void render(Exoskeleton exoskeleton, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        float hurtTime = (float)exoskeleton.getHurtTime() - partialTicks;
        float damageTime = exoskeleton.getDamage() - partialTicks;
        if (damageTime < 0.0F) {
            damageTime = 0.0F;
        }

        if (hurtTime > 0.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damageTime / 10.0F * (float)exoskeleton.getHurtDir()));
        }

        super.render(exoskeleton, yRot, partialTicks, poseStack, bufferSource, packedLight);
        poseStack.popPose();

        LivingEntity target = exoskeleton.getActiveAttackTarget();
        if (target != null) {
            float attackScale = exoskeleton.getAttackAnimationScale(partialTicks);
            float worldTicks = (exoskeleton.tickCount % 10) + partialTicks;
            float f2 = worldTicks * 0.5F % 1.0F;
            float eyeHeight = exoskeleton.getBbHeight();
            poseStack.pushPose();
            poseStack.translate(0.0D, eyeHeight, 0.0D);
            Vec3 laserTarget = this.getPosition(target, (double)target.getBbHeight() * 0.5D, partialTicks);
            Vec3 laserSource = this.getPosition(exoskeleton, eyeHeight, partialTicks);
            Vec3 laserDirection = laserTarget.subtract(laserSource);
            float laserLength = (float)(laserDirection.length());
            laserDirection = laserDirection.normalize();
            float pitch = (float)Math.acos(laserDirection.y);
            float yaw = (float)Math.atan2(laserDirection.z, laserDirection.x);
            poseStack.mulPose(Axis.YP.rotationDegrees((((float)Math.PI / 2F) - yaw) * (180F / (float)Math.PI)));
            poseStack.translate(0.0D, 0.0D, 4.0D / 16.0D);
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch * (180F / (float) Math.PI)));
            int i = 1;
            float rotate = (worldTicks / 10f) * Mth.TWO_PI;
            float f8 = attackScale * attackScale;
            int red = 255;//64 + (int)(f8 * 191.0F);
            int green = 255;//32 + (int)(f8 * 191.0F);
            int blue = 255;//128 - (int)(f8 * 64.0F);
            float beamScaleEnd = 0.5F;
            float beamScaleStart = 0.5F;
            float f11 = Mth.cos(rotate + 2.3561945F) * beamScaleStart;
            float f12 = Mth.sin(rotate + 2.3561945F) * beamScaleStart;
            float f13 = Mth.cos(rotate + ((float)Math.PI / 4F)) * beamScaleStart;
            float f14 = Mth.sin(rotate + ((float)Math.PI / 4F)) * beamScaleStart;
            float f15 = Mth.cos(rotate + 3.926991F) * beamScaleStart;
            float f16 = Mth.sin(rotate + 3.926991F) * beamScaleStart;
            float f17 = Mth.cos(rotate + 5.4977875F) * beamScaleStart;
            float f18 = Mth.sin(rotate + 5.4977875F) * beamScaleStart;
            float f19 = Mth.cos(rotate + (float)Math.PI) * beamScaleEnd;
            float f20 = Mth.sin(rotate + (float)Math.PI) * beamScaleEnd;
            float f21 = Mth.cos(rotate + 0.0F) * beamScaleEnd;
            float f22 = Mth.sin(rotate + 0.0F) * beamScaleEnd;
            float f23 = Mth.cos(rotate + ((float)Math.PI / 2F)) * beamScaleEnd;
            float f24 = Mth.sin(rotate + ((float)Math.PI / 2F)) * beamScaleEnd;
            float f25 = Mth.cos(rotate + ((float)Math.PI * 1.5F)) * beamScaleEnd;
            float f26 = Mth.sin(rotate + ((float)Math.PI * 1.5F)) * beamScaleEnd;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = 0f;//-1.0F + f2;
            float f30 = 1f;//laserLength * 2.5F + f29;
            VertexConsumer buffer = bufferSource.getBuffer(BEAM_RENDER_TYPE);
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f pose = posestack$pose.pose();
            Matrix3f normal = posestack$pose.normal();

            vertex(buffer, pose, normal, f19, laserLength, f20, red, green, blue, 1.0F, f30);
            vertex(buffer, pose, normal, f19, 0.0F, f20, red, green, blue, 1.0F, f29);
            vertex(buffer, pose, normal, f21, 0.0F, f22, red, green, blue, 0.0F, f29);
            vertex(buffer, pose, normal, f21, laserLength, f22, red, green, blue, 0.0F, f30);

            vertex(buffer, pose, normal, f23, laserLength, f24, red, green, blue, 1.0F, f30);
            vertex(buffer, pose, normal, f23, 0.0F, f24, red, green, blue, 1.0F, f29);
            vertex(buffer, pose, normal, f25, 0.0F, f26, red, green, blue, 0.0F, f29);
            vertex(buffer, pose, normal, f25, laserLength, f26, red, green, blue, 0.0F, f30);
            poseStack.popPose();
        }
    }

    private static void vertex(VertexConsumer buffer, Matrix4f pose, Matrix3f normal, float x, float y, float z, int red, int green, int blue, float u, float v) {
        buffer.vertex(pose, x, y, z)
                .color(red, green, blue, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    public static class VisorLayer extends RenderLayer<Exoskeleton, ExoskeletonModel> {
        private final ExoskeletonModel.VisorModel model;

        public VisorLayer(RenderLayerParent<Exoskeleton, ExoskeletonModel> parent, EntityModelSet modelSet) {
            super(parent);
            this.model = new ExoskeletonModel.VisorModel(modelSet.bakeLayer(ExoskeletonModel.LAYER_LOCATION_VISOR));
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, Exoskeleton entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            model.matchParentAnim(this.getParentModel());
            model.renderToBuffer(pose, bufferSource.getBuffer(model.renderType(model.getTexture(entity))), packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
