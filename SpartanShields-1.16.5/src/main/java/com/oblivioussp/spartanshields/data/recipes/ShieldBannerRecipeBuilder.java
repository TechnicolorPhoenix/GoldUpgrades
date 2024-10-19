package com.oblivioussp.spartanshields.data.recipes;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.oblivioussp.spartanshields.init.ModRecipes;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ShieldBannerRecipeBuilder
{
	private final Item shield;
	
	private ShieldBannerRecipeBuilder(IItemProvider shieldIn) 
	{
		shield = shieldIn.asItem();
	}
	
	public static ShieldBannerRecipeBuilder bannerRecipe(IItemProvider shieldIn)
	{
		return new ShieldBannerRecipeBuilder(shieldIn);
	}
	
	public void save(Consumer<IFinishedRecipe> consumer)
	{
		// TODO: Validate that any input shield is a Tower Shield
		consumer.accept(new Result(new ResourceLocation(ForgeRegistries.ITEMS.getKey(shield).toString() + "_banner"), shield));
	}
	
	public static class Result implements IFinishedRecipe
	{
		private final ResourceLocation id;
		private final Item shield;
		
		private Result(ResourceLocation idIn, Item shieldIn)
		{
			id = idIn;
			shield = shieldIn;
		}

		@Override
		public void serializeRecipeData(JsonObject json)
		{
			json.addProperty("shield", ForgeRegistries.ITEMS.getKey(shield).toString());
		}

		@Override
		public ResourceLocation getId() 
		{
			return id;
		}

		@Override
		public IRecipeSerializer<?> getType() 
		{
			return ModRecipes.SHIELD_BANNER;
		}

		@Override
		public JsonObject serializeAdvancement() 
		{
			return null;
		}

		@Override
		public ResourceLocation getAdvancementId() 
		{
			return new ResourceLocation("");
		}
		
	}
}
