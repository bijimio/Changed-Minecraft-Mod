package net.ltxprogrammer.changed.world.features.structures.facility;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.random.WeightedEntry;

public class FacilityPieceCollectionBuilder {
    private final ImmutableList.Builder<WeightedEntry.Wrapper<FacilityPiece>> builder = ImmutableList.builder();

    public static final int WEIGHT_COMMON = 20;
    public static final int WEIGHT_UNCOMMON = 10;
    public static final int WEIGHT_RARE = 5;
    public static final int WEIGHT_VERY_RARE = 1;

    public FacilityPieceCollectionBuilder register(FacilityPiece piece) {
        this.register(WEIGHT_COMMON, piece);
        return this;
    }

    public FacilityPieceCollectionBuilder register(int weight, FacilityPiece piece) {
        builder.add(WeightedEntry.wrap(piece, weight));
        return this;
    }

    public FacilityPieceCollection build() {
        return new FacilityPieceCollection(builder);
    }
}
