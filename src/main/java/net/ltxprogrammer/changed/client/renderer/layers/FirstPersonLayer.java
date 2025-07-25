package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public interface FirstPersonLayer<T extends LivingEntity> {
    float ZFIGHT_OFFSET = 1.0002f;

    default void renderFirstPersonOnFace(PoseStack stack, MultiBufferSource bufferSource, int packedLight, T entity, Camera camera) {}
    default void renderFirstPersonOnArms(PoseStack stack, MultiBufferSource bufferSource, int packedLight, T entity, HumanoidArm arm, PoseStack stackCorrector, float partialTick) {}

    static void renderFirstPersonLayersOnFace(PoseStack poseStack, Camera camera, float partialTicks) {
        if (!(Minecraft.getInstance().getCameraEntity() instanceof LivingEntity livingEntity))
            return;

        final LivingEntity renderEntity = EntityUtil.maybeGetOverlaying(livingEntity);
        if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(renderEntity) instanceof LivingEntityRenderer livingEntityRenderer) {
            poseStack.pushPose();
            poseStack.scale(1.0f, -1.0f, 1.0f);
            poseStack.mulPose(camera.rotation());
            final var blockPos = new BlockPos(
                    Mth.floor(renderEntity.getEyePosition().x),
                    Mth.floor(renderEntity.getEyePosition().y),
                    Mth.floor(renderEntity.getEyePosition().z)
            );
            livingEntityRenderer.layers.forEach(layer -> {
                if (layer instanceof FirstPersonLayer firstPersonLayer) {
                    firstPersonLayer.renderFirstPersonOnFace(poseStack,
                            Minecraft.getInstance().renderBuffers().bufferSource(),
                            Minecraft.getInstance().level.getLightEngine().getRawBrightness(blockPos, 0),
                            renderEntity,
                            camera);
                }
            });
            poseStack.popPose();
        }
    }
}
