package net.ltxprogrammer.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.client.WaveVisionRenderer;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockRenderDispatcher.class)
public abstract class BlockRenderDispatcherMixin {
    @WrapMethod(method = "getBlockModel")
    public BakedModel maybeWrapBlockModelForWaveVision(BlockState state, Operation<BakedModel> original) {
        if (!ChangedClient.shouldRenderingWaveVision())
            return original.call(state);
        if (state.is(ChangedTags.Blocks.CRYSTALLINE))
            return new WaveVisionRenderer.WrappedModel(original.call(state));

        return original.call(state);
    }

    @WrapOperation(method = "renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraftforge/client/model/data/ModelData;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockModelShaper;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;"))
    public BakedModel maybeWrapBlockModelForWaveVision(BlockModelShaper instance, BlockState state, Operation<BakedModel> original) {
        if (!ChangedClient.shouldRenderingWaveVision())
            return original.call(instance, state);
        if (state.is(ChangedTags.Blocks.CRYSTALLINE))
            return new WaveVisionRenderer.WrappedModel(original.call(instance, state));

        return original.call(instance, state);
    }
}
