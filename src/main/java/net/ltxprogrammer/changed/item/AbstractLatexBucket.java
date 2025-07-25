package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class AbstractLatexBucket extends BucketItem {
    public final Supplier<? extends AbstractLatexFluid> fluid;
    public final Supplier<? extends LatexType> latexType;

    public AbstractLatexBucket(Supplier<? extends AbstractLatexFluid> supplier, Supplier<? extends LatexType> latexType) {
        super(supplier, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON));
        this.fluid = supplier;
        this.latexType = latexType;
    }

    public static Supplier<AbstractLatexBucket> from(Supplier<? extends AbstractLatexFluid> fluid, Supplier<? extends LatexType> latexType) {
        return () -> new AbstractLatexBucket(fluid, latexType);
    }

    public LatexType getLatexType() {
        return latexType.get();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidBucketWrapper(stack);
    }
}
