package net.ltxprogrammer.changed.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.client.LatexCoveredBlocksRenderer;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public abstract class ChunkRenderDispatcherMixin {
    @Shadow @Final ChunkRenderDispatcher.RenderChunk this$1;

    @Unique
    public LatexCoverState getLatexCoverState(RenderChunkRegion region, BlockPos blockPos) {
        int i = SectionPos.blockToSectionCoord(blockPos.getX()) - region.centerX;
        int j = SectionPos.blockToSectionCoord(blockPos.getZ()) - region.centerZ;
        return LatexCoverState.getAt(region.chunks[i][j].wrapped, blockPos);
    }

    @Unique
    public void beginLayer(BufferBuilder builder) {
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
    }

    @Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getBlockRenderer()Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;"))
    public void addCompileSteps(float cameraX, float cameraY, float cameraZ, ChunkBufferBuilderPack bufferBuilderPack, CallbackInfoReturnable<?> cir,
                                @Local RenderChunkRegion region,
                                @Local PoseStack poseStack,
                                @Local Set<RenderType> renderTypes,
                                @Local RandomSource random) {
        BlockPos start = this$1.getOrigin().immutable();
        BlockPos end = start.offset(15, 15, 15);

        for (BlockPos blockPos : BlockPos.betweenClosed(start, end)) {
            BlockState blockState = region.getBlockState(blockPos);
            LatexCoverState latexCoverState = getLatexCoverState(region, blockPos);

            if (!latexCoverState.isPresent())
                continue;

            RenderType rendertype = RenderType.solid(); //ItemBlockRenderTypes.getRenderLayer(latexCoverState);
            BufferBuilder bufferbuilder = bufferBuilderPack.builder(rendertype);
            if (renderTypes.add(rendertype)) {
                beginLayer(bufferbuilder);
            }

            ChangedClient.latexCoveredBlocksRenderer.get().tesselate(
                    region,
                    LatexCoverGetter.extend(region, fetchPos -> this.getLatexCoverState(region, fetchPos)),
                    blockPos,
                    bufferbuilder,
                    blockState,
                    latexCoverState,
                    random);
        }
    }
}
