package net.ltxprogrammer.changed.world.features.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.init.ChangedStructureTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;
import java.util.function.Predicate;

public class DecayedLab extends Structure {
    public static final Codec<DecayedLab> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                settingsCodec(instance),
                ResourceLocation.CODEC.fieldOf("piece").forGetter(DecayedLab::getPiece),
                ResourceLocation.CODEC.fieldOf("loot_table").forGetter(DecayedLab::getLootTable)
        ).apply(instance, DecayedLab::new);
    });

    private final ResourceLocation piece;
    private final ResourceLocation lootTable;

    public DecayedLab(Structure.StructureSettings settings, ResourceLocation piece, ResourceLocation lootTable) {
        super(settings);
        this.piece = piece;
        this.lootTable = lootTable;
    }

    public ResourceLocation getPiece() {
        return piece;
    }

    public ResourceLocation getLootTable() {
        return lootTable;
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
        return ChangedStructureTypes.DECAYED_LAB.get();
    }
}
