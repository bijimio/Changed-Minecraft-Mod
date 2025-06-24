package net.ltxprogrammer.changed.world.features.structures.facility;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class GatherFacilityPiecesEvent extends Event implements IModBusEvent {
    private final PieceType pieceType;
    private final FacilityPieceCollectionBuilder builder;

    public GatherFacilityPiecesEvent(PieceType pieceType, FacilityPieceCollectionBuilder builder) {
        this.pieceType = pieceType;
        this.builder = builder;
    }

    public GatherFacilityPiecesEvent register(FacilityPiece piece) {
        builder.register(piece);
        return this;
    }

    public GatherFacilityPiecesEvent register(int weight, FacilityPiece piece) {
        builder.register(weight, piece);
        return this;
    }

    public FacilityPieceCollectionBuilder getBuilder() {
        return builder;
    }
}
