package net.ltxprogrammer.changed.mixin.compatibility.Personality;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.teamabnormals.personality.common.CommonEvents;
import com.teamabnormals.personality.core.Personality;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(value = TransfurVariantInstance.class, remap = false)
@RequiredMods("personality")
public class TransfurVariantInstanceMixin<T extends ChangedEntity> {
    @WrapMethod(method = "onSizeEvent")
    private static void applyPersonalitySizeEvent(EntityEvent.Size event, Operation<Void> original) {
        Entity entity = event.getEntity();
        if (event.getEntity() instanceof Player) {
            Player player = (Player) entity;
            if ((Personality.SITTING_PLAYERS.contains(player.getUUID()) || Personality.SYNCED_SITTING_PLAYERS.contains(player.getUUID())) && CommonEvents.testSit(player)) {
                return; // Allow personality to handle event size
            }

        }

        original.call(event);
    }
}
