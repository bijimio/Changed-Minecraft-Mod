package net.ltxprogrammer.changed.mixin.compatibility.Rubidium;

import com.llamalad7.mixinextras.sugar.Local;
import me.jellysquid.mods.sodium.client.gl.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildResult;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderBounds;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask;
import me.jellysquid.mods.sodium.client.render.vertex.type.ChunkVertexEncoder;
import me.jellysquid.mods.sodium.client.util.task.CancellationSource;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.rubidium.OptimizedVertexBuilder;
import net.ltxprogrammer.changed.extension.rubidium.WorldSliceExtension;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = ChunkRenderRebuildTask.class, remap = false)
@RequiredMods("rubidium")
public abstract class ChunkRenderRebuildTaskMixin {
    @Shadow @Final private RenderSection render;

    @Shadow @Final private XoroshiroRandomSource random;

    @Unique
    public LatexCoverState getLatexCoverState(WorldSlice slice, BlockPos blockPos) {
        return ((WorldSliceExtension)slice).getLatexCoverState(blockPos);
    }

    @Inject(method = "performBuild", at = @At(value = "INVOKE", target = "Ljava/util/EnumMap;<init>(Ljava/lang/Class;)V"))
    public void addChangedSteps(ChunkBuildContext buildContext, CancellationSource cancellationSource, CallbackInfoReturnable<ChunkBuildResult> cir,
                                @Local ChunkRenderBounds.Builder bounds) {
        ChunkBuildBuffers buffers = buildContext.buffers;
        WorldSlice slice = buildContext.cache.getWorldSlice();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos modelOffset = new BlockPos.MutableBlockPos();

        ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();
        Map<RenderType, OptimizedVertexBuilder> builderCache = new HashMap<>();

        int minX = this.render.getOriginX();
        int minY = this.render.getOriginY();
        int minZ = this.render.getOriginZ();
        int maxX = minX + 16;
        int maxY = minY + 16;
        int maxZ = minZ + 16;

        for(int y = minY; y < maxY; ++y) {
            if (cancellationSource.isCancelled()) {
                return;
            }

            for (int z = minZ; z < maxZ; ++z) {
                for (int x = minX; x < maxX; ++x) {
                    blockPos.set(x, y, z);

                    BlockState blockState = slice.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof LatexCoveringSource source)
                        source.getLatexCoverState(blockState, blockPos);
                    LatexCoverState latexCoverState = getLatexCoverState(slice, blockPos);

                    if (!latexCoverState.isPresent())
                        continue;

                    RenderType rendertype = RenderType.solid(); //ItemBlockRenderTypes.getRenderLayer(latexCoverState);

                    boolean rendered = ChangedClient.latexCoveredBlocksRenderer.get().tesselate(
                            slice,
                            fetchPos -> this.getLatexCoverState(slice, fetchPos),
                            blockPos,
                            builderCache.computeIfAbsent(rendertype, type -> new OptimizedVertexBuilder(vertices, buffers.get(rendertype))),
                            blockState,
                            latexCoverState,
                            this.random);

                    if (rendered)
                        bounds.addBlock(x & 15, y & 15, z & 15);
                }
            }
        }
    }
}
