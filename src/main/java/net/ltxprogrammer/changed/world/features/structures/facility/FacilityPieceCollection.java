package net.ltxprogrammer.changed.world.features.structures.facility;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.random.WeightedRandomList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FacilityPieceCollection {
    private final ImmutableList<WeightedEntry.Wrapper<FacilityPiece>> pieces;
    private final int totalWeight;

    FacilityPieceCollection(ImmutableList.Builder<WeightedEntry.Wrapper<FacilityPiece>> pieces) {
        this.pieces = pieces.build();
        this.totalWeight = WeightedRandom.getTotalWeight(this.pieces);
    }

    boolean contains(FacilityPiece facilityPiece) {
        return pieces.stream().map(WeightedEntry.Wrapper::getData).anyMatch(facilityPiece::equals);
    }

    public Stream<FacilityPiece> stream() {
        return pieces.stream().map(WeightedEntry.Wrapper::getData);
    }

    public Stream<FacilityPiece> shuffledStream(RandomSource random) {
        if (this.totalWeight == 0) {
            return Stream.empty();
        } else {
            final var first = WeightedRandom.getWeightedItem(this.pieces, random.nextInt(this.totalWeight));
            if (first.isEmpty())
                return Stream.empty();

            return Stream.<Pair<Optional<WeightedEntry.Wrapper<FacilityPiece>>, List<WeightedEntry.Wrapper<FacilityPiece>>>>iterate(Pair.of(first, this.pieces),
                    pair -> !pair.getSecond().isEmpty() && pair.getFirst().isPresent(),
                    pair -> {
                        var remaining = new ArrayList<>(pair.getSecond());
                        remaining.remove(pair.getFirst().orElseThrow());

                        return Pair.of(WeightedRandom.getWeightedItem(this.pieces, random.nextInt(this.totalWeight)), remaining);
                    }).map(Pair::getFirst).filter(Optional::isPresent).map(Optional::get).map(WeightedEntry.Wrapper::getData);
        }
    }

    public Optional<FacilityPiece> findNextPiece(RandomSource random) {
        if (this.totalWeight == 0) {
            return Optional.empty();
        } else {
            int i = random.nextInt(this.totalWeight);
            return WeightedRandom.getWeightedItem(this.pieces, i).map(WeightedEntry.Wrapper::getData);
        }
    }
}
