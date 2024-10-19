package com.oblivioussp.spartanshields.compat.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.item.crafting.ShieldBannerRecipe;

import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShieldRecipes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ShieldBannerRecipeCategory implements IRecipeCategory<ShieldRecipes> 
{
	private static final ResourceLocation RECIPE_GUI_VANILLA = new ResourceLocation(ModIds.JEI_ID, "textures/gui/gui_vanilla.png");
	
	private final IDrawable background;
	private final IDrawable icon;
	private final String title;
	
	public ShieldBannerRecipeCategory(IGuiHelper guiHelper)
	{
		icon = guiHelper.createDrawableIngredient(new ItemStack(Items.SHIELD));
		background = guiHelper.createDrawable(RECIPE_GUI_VANILLA, 0, 60, 116, 54);
		title = I18n.get("gui." + ModSpartanShields.ID + ".category.shield_banner");
	}

	@Override
	public ResourceLocation getUid()
	{
		return SpartanShieldsPlugin.SHIELD_BANNER_UID;
	}

	@Override
	public Class<? extends ShieldRecipes> getRecipeClass() 
	{
		return ShieldRecipes.class;
	}

	@Override
	public String getTitle() 
	{
		return title;
	}

	@Override
	public IDrawable getBackground()
	{
		return background;
	}

	@Override
	public IDrawable getIcon()
	{
		return icon;
	}

	@Override
	public void setIngredients(ShieldRecipes recipe, IIngredients ingredients) 
	{
		ItemStack shield = new ItemStack(Items.SHIELD);
		List<ItemStack> banners = Arrays.asList(Ingredient.of(ItemTags.BANNERS).getItems());
		
		if(recipe instanceof ShieldBannerRecipe)
		{
			ShieldBannerRecipe shieldRecipe = (ShieldBannerRecipe)recipe;
			shield = new ItemStack(shieldRecipe.getShieldItem());
		}
		
		List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>();
		inputs.add(Collections.singletonList(shield));
		inputs.add(banners);
		ingredients.setInputLists(VanillaTypes.ITEM, inputs);
		
		List<ItemStack> outputs = new ArrayList<ItemStack>();
		
		for(ItemStack banner : banners)
		{
			ItemStack banneredShield = shield.copy();
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("Base", ((BannerItem)banner.getItem()).getColor().getId());
			banneredShield.getOrCreateTag().put("BlockEntityTag", nbt);
			outputs.add(banneredShield);
		}
		
		ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(outputs));
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ShieldRecipes recipe, IIngredients ingredients) 
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(0, false, 94, 18);
		
		guiItemStacks.init(1, true, 18, 18);
		guiItemStacks.init(2, true, 36, 18);
		
		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
		
		guiItemStacks.set(1, inputs.get(0));
		guiItemStacks.set(2, inputs.get(1));
		guiItemStacks.set(0, outputs.get(0));
		
		recipeLayout.setShapeless();
		IGuiItemStackGroup group = recipeLayout.getItemStacks();
		group.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
			if(outputs.get(0).contains(ingredient))
			{
				tooltip.add(new TranslationTextComponent("gui." + ModSpartanShields.ID + ".tooltip.shield_banner.any_pattern").withStyle(TextFormatting.ITALIC, TextFormatting.RED));
			}
		});
	}
	
	

}
