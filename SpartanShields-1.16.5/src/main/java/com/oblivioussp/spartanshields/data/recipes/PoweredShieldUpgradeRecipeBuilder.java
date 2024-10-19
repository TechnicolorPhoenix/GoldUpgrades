package com.oblivioussp.spartanshields.data.recipes;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.oblivioussp.spartanshields.init.ModRecipes;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;

public class PoweredShieldUpgradeRecipeBuilder extends ConditionalShapedRecipeBuilder
{
	public PoweredShieldUpgradeRecipeBuilder(IItemProvider resultIn, int countIn)
	{
		super(resultIn, countIn);
	}
	
	public static PoweredShieldUpgradeRecipeBuilder shaped(IItemProvider itemIn)
	{
		return new PoweredShieldUpgradeRecipeBuilder(itemIn, 1);
	}
	
	public static PoweredShieldUpgradeRecipeBuilder shaped(IItemProvider itemIn, int countIn)
	{
		return new PoweredShieldUpgradeRecipeBuilder(itemIn, countIn);
	}
	
	public PoweredShieldUpgradeRecipeBuilder define(Character character, ITag<Item> tagIn)
	{
		return define(character, Ingredient.of(tagIn));
	}
	
	public PoweredShieldUpgradeRecipeBuilder define(Character character, IItemProvider itemIn)
	{
		return define(character, Ingredient.of(itemIn));
	}
	
	public PoweredShieldUpgradeRecipeBuilder define(Character character, Ingredient ingredientIn)
	{
		if(keys.containsKey(character))
			throw new IllegalArgumentException("Key character '" + character + "' is already defined!");
		else if(character == ' ')
			throw new IllegalArgumentException("Key character ' ' (whitespace) cannot be defined as it is reserved!");
		else
			keys.put(character, ingredientIn);
		return this;
	}
	
	public PoweredShieldUpgradeRecipeBuilder pattern(String patternIn)
	{
		if(patternIn.isEmpty() && patternIn.length() != pattern.get(0).length())
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		else
			pattern.add(patternIn);
		return this;
	}
	
	public PoweredShieldUpgradeRecipeBuilder unlockedBy(String name, ICriterionInstance criterionIn)
	{
		advancementBuilder.addCriterion(name, criterionIn);
		return this;
	}
	
	public PoweredShieldUpgradeRecipeBuilder group(String groupIn)
	{
		group = groupIn;
		return this;
	}
	
	public PoweredShieldUpgradeRecipeBuilder condition(ICondition conditionIn)
	{
		conditions.add(conditionIn);
		return this;
	}
	
	@Override
	public void save(Consumer<IFinishedRecipe> consumerIn)
	{
		save(consumerIn, ForgeRegistries.ITEMS.getKey(result));
	}
	
	@Override
	public void save(Consumer<IFinishedRecipe> consumerIn, String save) 
	{
		ResourceLocation resultLoc = ForgeRegistries.ITEMS.getKey(result);
		ResourceLocation saveLoc = new ResourceLocation(save);
		if(saveLoc.equals(resultLoc))
			throw new IllegalStateException("Shaped recipe " + save + " save argument is redundant as it's the same as the item id!");
		else
			save(consumerIn, saveLoc);
	}
	
	@Override
	public void save(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id)
	{
		validate(id);
		advancementBuilder.parent(new ResourceLocation("minecraft:recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
		consumerIn.accept(new Result(id, result, count, group == null ? "" : group, pattern, keys, conditions, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + result.getItemCategory().getRecipeFolderName() + "/" + id.getPath())));
	}
	
	public class Result extends ConditionalShapedRecipeBuilder.Result
	{
		public Result(ResourceLocation idIn, Item resultIn, int countIn, String groupIn, List<String> patternIn,
				Map<Character, Ingredient> keysIn, List<ICondition> conditionsIn, 
				Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn)
		{
			super(idIn, resultIn, countIn, groupIn, patternIn, keysIn, conditionsIn, advancementBuilderIn, advancementIdIn);
		}
		
		@Override
		public IRecipeSerializer<?> getType() 
		{
			return ModRecipes.POWERED_SHIELD_UPGRADE;
		}
		
	}
}
