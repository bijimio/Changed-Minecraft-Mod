package net.ltxprogrammer.changed.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.item.SpecializedItemRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ItemLayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ResourceManagerReloadListener {
    @Shadow public abstract ItemModelShaper getItemModelShaper();

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;handleCameraTransforms(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemDisplayContext;Z)Lnet/minecraft/client/resources/model/BakedModel;"))
    public BakedModel getOverriddenModel(PoseStack poseStack, BakedModel model, ItemDisplayContext cameraTransformType, boolean applyLeftHandTransform, Operation<BakedModel> original,
                                        @Local(argsOnly = true) ItemStack stack) {
        if (stack.getItem() instanceof SpecializedItemRendering special) {
            model = this.getItemModelShaper().getModelManager().getModel(special.getModelLocation(stack, cameraTransformType));
        }

        return original.call(poseStack, model, cameraTransformType, applyLeftHandTransform);
    }
}
