package net.ltxprogrammer.changed.world.features.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.init.ChangedStructureTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.IglooStructure;

import java.util.Optional;

public class Beehive extends Structure {
    public static final Codec<Beehive> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                settingsCodec(instance),
                ResourceLocation.CODEC.fieldOf("piece").forGetter(Beehive::getPiece)
        ).apply(instance, Beehive::new);
    });

    private final ResourceLocation piece;

    public Beehive(Structure.StructureSettings settings, ResourceLocation piece) {
        super(settings);
        this.piece = piece;
    }

    public ResourceLocation getPiece() {
        return piece;
    }

    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
        builder.addPiece(new SurfaceNBTPiece(this.getPiece(), null, context));
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> {
            generatePieces(builder, context);
        });
    }

    @Override
    public StructureType<?> type() {
        return ChangedStructureTypes.BEEHIVE.get();
    }
}
