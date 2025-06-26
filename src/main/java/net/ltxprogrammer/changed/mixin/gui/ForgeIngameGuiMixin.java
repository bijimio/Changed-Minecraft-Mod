package net.ltxprogrammer.changed.mixin.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeGui.class, remap = false)
public abstract class ForgeIngameGuiMixin extends Gui {
    public ForgeIngameGuiMixin(Minecraft p_232355_, ItemRenderer p_232356_) {
        super(p_232355_, p_232356_);
    }

    @Inject(method = "renderAir", at = @At("HEAD"), cancellable = true)
    protected void renderAir(int width, int height, GuiGraphics guiGraphics, CallbackInfo callback) {
        var entity = Minecraft.getInstance().getCameraEntity();
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity), (player, variant) -> {
            if (variant.breatheMode.canBreatheWater() && player.getAirSupply() >= 300)
                callback.cancel();
        });
    }
}
