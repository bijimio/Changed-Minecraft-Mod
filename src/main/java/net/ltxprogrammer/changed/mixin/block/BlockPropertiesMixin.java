package net.ltxprogrammer.changed.mixin.block;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Function;

@Mixin(BlockBehaviour.Properties.class)
public abstract class BlockPropertiesMixin {
    @Shadow Function<BlockState, MapColor> mapColor;

    @Unique
    private static LatexType getTypeOrNeutral(BlockState state) {
        @Nullable var properties = state.getValues();
        if (properties != null && properties.containsKey(AbstractLatexBlock.COVERED))
            return state.getValue(AbstractLatexBlock.COVERED);
        else {
            if (properties == null)
                Changed.LOGGER.warn("BlockState has null properties! {}", state.getBlock().getClass());

            return LatexType.NEUTRAL;
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initDirectColor(CallbackInfo ci) {
        var oldFunc = mapColor;
        mapColor = blockState -> {
            var latex = getTypeOrNeutral(blockState);
            if (latex != LatexType.NEUTRAL)
                return latex.mapColor; // override color
            else
                return oldFunc.apply(blockState);
        };
    }
}
