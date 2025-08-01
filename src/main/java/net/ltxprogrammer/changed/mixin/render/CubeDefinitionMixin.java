package net.ltxprogrammer.changed.mixin.render;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.client.CubeDefinitionExtender;
import net.ltxprogrammer.changed.client.CubeExtender;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mixin(CubeDefinition.class)
public abstract class CubeDefinitionMixin implements CubeDefinitionExtender {
    @Unique
    private Set<Pair<Direction, Direction>> copyUVStarts = null;
    @Unique
    private Set<Direction> removedDirections = null;

    @Override
    public void removeFaces(Direction... directions) {
        if (removedDirections == null)
            removedDirections = new HashSet<>();

        removedDirections.addAll(Arrays.asList(directions));
    }

    @Override
    public void copyFaceUVStart(Direction from, Direction to) {
        if (copyUVStarts == null)
            copyUVStarts = new HashSet<>();

        copyUVStarts.add(Pair.of(from, to));
    }

    @Inject(method = "bake", at = @At("RETURN"))
    public void bakeWithExtra(int u, int v, CallbackInfoReturnable<ModelPart.Cube> cubeCallback) {
        if (copyUVStarts != null) {
            CubeExtender cubeExtender = (CubeExtender) cubeCallback.getReturnValue();
            cubeExtender.copyUVStarts(copyUVStarts);
        }

        if (removedDirections != null) {
            CubeExtender cubeExtender = (CubeExtender) cubeCallback.getReturnValue();
            cubeExtender.removeSides(removedDirections);
        }
    }
}
