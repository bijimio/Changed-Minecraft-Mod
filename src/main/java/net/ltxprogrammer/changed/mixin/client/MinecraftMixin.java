package net.ltxprogrammer.changed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.block.AlertingPuddle;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public Entity cameraEntity;

    @WrapMethod(method = "shouldEntityAppearGlowing")
    public boolean isEntityMovingOnWhiteLatex(Entity entity, Operation<Boolean> original) {
        if (!(entity instanceof LivingEntity livingEntity))
            return original.call(entity);
        if (this.cameraEntity == null)
            return original.call(entity);
        if (LatexType.getEntityLatexType(this.cameraEntity) != ChangedLatexTypes.WHITE_LATEX.get())
            return original.call(entity);
        if (LatexType.getEntityLatexType(livingEntity) == ChangedLatexTypes.WHITE_LATEX.get())
            return original.call(entity);

        BlockState standing = livingEntity.level().getBlockState(livingEntity.blockPosition().below());
        if (standing == null || standing.isAir())
            return original.call(entity);
        if (AbstractLatexBlock.isSurfaceOfType(entity.level(), livingEntity.blockPosition(), Direction.DOWN, ChangedLatexTypes.WHITE_LATEX.get()))
            return true;
        return original.call(entity);
    }

    @WrapMethod(method = "shouldEntityAppearGlowing")
    public boolean isEntityMovingOnAlertPuddle(Entity entity, Operation<Boolean> original) {
        if (!(entity instanceof LivingEntity livingEntity))
            return original.call(entity);
        if (this.cameraEntity == null)
            return original.call(entity);

        BlockState standing = livingEntity.level().getBlockState(livingEntity.blockPosition());
        if (standing.getBlock() instanceof AlertingPuddle alertingPuddle && alertingPuddle.shouldGlowLocally(livingEntity, this.cameraEntity))
            return true;
        standing = livingEntity.level().getBlockState(livingEntity.blockPosition().below());
        if (standing.getBlock() instanceof AlertingPuddle alertingPuddle && alertingPuddle.shouldGlowLocally(livingEntity, this.cameraEntity))
            return true;
        return original.call(entity);
    }
}
