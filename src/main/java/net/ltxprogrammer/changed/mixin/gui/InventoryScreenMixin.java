package net.ltxprogrammer.changed.mixin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.AbstractRadialScreen;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.network.packet.AccessorySyncPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
    @Shadow private float xMouse;
    @Shadow private float yMouse;
    @Unique private static final ResourceLocation LATEX_INVENTORY_LOCATION = Changed.modResource("textures/gui/latex_inventory.png");
    @Unique private static final ResourceLocation ACCESSORY_ICON = Changed.modResource("textures/gui/basic_player_info.png");

    public InventoryScreenMixin(InventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void addAccessoryButton(CallbackInfo ci) {
        this.addRenderableWidget(new ImageButton(this.leftPos - 24, this.height / 2 - 22, 20, 20, 0, 0, 20, ACCESSORY_ICON, 20, 40, (button) -> {
            AccessorySlots.openAccessoriesMenu(menu.owner);
        }));
    }

    @Inject(method = "renderLabels", at = @At("HEAD"), cancellable = true)
    protected void renderLabels(GuiGraphics graphics, int p_98890_, int p_98891_, CallbackInfo callback) {
        if (this.minecraft == null)
            return;
        if (!Changed.config.client.useGoopyInventory.get())
            return;

        ProcessTransfur.ifPlayerTransfurred(this.minecraft.player, variant -> {
            if (ProcessTransfur.isPlayerNotLatex(this.minecraft.player))
                return;

            var colorPair = AbstractRadialScreen.getColors(variant);
            var primary = colorPair.background();
            var secondary = colorPair.foreground();
            int textColor = primary.brightness() > 0.5f ? 0x0 : 0xffffff;
            if (Mth.abs(secondary.brightness() - primary.brightness()) > 0.1f)
                textColor = secondary.toInt();

            graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, textColor);
            callback.cancel();
        });
    }

    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    protected void renderBg(GuiGraphics graphics, float p_98871_, int p_98872_, int p_98873_, CallbackInfo callback) {
        if (this.minecraft == null)
            return;
        if (!Changed.config.client.useGoopyInventory.get())
            return;

        ProcessTransfur.ifPlayerTransfurred(this.minecraft.player, variant -> {
            if (ProcessTransfur.isPlayerNotLatex(this.minecraft.player))
                return;

            int i = this.leftPos;
            int j = this.topPos;

            var colorPair = AbstractRadialScreen.getColors(variant);
            var primary = colorPair.background();
            var secondary = colorPair.foreground();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, LATEX_INVENTORY_LOCATION);

            graphics.setColor(secondary.red(), secondary.green(), secondary.blue(), 1.0F);
            graphics.blit(LATEX_INVENTORY_LOCATION, i, j, 256, 0, this.imageWidth, this.imageHeight, 768, 256);
            graphics.setColor(primary.red(), primary.green(), primary.blue(), 1.0F);
            graphics.blit(LATEX_INVENTORY_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight, 768, 256);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, i + 51, j + 75, 30, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.minecraft.player);

            callback.cancel();
        });
    }
}
