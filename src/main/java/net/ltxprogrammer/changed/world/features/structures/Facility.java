package net.ltxprogrammer.changed.world.features.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.List;
import java.util.Optional;

public class Facility extends Structure {
    public static final int GENERATION_CHUNK_RADIUS = 6;

    public static final Codec<Facility> CODEC = simpleCodec(Facility::new);

    public Facility(Structure.StructureSettings settings) {
        super(settings);
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(GenerationContext context) {
        Rotation rotation = Rotation.getRandom(context.random());
        BlockPos blockpos = this.getLowestYIn5by5BoxOffset7Blocks(context, rotation);
        return blockpos.getY() < 60 ? Optional.empty() : Optional.of(new Structure.GenerationStub(blockpos, (builder) -> {
            this.generatePieces(builder, context, blockpos, rotation);
        }));
    }

    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context, BlockPos blockPos, Rotation rotation) {
        ChunkPos center = context.chunkPos();
        ChunkPos min = new ChunkPos(center.x - GENERATION_CHUNK_RADIUS, center.z - GENERATION_CHUNK_RADIUS);
        ChunkPos max = new ChunkPos(center.x + GENERATION_CHUNK_RADIUS, center.z + GENERATION_CHUNK_RADIUS);
        BlockPos minPos = new BlockPos(min.getMinBlockX(), context.heightAccessor().getMinBuildHeight(), min.getMinBlockZ());
        BlockPos maxPos = new BlockPos(max.getMaxBlockX(), context.heightAccessor().getMaxBuildHeight(), max.getMaxBlockZ());

        BoundingBox generationRegion = BoundingBox.fromCorners(minPos, maxPos);

        FacilityPieces.generateFacility(builder, context, 5, 25, generationRegion);
        Changed.LOGGER.info("Generated facility with {} pieces, at ChunkPos {}", ((StructurePiecesBuilderExtender)builder).pieceCount(), center);
    }

    @Override
    public StructureType<?> type() {
        return ChangedStructureTypes.FACILITY.get();
    }
}
