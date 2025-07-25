package net.ltxprogrammer.changed.mixin.compatibility.Personality;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamabnormals.personality.core.other.PersonalityClientEvents;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.event.entity.EntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PersonalityClientEvents.class, remap = false)
@RequiredMods("personality")
public class PersonalityClientEventsMixin<T extends ChangedEntity> {
    @WrapOperation(method = "onEntitySize", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;STANDING_DIMENSIONS:Lnet/minecraft/world/entity/EntityDimensions;"))
    private static EntityDimensions applyPersonalitySizeEvent(Operation<EntityDimensions> original, @Local(argsOnly = true) EntityEvent.Size event) {
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(event.getEntity()))
                .map(variant -> variant.getTransfurDimensions(Pose.STANDING))
                .orElseGet(original::call);
    }
}
