package com.oblivioussp.spartanshields.client;

import com.oblivioussp.spartanshields.init.ModItems;
import com.oblivioussp.spartanshields.item.FEPoweredShieldItem;
import com.oblivioussp.spartanshields.item.ShieldBaseItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientHelper 
{
	public static void registerItemColors()
	{
		Minecraft.getInstance().getItemColors().register(new IItemColor() {
			@Override
			public int getColor(ItemStack stack, int layer) 
			{
				return layer == 1 ? 0x78F083 : 0xFFFFFF;
			}
		}, ModItems.shieldPoweredMekanismBasic);
		Minecraft.getInstance().getItemColors().register(new IItemColor() {
			@Override
			public int getColor(ItemStack stack, int layer) 
			{
				return layer == 1 ? 0xF07883 : 0xFFFFFF;
			}
		}, ModItems.shieldPoweredMekanismAdvanced);
		Minecraft.getInstance().getItemColors().register(new IItemColor() {
			@Override
			public int getColor(ItemStack stack, int layer) 
			{
				return layer == 1 ? 0x7883F0 : 0xFFFFFF;
			}
		}, ModItems.shieldPoweredMekanismElite);
		Minecraft.getInstance().getItemColors().register(new IItemColor() {
			@Override
			public int getColor(ItemStack stack, int layer) 
			{
				return layer == 1 ? 0xF083F0 : 0xFFFFFF;
			}
		}, ModItems.shieldPoweredMekanismUltimate);
	}
	
	public static void registerShieldPropertyOverrides(ShieldBaseItem item)
	{
		ItemModelsProperties.register(item, new ResourceLocation("blocking"), (stack, world, living) ->
		{
	         return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0f : 0.0f;
		});
	}
	
	public static void registerPoweredShieldPropertyOverrides(FEPoweredShieldItem item)
	{
		registerShieldPropertyOverrides(item);
		ItemModelsProperties.register(item, new ResourceLocation("disabled"), (stack, world, living) -> 
		{
			return stack.getOrCreateTag().getInt(FEPoweredShieldItem.NBT_ENERGY) <= 0 ? 1.0f : 0.0f;
		});
	}
}
