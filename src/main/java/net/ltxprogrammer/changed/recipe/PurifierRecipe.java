package net.ltxprogrammer.changed.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRecipeSerializers;
import net.ltxprogrammer.changed.init.ChangedRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class PurifierRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    final String group;
    final Ingredient ingredient;
    final Item result;
    final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public PurifierRecipe(ResourceLocation id, String group, Ingredient ingredient, Item result) {
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
        this.isSimple = ingredient.isSimple();
        this.ingredients = NonNullList.of(ingredient, ingredient);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return ChangedRecipeSerializers.PURIFIER_RECIPE.get();
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem() {
        return new ItemStack(ChangedItems.LATEX_SYRINGE.get());
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public Item getResult() {
        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public boolean matches(SimpleContainer p_44262_, Level p_44263_) {
        StackedContents stackedcontents = new StackedContents();
        List<ItemStack> inputs = new ArrayList<>();
        int i = 0;

        for(int j = 0; j < p_44262_.getContainerSize(); ++j) {
            ItemStack itemstack = p_44262_.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple)
                    stackedcontents.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == 1 && (isSimple ? stackedcontents.canCraft(this, (IntList)null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs,  this.ingredients) != null);
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return this.getResultItem();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.getResultItem();
    }

    public boolean canCraftInDimensions(int p_44252_, int p_44253_) {
        return p_44252_ * p_44253_ >= this.ingredients.size();
    }

    public static class Serializer implements RecipeSerializer<PurifierRecipe> {
        public PurifierRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            Ingredient in = Ingredient.fromJson(json.get("ingredient"));
            Item out = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(json.get("result").getAsString()));
            return new PurifierRecipe(id, group, in, out);
        }

        public PurifierRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Ingredient in = Ingredient.fromNetwork(buffer);
            Item out = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(buffer.readUtf()));
            return new PurifierRecipe(id, group, in, out);
        }

        public void toNetwork(FriendlyByteBuf buffer, PurifierRecipe recipe) {
            buffer.writeUtf(recipe.group);
            recipe.ingredient.toNetwork(buffer);
            buffer.writeUtf(ForgeRegistries.ITEMS.getKey(recipe.result).toString());
        }
    }

    @Override
    public RecipeType<?> getType() {
        return ChangedRecipeTypes.PURIFIER_RECIPE.get();
    }
}
