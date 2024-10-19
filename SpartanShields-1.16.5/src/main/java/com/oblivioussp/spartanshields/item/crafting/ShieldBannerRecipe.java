package com.oblivioussp.spartanshields.item.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.init.ModRecipes;
import com.oblivioussp.spartanshields.item.ShieldBaseItem;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShieldRecipes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ShieldBannerRecipe extends ShieldRecipes
{
	//private static ResourceLocation NAME = new ResourceLocation(Reference.MOD_ID, "apply_banner");
	protected final Item shieldItem;

	public ShieldBannerRecipe(ResourceLocation loc, Item shield)
	{
		super(loc);
		shieldItem = shield;
	}
	
	@Override
	public boolean matches(CraftingInventory inv, World worldIn)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemStack itemstack1 = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); ++i)
        {
            ItemStack itemstack2 = inv.getItem(i);

            if (!itemstack2.isEmpty())
            {
                if (ItemTags.BANNERS.contains(itemstack2.getItem()))
                {
                    if (!itemstack1.isEmpty())
                    {
                        return false;
                    }

                    itemstack1 = itemstack2;
                }
                else
                {
                    if //(!shieldItems.contains(itemstack2.getItem()))
                    	(itemstack2.getItem() != shieldItem)
                    {
                        return false;
                    }

                    if (!itemstack.isEmpty())
                    {
                        return false;
                    }

                    if (itemstack2.getTagElement("BlockEntityTag") != null)
                    {
                        return false;
                    }

                    itemstack = itemstack2;
                }
            }
        }

        if (!itemstack.isEmpty() && !itemstack1.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns an Item that is the result of this recipe
     */
	@Override
    public ItemStack assemble(CraftingInventory inv)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemStack itemstack1 = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); ++i)
        {
            ItemStack itemstack2 = inv.getItem(i);

            if (!itemstack2.isEmpty())
            {
                if (ItemTags.BANNERS.contains(itemstack2.getItem()))
                {
                    itemstack = itemstack2;
                }
                else if //!shieldItems.contains(itemstack2.getItem()))
                		(itemstack2.getItem() == shieldItem)
                {
                    itemstack1 = itemstack2.copy();
                }
            }
        }

        if (itemstack1.isEmpty())
        {
            return itemstack1;
        }
        else
        {
            CompoundNBT nbttagcompound = itemstack.getTagElement("BlockEntityTag");
            CompoundNBT nbttagcompound1 = nbttagcompound == null ? new CompoundNBT() : nbttagcompound.copy();
            nbttagcompound1.putInt("Base", ((BannerItem)itemstack.getItem()).getColor().getId());
            itemstack1.addTagElement("BlockEntityTag", nbttagcompound1);
            return itemstack1;
        }
    }

	@Override
    public ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

	@Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv)
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i)
        {
            ItemStack itemstack = inv.getItem(i);

            if (itemstack.getItem().hasContainerItem(itemstack))
            {
                nonnulllist.set(i, itemstack.getItem().getContainerItem(itemstack));
            }
        }

        return nonnulllist;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
	@Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= 2;
    }

	@Override
	public IRecipeSerializer<?> getSerializer() 
	{
		return ModRecipes.SHIELD_BANNER;
	}
	
	public Item getShieldItem()
	{
		return shieldItem;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShieldBannerRecipe>
	{
		public Serializer()
		{
			setRegistryName(new ResourceLocation(ModSpartanShields.ID, "apply_banner"));
		}
		
		@Override
		public ShieldBannerRecipe fromJson(ResourceLocation recipeId, JsonObject json)
		{
			String shieldName = JSONUtils.getAsString(json, "shield");
	        Item shieldItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(shieldName));
			if(shieldItem != null)
			{
				if(shieldItem instanceof ShieldBaseItem)
					return new ShieldBannerRecipe(recipeId, shieldItem);
				else
					throw new JsonSyntaxException("Item " + shieldName + " is not a valid Shield!");
			}
			else
	            throw new JsonSyntaxException("Item " + shieldName + " did not exist!");
		}

		@Override
		public ShieldBannerRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) 
		{
			ResourceLocation shieldName = buffer.readResourceLocation();
			Item shield = ForgeRegistries.ITEMS.getValue(shieldName);
			return new ShieldBannerRecipe(recipeId, shield);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, ShieldBannerRecipe recipe) 
		{
			ResourceLocation shieldName = recipe.shieldItem.getRegistryName();
			buffer.writeResourceLocation(shieldName);
		}
		
		
	}
}
