package com.oblivioussp.spartanshields.compat.jei;

import java.util.List;
import java.util.stream.Collectors;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.init.ModRecipes;
import com.oblivioussp.spartanshields.util.Log;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class SpartanShieldsPlugin implements IModPlugin
{
	private final ResourceLocation PLUGIN_UID = new ResourceLocation(ModSpartanShields.ID, "jei_plugin");
	
	public static final ResourceLocation SHIELD_BANNER_UID = new ResourceLocation(ModSpartanShields.ID, "shield_banner");
	public static ShieldBannerRecipeCategory shieldBannerRecipeCategory;
	
	@Override
	public ResourceLocation getPluginUid()
	{
		return PLUGIN_UID;
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration reg) 
	{
		shieldBannerRecipeCategory = new ShieldBannerRecipeCategory(reg.getJeiHelpers().getGuiHelper());
		reg.addRecipeCategories(shieldBannerRecipeCategory);
	}

	@Override
	public void registerRecipes(IRecipeRegistration reg)
	{
		RecipeManager recipeManager = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getRecipeManager() : null;
		if(recipeManager == null)
		{
			Log.error("Failed to fetch recipe manager from world: world doesn't exist!");
			return;
		}
		
		List<IRecipe<?>> recipesToAdd = recipeManager.getRecipes().stream().filter(r -> r.getSerializer() == IRecipeSerializer.SHIELD_DECORATION || r.getSerializer() == ModRecipes.SHIELD_BANNER).
		collect(Collectors.toList());
		
		reg.addRecipes(recipesToAdd, SHIELD_BANNER_UID);
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) 
	{
		reg.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), SHIELD_BANNER_UID);
	}
	
}
