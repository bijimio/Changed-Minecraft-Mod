package net.ltxprogrammer.changed.extension.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.recipe.InfuserRecipe;
import net.ltxprogrammer.changed.recipe.PurifierRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfuserRecipeCategory implements IRecipeCategory<InfuserRecipe> {
    private static final int craftOutputSlot = 0;
    private static final int craftInputSlot1 = 1;

    public static final int WIDTH = 116;
    public static final int HEIGHT = 54;

    private final IDrawable icon;
    private final Component localizedName;
    private final ICraftingGridHelper craftingGridHelper;

    public InfuserRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath("jei", "textures/gui/gui_vanilla.png");
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ChangedBlocks.INFUSER.get()));
        localizedName = Component.translatable("container.changed.infuser");
        craftingGridHelper = guiHelper.createCraftingGridHelper();
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public @NotNull RecipeType<InfuserRecipe> getRecipeType() {
        return new RecipeType<>(Changed.modResource("infuser_recipe"), InfuserRecipe.class);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InfuserRecipe recipe, IFocusGroup focuses) {
        var ingredients = recipe.getIngredients();
        List<List<ItemStack>> grid = new ArrayList<>();

        for (int idx = 0; idx < ingredients.size(); ++idx)
            grid.add(Arrays.asList(ingredients.get(idx).getItems()));

        craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, grid, 3, 3);
        craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, recipe.getPossibleResults());
    }
}
