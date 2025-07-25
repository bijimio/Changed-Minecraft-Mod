package net.ltxprogrammer.changed.mixin.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.recipe.InfuserRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeButton.class)
public abstract class RecipeButtonMixin extends AbstractWidget {
    @Shadow
    private RecipeCollection collection;
    @Shadow
    private float time;
    @Shadow
    private int currentIndex;
    @Shadow
    private RecipeBook book;
    @Shadow
    private RecipeBookMenu<?> menu;
    @Unique
    private static final ResourceLocation RECIPE_BOOK_LOCATION = ResourceLocation.parse("textures/gui/recipe_book.png");
    @Unique
    private Gender activeGender = Gender.MALE;

    public RecipeButtonMixin(int p_93629_, int p_93630_, int p_93631_, int p_93632_, Component p_93633_) {
        super(p_93629_, p_93630_, p_93631_, p_93632_, p_93633_);
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    public void renderButton(GuiGraphics p_281385_, int p_282779_, int p_282744_, float p_282439_, CallbackInfo ci) {
        if (collection.getRecipes().get(0) instanceof InfuserRecipe infuserRecipe) {
            if (!Screen.hasControlDown()) {
                time += p_282439_;
            }

            Minecraft minecraft = Minecraft.getInstance();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, RECIPE_BOOK_LOCATION);

            currentIndex = 0;
            activeGender = Gender.values()[Mth.floor(time / 30.0F) % (infuserRecipe.gendered ? 2 : 1)];
            ResourceLocation formId = infuserRecipe.form;
            if (infuserRecipe.gendered)
                formId = ResourceLocation.parse(formId + "/" + activeGender.toString().toLowerCase());
            TransfurVariant<?> variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(formId);
            if (variant == null)
                return;
            ChangedEntity entity = ChangedEntities.getCachedEntity(minecraft.level, variant.getEntityType());
            if (minecraft.player instanceof PlayerDataExtension ext)
                entity.getBasicPlayerInfo().copyFrom(ext.getBasicPlayerInfo());
            entity.tickCount = (int)time;

            InventoryScreen.renderEntityInInventoryFollowsMouse(p_281385_, this.getX() + 15, this.getY() + 25, isHoveredOrFocused() ? 40 : 10, (float) (Math.sin(time / 60.0f) * 60.0f), 0.0f, entity);

            ci.cancel();
        }
    }

    private static final Component MORE_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.moreRecipes");
    @Inject(method = "getTooltipText", at = @At("HEAD"), cancellable = true)
    public void getTooltipText(CallbackInfoReturnable<List<Component>> ci) {
        if (collection.getRecipes().get(0) instanceof InfuserRecipe infuserRecipe) {
            ci.cancel();
            List<Component> list = Lists.newArrayList(infuserRecipe.getNameFor(Minecraft.getInstance().level, activeGender));

            if (collection.getRecipes(book.isFiltering(menu)).size() > 1)
                list.add(MORE_RECIPES_TOOLTIP);

            ci.setReturnValue(list);
        }
    }
}
