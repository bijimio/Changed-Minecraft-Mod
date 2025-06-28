package net.ltxprogrammer.changed.mixin.compatibility.Personality;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamabnormals.personality.core.Personality;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AdvancedHumanoidRenderer.class, remap = false)
@RequiredMods("personality")
public abstract class AdvancedHumanoidRendererMixin {
    @Inject(
            method = "setupRotations(Lnet/ltxprogrammer/changed/entity/ChangedEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V",
            at = @At("HEAD"),
            remap = false
    )
    private void adjustSitPosition(ChangedEntity entity, PoseStack matrixStack, float f, float g, float h, CallbackInfo ci) {
        if (entity.maybeGetUnderlying() instanceof Player player) {
            if (Personality.SYNCED_SITTING_PLAYERS.contains(player.getUUID()))
                matrixStack.translate(0.0F, entity.getMyRidingOffset() - 0.25F, 0.0F);
        }
    }
}
