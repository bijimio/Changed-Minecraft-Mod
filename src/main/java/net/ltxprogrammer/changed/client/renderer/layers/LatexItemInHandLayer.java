package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.UseItemMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LatexItemInHandLayer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
    private static final float X_ROT_MIN = (-(float)Math.PI / 6F);
    private static final float X_ROT_MAX = ((float)Math.PI / 2F);

    private final ItemInHandRenderer itemInHandRenderer;

    public LatexItemInHandLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer itemInHandRenderer) {
        super(parent, itemInHandRenderer);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    protected void renderArmWithItem(LivingEntity p_174525_, ItemStack p_174526_, ItemDisplayContext p_174527_, HumanoidArm p_174528_, PoseStack poseStack, MultiBufferSource p_174530_, int p_174531_) {
        if (p_174525_ instanceof ChangedEntity ChangedEntity && ChangedEntity.getUnderlyingPlayer() != null)
            p_174525_ = ChangedEntity.getUnderlyingPlayer();

        if (p_174526_.is(Items.SPYGLASS) && p_174525_.getUseItem() == p_174526_ && p_174525_.swingTime == 0) {
            this.renderArmWithSpyglass(p_174525_, p_174526_, p_174528_, poseStack, p_174530_, p_174531_);
        } else {
            poseStack.pushPose();
            if (this.getParentModel() instanceof AdvancedHumanoidModelInterface<?,?> modelInterface)
                modelInterface.scaleForBody(poseStack);
            super.renderArmWithItem(p_174525_, p_174526_, p_174527_, p_174528_, poseStack, p_174530_, p_174531_);
            poseStack.popPose();
        }

    }

    private void renderArmWithSpyglass(LivingEntity entity, ItemStack itemStack, HumanoidArm arm, PoseStack pose, MultiBufferSource source, int color) {
        pose.pushPose();
        ModelPart modelpart = this.getParentModel().getHead();
        if (this.getParentModel() instanceof AdvancedHumanoidModelInterface<?,?> modelInterface)
            modelInterface.scaleForHead(pose);
        float f = modelpart.xRot;
        modelpart.xRot = Mth.clamp(modelpart.xRot, (-(float)Math.PI / 6F), ((float)Math.PI / 2F));
        modelpart.translateAndRotate(pose);
        modelpart.xRot = f;
        CustomHeadLayer.translateToHead(pose, false);
        boolean flag = arm == HumanoidArm.LEFT;
        /*var list = AdvancedHumanoidModel.findLargestCube(modelpart);
        if (list.isEmpty()) {
            pose.popPose();
            return;
        }
        var headCube = list.get(0);
        float dH = 0.5f - headCube.maxY;*/
        pose.translate(((flag ? -2.5F : 2.5F) / 16.0F), -0.0625D/* + (dH / 16.0f)*/, 0.0D);
        itemInHandRenderer.renderItem(entity, itemStack, ItemDisplayContext.HEAD, false, pose, source, color);
        pose.popPose();
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getItemUseMode() == UseItemMode.NORMAL)
            super.render(pose, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        else if (entity.getItemUseMode() == UseItemMode.MOUTH) {
            boolean flag = entity.isSleeping();
            pose.pushPose();
            var head = this.getParentModel().getHead();
            if (this.getParentModel() instanceof AdvancedHumanoidModelInterface<?,?> modelInterface)
                modelInterface.scaleForHead(pose);
            pose.translate(head.x / 16.0F, (head.y) / 16.0F, head.z / 16.0F);
            pose.mulPose(Axis.ZP.rotation(0.0F));
            pose.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
            pose.mulPose(Axis.XP.rotationDegrees(headPitch));
            if (flag) {
                pose.translate(0.46F, 0.26F, 0.22F);
            } else {
                pose.translate(0.06F, 0.27F, -0.5D);
            }

            pose.mulPose(Axis.XP.rotationDegrees(90.0F));
            if (flag) {
                pose.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }
            pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
            pose.translate(1.0 / 16.0F, -2.0 / 16.0F, 1.0 / 16.0F);

            ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
            itemInHandRenderer.renderItem(entity, itemstack, ItemDisplayContext.GROUND, false, pose, bufferSource, packedLight);
            pose.popPose();
        }
    }
}
