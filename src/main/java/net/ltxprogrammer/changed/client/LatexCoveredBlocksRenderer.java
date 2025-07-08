package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.LevelChunkSectionExtension;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LatexCoveredBlocksRenderer implements PreparableReloadListener {
    private final Minecraft minecraft;
    private static final ResourceLocation BLOCK_ATLAS = InventoryMenu.BLOCK_ATLAS;
    private static final ResourceLocation DARK_LATEX_TEXTURE = Changed.modResource("block/dark_latex_block_top");

    public LatexCoveredBlocksRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    private boolean wrappedTesselate(BlockAndTintGetter level, LatexCoverGetter latexCoverGetter, BlockPos blockPos, VertexConsumer bufferBuilder, BlockState blockState, LatexCoverState coverState) {
        TextureAtlasSprite sprite = minecraft.getTextureAtlas(BLOCK_ATLAS).apply(DARK_LATEX_TEXTURE);
        float alpha = 1.0f;// = (float)(i >> 24 & 255) / 255.0F;
        float red = 1.0f;// = (float)(i >> 16 & 255) / 255.0F;
        float green = 1.0f;// = (float)(i >> 8 & 255) / 255.0F;
        float blue = 1.0f;// = (float)(i & 255) / 255.0F;

        if (blockState.isCollisionShapeFullBlock(level, blockPos))
            return false;

        int blockX0 = blockPos.getX() & 15;
        int blockY0 = blockPos.getY() & 15;
        int blockZ0 = blockPos.getZ() & 15;
        int blockX1 = blockX0 + 1;
        int blockY1 = blockY0 + 1;
        int blockZ1 = blockZ0 + 1;

        int lightColor = this.getLightColor(level, blockPos);

        BlockPos posUp = blockPos.relative(Direction.UP);
        BlockPos posDown = blockPos.relative(Direction.DOWN);
        BlockPos posNorth = blockPos.relative(Direction.NORTH);
        BlockPos posSouth = blockPos.relative(Direction.SOUTH);
        BlockPos posEast = blockPos.relative(Direction.EAST);
        BlockPos posWest = blockPos.relative(Direction.WEST);

        BlockState blockUp = level.getBlockState(posUp);
        boolean surfaceTop = blockUp.isFaceSturdy(level, posUp, Direction.DOWN, SupportType.FULL);
        BlockState blockDown = level.getBlockState(posDown);
        boolean surfaceBottom = blockDown.isFaceSturdy(level, posDown, Direction.UP, SupportType.FULL);
        BlockState blockNorth = level.getBlockState(posNorth);
        boolean surfaceNorth = blockNorth.isFaceSturdy(level, posNorth, Direction.SOUTH, SupportType.FULL);
        BlockState blockSouth = level.getBlockState(posSouth);
        boolean surfaceSouth = blockSouth.isFaceSturdy(level, posSouth, Direction.NORTH, SupportType.FULL);
        BlockState blockEast = level.getBlockState(posEast);
        boolean surfaceEast = blockEast.isFaceSturdy(level, posEast, Direction.WEST, SupportType.FULL);
        BlockState blockWest = level.getBlockState(posWest);
        boolean surfaceWest = blockWest.isFaceSturdy(level, posWest, Direction.EAST, SupportType.FULL);

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        
        double height = 2.0d / 16.0d;

        if (surfaceTop) {
            vertex(bufferBuilder, blockX1, blockY1 - height, blockZ0, red, green, blue, alpha, u0, v0, lightColor);
            vertex(bufferBuilder, blockX1, blockY1 - height, blockZ1, red, green, blue, alpha, u0, v1, lightColor);
            vertex(bufferBuilder, blockX0, blockY1 - height, blockZ1, red, green, blue, alpha, u1, v1, lightColor);
            vertex(bufferBuilder, blockX0, blockY1 - height, blockZ0, red, green, blue, alpha, u1, v0, lightColor);
        }

        if (surfaceBottom) {
            vertex(bufferBuilder, blockX0, blockY0 + height, blockZ0, red, green, blue, alpha, u0, v0, lightColor);
            vertex(bufferBuilder, blockX0, blockY0 + height, blockZ1, red, green, blue, alpha, u0, v1, lightColor);
            vertex(bufferBuilder, blockX1, blockY0 + height, blockZ1, red, green, blue, alpha, u1, v1, lightColor);
            vertex(bufferBuilder, blockX1, blockY0 + height, blockZ0, red, green, blue, alpha, u1, v0, lightColor);
        }

        if (surfaceNorth) {
            vertex(bufferBuilder, blockX0, blockY1, blockZ0 + height, red, green, blue, alpha, u0, v1, lightColor);
            vertex(bufferBuilder, blockX0, blockY0, blockZ0 + height, red, green, blue, alpha, u0, v0, lightColor);
            vertex(bufferBuilder, blockX1, blockY0, blockZ0 + height, red, green, blue, alpha, u1, v0, lightColor);
            vertex(bufferBuilder, blockX1, blockY1, blockZ0 + height, red, green, blue, alpha, u1, v1, lightColor);
        }

        if (surfaceSouth) {
            vertex(bufferBuilder, blockX0, blockY0, blockZ1 - height, red, green, blue, alpha, u0, v0, lightColor);
            vertex(bufferBuilder, blockX0, blockY1, blockZ1 - height, red, green, blue, alpha, u0, v1, lightColor);
            vertex(bufferBuilder, blockX1, blockY1, blockZ1 - height, red, green, blue, alpha, u1, v1, lightColor);
            vertex(bufferBuilder, blockX1, blockY0, blockZ1 - height, red, green, blue, alpha, u1, v0, lightColor);
        }

        if (surfaceEast) {
            vertex(bufferBuilder, blockX1 - height, blockY1, blockZ0, red, green, blue, alpha, u0, v1, lightColor);
            vertex(bufferBuilder, blockX1 - height, blockY0, blockZ0, red, green, blue, alpha, u0, v0, lightColor);
            vertex(bufferBuilder, blockX1 - height, blockY0, blockZ1, red, green, blue, alpha, u1, v0, lightColor);
            vertex(bufferBuilder, blockX1 - height, blockY1, blockZ1, red, green, blue, alpha, u1, v1, lightColor);
        }

        if (surfaceWest) {
            vertex(bufferBuilder, blockX0 + height, blockY0, blockZ0, red, green, blue, alpha, u0, v0, lightColor);
            vertex(bufferBuilder, blockX0 + height, blockY1, blockZ0, red, green, blue, alpha, u0, v1, lightColor);
            vertex(bufferBuilder, blockX0 + height, blockY1, blockZ1, red, green, blue, alpha, u1, v1, lightColor);
            vertex(bufferBuilder, blockX0 + height, blockY0, blockZ1, red, green, blue, alpha, u1, v0, lightColor);
        }

        /*LatexCoverState coverUp = latexCoverGetter.getLatexCover(posUp);
        LatexCoverState coverDown = latexCoverGetter.getLatexCover(posDown);
        LatexCoverState coverNorth = latexCoverGetter.getLatexCover(posNorth);
        LatexCoverState coverSouth = latexCoverGetter.getLatexCover(posSouth);
        LatexCoverState coverEast = latexCoverGetter.getLatexCover(posEast);
        LatexCoverState coverWest = latexCoverGetter.getLatexCover(posWest);*/

        return true;
    }

    private int getLightColor(BlockAndTintGetter level, BlockPos blockPos) {
        return LevelRenderer.getLightColor(level, blockPos);
        /*int lightColor = LevelRenderer.getLightColor(level, blockPos);
        int lightColorAbove = LevelRenderer.getLightColor(level, blockPos.above());
        int k = lightColor & 255;
        int l = lightColorAbove & 255;
        int i1 = lightColor >> 16 & 255;
        int j1 = lightColorAbove >> 16 & 255;
        return (Math.max(k, l)) | (Math.max(i1, j1)) << 16;*/
    }

    private void vertex(VertexConsumer consumer, double x, double y, double z, float red, float green, float blue, float alpha, float texCoordU, float texCoordV, int packedLight) {
        consumer.vertex(x, y, z).color(red, green, blue, alpha).uv(texCoordU, texCoordV).uv2(packedLight).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    public boolean tesselate(BlockAndTintGetter level, LatexCoverGetter latexCoverGetter, BlockPos blockPos, VertexConsumer bufferBuilder, BlockState blockState, LatexCoverState coverState) {
        try {
            return this.wrappedTesselate(level, latexCoverGetter, blockPos, bufferBuilder, blockState, coverState);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating latex cover in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, level, blockPos, (BlockState)null);
            throw new ReportedException(crashreport);
        }
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resources, ProfilerFiller profilerA, ProfilerFiller profilerB, Executor backgroundExecutor, Executor minecraftExecutor) {
        // TODO load general and specialized models.
        return CompletableFuture.supplyAsync(() -> null, backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(object -> {}, minecraftExecutor);
    }
}
