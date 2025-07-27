package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.latex.IClientLatexTypeExtensions;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.latex.SpreadingLatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.NamedRenderTypeManager;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LatexCoveredBlocksRenderer implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation BLOCK_ATLAS = InventoryMenu.BLOCK_ATLAS;
    public static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("latex_cover_models");
    private static final ResourceLocation RENDERTYPE_SOLID = ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "solid");

    public static class ModelSet {
        @Nullable
        private final BakedModel surfaceTop;
        @Nullable
        private final BakedModel surfaceBottom;
        @Nullable
        private final BakedModel surfaceNorth;
        @Nullable
        private final BakedModel surfaceSouth;
        @Nullable
        private final BakedModel surfaceEast;
        @Nullable
        private final BakedModel surfaceWest;
        @Nullable
        private final BakedModel extra;

        public ModelSet(@Nullable BakedModel surfaceTop,
                        @Nullable BakedModel surfaceBottom,
                        @Nullable BakedModel surfaceNorth,
                        @Nullable BakedModel surfaceSouth,
                        @Nullable BakedModel surfaceEast,
                        @Nullable BakedModel surfaceWest,
                        @Nullable BakedModel extra) {
            this.surfaceTop = surfaceTop;
            this.surfaceBottom = surfaceBottom;
            this.surfaceNorth = surfaceNorth;
            this.surfaceSouth = surfaceSouth;
            this.surfaceEast = surfaceEast;
            this.surfaceWest = surfaceWest;
            this.extra = extra;
        }

        @Nullable
        public BakedModel getModel(Direction surface) {
            return switch (surface) {
                case UP -> surfaceTop;
                case DOWN -> surfaceBottom;
                case NORTH -> surfaceNorth;
                case SOUTH -> surfaceSouth;
                case EAST -> surfaceEast;
                case WEST -> surfaceWest;
            };
        }

        @Nullable
        public TextureAtlasSprite getParticleIcon() {
            return surfaceTop == null ? null : surfaceTop.getParticleIcon(ModelData.EMPTY);
        }

        @Nullable
        public BakedModel getExtraModel() {
            return extra;
        }
    }

    private final Minecraft minecraft;
    private final BlockRenderDispatcher dispatcher;
    private final ModelBlockRenderer modelRenderer;
    private Map<LatexType, ModelSet> defaultModelSets;
    private Map<BlockState, Map<LatexType, ModelSet>> specialModelSets = new HashMap<>();

    public LatexCoveredBlocksRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.dispatcher = minecraft.getBlockRenderer();
        this.modelRenderer = dispatcher.getModelRenderer();
    }

    private ModelSet getModelSet(BlockState blockState, LatexCoverState coverState) {
        return specialModelSets.getOrDefault(blockState, defaultModelSets).get(coverState.getType());
    }

    public RenderType getRenderType(LatexCoverState coverState) {
        // Maybe use a tag
        if (ChangedClient.shouldBeRenderingWaveVision() && ChangedLatexTypes.DARK_LATEX.get().isFriendlyTo(coverState.getType()))
            return ChangedShaders.waveVisionResonantSolid(WaveVisionRenderer.LATEX_RESONANCE_NEUTRAL);
        return RenderType.solid();
    }

    private boolean wrappedTesselate(
            BlockAndTintGetter level, LatexCoverGetter latexCoverGetter,
            BlockPos blockPos, VertexConsumer bufferBuilder,
            BlockState blockState, LatexCoverState coverState,
            RandomSource random) {
        final ModelSet modelSet = getModelSet(blockState, coverState);

        if (blockState.isCollisionShapeFullBlock(level, blockPos))
            return false;

        int blockX0 = blockPos.getX() & 15;
        int blockY0 = blockPos.getY() & 15;
        int blockZ0 = blockPos.getZ() & 15;

        int lightColor = this.getLightColor(level, blockPos);

        boolean surfaceTop = coverState.getProperties().contains(SpreadingLatexType.UP) && coverState.getValue(SpreadingLatexType.UP);
        boolean surfaceBottom = coverState.getProperties().contains(SpreadingLatexType.DOWN) && coverState.getValue(SpreadingLatexType.DOWN);
        boolean surfaceNorth = coverState.getProperties().contains(SpreadingLatexType.NORTH) && coverState.getValue(SpreadingLatexType.NORTH);
        boolean surfaceSouth = coverState.getProperties().contains(SpreadingLatexType.SOUTH) && coverState.getValue(SpreadingLatexType.SOUTH);
        boolean surfaceEast = coverState.getProperties().contains(SpreadingLatexType.EAST) && coverState.getValue(SpreadingLatexType.EAST);
        boolean surfaceWest = coverState.getProperties().contains(SpreadingLatexType.WEST) && coverState.getValue(SpreadingLatexType.WEST);

        PoseStack poseStack = new PoseStack();
        poseStack.translate(blockX0, blockY0, blockZ0);

        long seed = coverState.getSeed(blockPos);

        final RenderType renderType = this.getRenderType(coverState);

        if (surfaceTop && modelSet.surfaceTop != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceTop, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (surfaceBottom && modelSet.surfaceBottom != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceBottom, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (surfaceNorth && modelSet.surfaceNorth != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceNorth, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (surfaceSouth && modelSet.surfaceSouth != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceSouth, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (surfaceEast && modelSet.surfaceEast != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceEast, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (surfaceWest && modelSet.surfaceWest != null) {
            modelRenderer.tesselateWithAO(level, modelSet.surfaceWest, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

        if (modelSet.extra != null) {
            modelRenderer.tesselateWithAO(level, modelSet.extra, blockState, blockPos, poseStack, bufferBuilder, true, random, seed, lightColor,
                    ModelData.EMPTY, renderType);
        }

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

    private static void vertex(VertexConsumer consumer, double x, double y, double z, float red, float green, float blue, float alpha, float texCoordU, float texCoordV, int packedLight) {
        consumer.vertex(x, y, z).color(red, green, blue, alpha).uv(texCoordU, texCoordV).uv2(packedLight).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    public boolean tesselate(
            BlockAndTintGetter level, LatexCoverGetter latexCoverGetter,
            BlockPos blockPos, VertexConsumer bufferBuilder,
            BlockState blockState, LatexCoverState coverState,
            RandomSource random) {
        try {
            return this.wrappedTesselate(level, latexCoverGetter, blockPos, bufferBuilder, blockState, coverState, random);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating latex cover in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, level, blockPos, (BlockState)null);
            throw new ReportedException(crashreport);
        }
    }

    private static IModelBuilder<?> modelBuilderFor(TextureAtlasSprite particle) {
        return IModelBuilder.of(true, true, true,
                ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY,
                particle,
                NamedRenderTypeManager.get(RENDERTYPE_SOLID));
    }

    private static CompletableFuture<Map<ResourceLocation, BlockModel>> loadBlockModels(ResourceManager resources, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            return MODEL_LISTER.listMatchingResources(resources);
        }, executor).thenCompose((namedResources) -> {
            List<CompletableFuture<Pair<ResourceLocation, BlockModel>>> list = new ArrayList<>(namedResources.size());

            for (Map.Entry<ResourceLocation, Resource> entry : namedResources.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    try (Reader reader = entry.getValue().openAsReader()) {
                        return Pair.of(MODEL_LISTER.fileToId(entry.getKey()), BlockModel.fromStream(reader));
                    } catch (Exception exception) {
                        LOGGER.error("Failed to load model {}", entry.getKey(), exception);
                        return null;
                    }
                }, executor));
            }

            return Util.sequence(list).thenApply((result) -> {
                return result.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
            });
        });
    }

    private static Stream<LatexType> getCoverTypes() {
        return ChangedRegistry.LATEX_TYPE.get().getValues().stream()
                .filter(type -> type != ChangedLatexTypes.NONE.get());
    }

    private static CompletableFuture<Map<ResourceLocation, Map<LatexType, BakedModel>>> bakeModels(Function<ResourceLocation, TextureAtlasSprite> getSprite,
                                                                                   Map<ResourceLocation, BlockModel> unbakedModels,
                                                                                   Executor executor) {
        final var modelBakes = unbakedModels.entrySet().stream().map(entry -> {
            final var stateBake = getCoverTypes()
                    .map(type -> {
                        final var properties = IClientLatexTypeExtensions.of(type);

                        return CompletableFuture.supplyAsync(() -> modelBuilderFor(getSprite.apply(properties.getTextureForParticle())), executor)
                                .thenApply(builder -> {
                                    entry.getValue().getElements().forEach(blockElement -> {
                                        blockElement.faces.forEach((side, face) -> {
                                            var sprite = getSprite.apply(properties.getTextureForFace(side));
                                            var quad = UnbakedGeometryHelper.bakeElementFace(blockElement, face, sprite, side, BlockModelRotation.X0_Y0, entry.getKey());
                                            if (face.cullForDirection == null)
                                                builder.addUnculledFace(quad);
                                            else
                                                builder.addCulledFace(face.cullForDirection, quad);

                                        });
                                    });

                                    return Pair.of(type, builder.build());
                                });
                    }).toList();

            return Util.sequence(stateBake).thenApply((result) -> {
                return Pair.of(entry.getKey(), result.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
            });
        }).toList();

        return Util.sequence(modelBakes).thenApply((result) -> {
            return result.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
        });
    }

    private static final ResourceLocation DEFAULT_TOP = Changed.modResource("default_top");
    private static final ResourceLocation DEFAULT_BOTTOM = Changed.modResource("default_bottom");
    private static final ResourceLocation DEFAULT_NORTH = Changed.modResource("default_north");
    private static final ResourceLocation DEFAULT_SOUTH = Changed.modResource("default_south");
    private static final ResourceLocation DEFAULT_EAST = Changed.modResource("default_east");
    private static final ResourceLocation DEFAULT_WEST = Changed.modResource("default_west");
    private static final ResourceLocation DEFAULT_EXTRA = Changed.modResource("default_extra");

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resources, ProfilerFiller profilerA, ProfilerFiller profilerB, Executor backgroundExecutor, Executor minecraftExecutor) {
        return loadBlockModels(resources, backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenCompose(unbakedModels -> {
                    return bakeModels(minecraft.getTextureAtlas(BLOCK_ATLAS),
                            unbakedModels, minecraftExecutor);
                })
                .thenApply(bakedModels -> {
                    this.defaultModelSets = getCoverTypes().collect(Collectors.toUnmodifiableMap(Function.identity(),
                            state -> new ModelSet(
                                    bakedModels.getOrDefault(DEFAULT_TOP, Map.of()).get(state),
                                    bakedModels.getOrDefault(DEFAULT_BOTTOM, Map.of()).get(state),
                                    bakedModels.getOrDefault(DEFAULT_NORTH, Map.of()).get(state),
                                    bakedModels.getOrDefault(DEFAULT_SOUTH, Map.of()).get(state),
                                    bakedModels.getOrDefault(DEFAULT_EAST, Map.of()).get(state),
                                    bakedModels.getOrDefault(DEFAULT_WEST, Map.of()).get(state),
                                    bakedModels.getOrDefault(DEFAULT_EXTRA, Map.of()).get(state)
                            )));

                    return bakedModels;
                })
                .thenAcceptAsync(bakedModels -> {
                    // TODO load special models to attach to blockstates
                }, minecraftExecutor);
    }
}
