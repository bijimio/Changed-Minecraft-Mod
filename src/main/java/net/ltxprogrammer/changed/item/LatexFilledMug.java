package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class LatexFilledMug extends FilledMug {
    protected final Supplier<? extends LatexType> latexType;

    public LatexFilledMug(Supplier<? extends LatexType> latexType, Properties properties) {
        super(properties);
        this.latexType = latexType;
    }

    @Override
    protected void onDrink(ItemStack stack, Level level, LivingEntity user) {
        ProcessTransfur.progressTransfur(user, 22.0f,
                latexType.get().getTransfurVariant(TransfurCause.ATE_LATEX, level.random),
                TransfurContext.hazard(TransfurCause.ATE_LATEX));
    }
}
