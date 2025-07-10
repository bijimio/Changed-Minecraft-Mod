package net.ltxprogrammer.changed.mixin.compatibility.PresenceFootsteps;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.ha3.presencefootsteps.world.PFSolver;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PFSolver.class, remap = false)
@RequiredMods("presencefootsteps")
public abstract class PFSolverMixin {
    @WrapOperation(method = "findAssociation(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/AABB;)Leu/ha3/presencefootsteps/world/Association;",
        at = @At(value = "INVOKE",
                target = "Leu/ha3/presencefootsteps/world/PFSolver;getBlockStateAt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                ordinal = 0))
    public BlockState overrideLatexCover(PFSolver instance, Entity entity, BlockPos pos, Operation<BlockState> original) {
        final Block effectBlock = AbstractLatexBlock.getSurfaceType(entity.level(), pos.above(), Direction.DOWN).getBlock();
        if (effectBlock != null)
            return effectBlock.defaultBlockState();
        else
            return original.call(instance, entity, pos);
    }
}
