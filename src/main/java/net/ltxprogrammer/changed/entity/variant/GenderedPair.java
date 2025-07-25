package net.ltxprogrammer.changed.entity.variant;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.util.RandomSource;

import java.util.Random;
import java.util.function.Supplier;

public class GenderedPair<M extends ChangedEntity, F extends ChangedEntity> {
    private final Supplier<? extends TransfurVariant<M>> maleVariant;
    private final Supplier<? extends TransfurVariant<F>> femaleVariant;

    public GenderedPair(Supplier<? extends TransfurVariant<M>> maleVariant, Supplier<? extends TransfurVariant<F>> femaleVariant) {
        this.maleVariant = maleVariant;
        this.femaleVariant = femaleVariant;
    }

    public TransfurVariant<M> getMaleVariant() {
        return maleVariant.get();
    }

    public TransfurVariant<F> getFemaleVariant() {
        return femaleVariant.get();
    }

    public TransfurVariant<?> getRandomVariant(RandomSource random) {
        return random.nextBoolean() ? getFemaleVariant() : getMaleVariant();
    }

    public boolean contains(TransfurVariant<?> variant) {
        return maleVariant.get() == variant || femaleVariant.get() == variant;
    }
}
