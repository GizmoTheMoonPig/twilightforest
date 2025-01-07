package twilightforest.data.custom;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import twilightforest.item.recipe.ScepterRepairRecipe;

import java.util.ArrayList;
import java.util.List;

public class ScepterRecipeBuilder {

	private final HolderGetter<Item> getter;
	private final Item scepter;
	private final int durability;
	private final List<Ingredient> repairItems = new ArrayList<>();

	private ScepterRecipeBuilder(HolderGetter<Item> getter, Item scepter, int durability) {
		this.getter = getter;
		this.scepter = scepter;
		this.durability = durability;
	}

	public static ScepterRecipeBuilder repairFor(HolderGetter<Item> getter, Item scepter, int durability) {
		return new ScepterRecipeBuilder(getter, scepter, durability);
	}

	public ScepterRecipeBuilder addRepairIngredient(Ingredient item) {
		this.repairItems.add(item);
		return this;
	}

	public ScepterRecipeBuilder addRepairIngredient(TagKey<Item> item) {
		this.repairItems.add(Ingredient.of(this.getter.getOrThrow(item)));
		return this;
	}

	public ScepterRecipeBuilder addRepairIngredient(ItemLike item) {
		this.repairItems.add(Ingredient.of(item));
		return this;
	}

	public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
		ScepterRepairRecipe recipe = new ScepterRepairRecipe(this.scepter, this.repairItems, this.durability, CraftingBookCategory.MISC);
		output.accept(id, recipe, null);
	}
}
