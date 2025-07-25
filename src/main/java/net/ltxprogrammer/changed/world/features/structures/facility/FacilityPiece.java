package net.ltxprogrammer.changed.world.features.structures.facility;

import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class FacilityPiece {
    public final PieceType type;

    protected FacilityPiece(PieceType type) {
        this.type = type;
    }

    public abstract WeightedRandomList<WeightedEntry.Wrapper<PieceType>> getValidNeighbors(FacilityGenerationStack stack);
    public abstract FacilityPieceInstance createStructurePiece(StructureTemplateManager structures, int genDepth);
}
