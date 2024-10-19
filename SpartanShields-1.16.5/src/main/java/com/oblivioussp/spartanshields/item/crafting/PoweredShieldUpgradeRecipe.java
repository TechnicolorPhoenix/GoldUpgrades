package com.oblivioussp.spartanshields.item.crafting;

import com.google.gson.JsonObject;
import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.init.ModRecipes;
import com.oblivioussp.spartanshields.item.FEPoweredShieldItem;
import com.oblivioussp.spartanshields.util.Log;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class PoweredShieldUpgradeRecipe implements ICraftingRecipe, IShapedRecipe<CraftingInventory>
{
	public static final NonNullSupplier<IllegalArgumentException> CAPABILITY_EXCEPTION = () -> new IllegalArgumentException("Capability must not be null!");
	
	private final ShapedRecipe internalRecipe;
	
	public PoweredShieldUpgradeRecipe(ShapedRecipe baseRecipe)
	{
		internalRecipe = baseRecipe;
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) 
	{
		return internalRecipe.matches(inv, worldIn);
	}

	@Override
	public ItemStack assemble(CraftingInventory inv) 
	{
		ItemStack resultStack = getResultItem().copy();
		//CompoundNBT nbt = new CompoundNBT();
		int feToTransfer = 0;
		
		for(int i = 0; i < inv.getContainerSize(); i++)
		{
			ItemStack stack = inv.getItem(i);
			LazyOptional<IEnergyStorage> cap = stack.getCapability(CapabilityEnergy.ENERGY);
			if(cap.isPresent())
				feToTransfer += cap.orElseThrow(CAPABILITY_EXCEPTION).getEnergyStored();
		}
		
		// The Result item *MUST* have the energy capability or it will cause a crash.
		// It is used to look up the maximum stored FE.
		LazyOptional<IEnergyStorage> resultCap = resultStack.getCapability(CapabilityEnergy.ENERGY);
		int maxFE = resultCap.orElseThrow(CAPABILITY_EXCEPTION).getMaxEnergyStored();
		
		// Clamp the stored FE to the maximum storable energy and store it in NBT 
		// (more for edge-cases; in case a recipe is changed that adds more energy tablets)
		feToTransfer = MathHelper.clamp(feToTransfer, 0, maxFE);
		resultStack.getOrCreateTag().putInt(FEPoweredShieldItem.NBT_ENERGY, feToTransfer);
		
		return resultStack;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return internalRecipe.canCraftInDimensions(width, height);
	}

	@Override
	public ItemStack getResultItem() 
	{
		return internalRecipe.getResultItem();
	}

	@Override
	public ResourceLocation getId() 
	{
		return internalRecipe.getId();
	}

	@Override
	public IRecipeSerializer<?> getSerializer() 
	{
		return ModRecipes.POWERED_SHIELD_UPGRADE;
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv)
	{
		return internalRecipe.getRemainingItems(inv);
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() 
	{
		return internalRecipe.getIngredients();
	}
	
	@Override
	public boolean isSpecial() 
	{
		return internalRecipe.isSpecial();
	}
	
	@Override
	public String getGroup() 
	{
		return internalRecipe.getGroup();
	}
	
	@Override
	public ItemStack getToastSymbol() 
	{
		return internalRecipe.getToastSymbol();
	}

	@Override
	public int getRecipeWidth() 
	{
		return internalRecipe.getRecipeWidth();
	}

	@Override
	public int getRecipeHeight() 
	{
		return internalRecipe.getRecipeHeight();
	}
	
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<PoweredShieldUpgradeRecipe>
	{
		public Serializer()
		{
			setRegistryName(new ResourceLocation(ModSpartanShields.ID, "upgrade_powered_shield"));
		}

		@Override
		public PoweredShieldUpgradeRecipe fromJson(ResourceLocation recipeId, JsonObject json) 
		{
			return new PoweredShieldUpgradeRecipe(IRecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json));
		}

		@Override
		public PoweredShieldUpgradeRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
		{
			try
			{
				return new PoweredShieldUpgradeRecipe(IRecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer));
			}
			catch(Exception e)
			{
				Log.error("Failed to read a Powered Shield Upgrade Recipe from a packet!");
				throw e;
			}
		}

		@Override
		public void toNetwork(PacketBuffer buffer, PoweredShieldUpgradeRecipe recipe)
		{
			try
			{
				IRecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe.internalRecipe);
			}
			catch(Exception e)
			{
				Log.error("Failed to write a Powered Shield Upgrade Recipe to a packet!");
			}
		}
		
	}
}
