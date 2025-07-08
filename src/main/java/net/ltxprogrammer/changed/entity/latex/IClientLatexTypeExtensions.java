package net.ltxprogrammer.changed.entity.latex;

import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;

public interface IClientLatexTypeExtensions {
    IClientLatexTypeExtensions DEFAULT = new IClientLatexTypeExtensions() { };

    static IClientLatexTypeExtensions of(LatexCoverState state) {
        return of(state.getType());
    }

    static IClientLatexTypeExtensions of(LatexType type) {
        return type.getRenderPropertiesInternal() instanceof IClientLatexTypeExtensions props ? props : DEFAULT;
    }

    default ResourceLocation getTextureForFace(LatexCoverState state, Direction face) {
        return null;
    }

    default ResourceLocation getTextureForParticle(LatexCoverState state) {
        return getTextureForFace(state, Direction.UP);
    }
}
