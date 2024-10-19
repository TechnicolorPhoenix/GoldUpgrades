package com.oblivioussp.spartanshields.init;

import com.oblivioussp.spartanshields.item.crafting.PoweredShieldUpgradeRecipe;
import com.oblivioussp.spartanshields.item.crafting.ShieldBannerRecipe;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipes 
{
	public static ShieldBannerRecipe.Serializer SHIELD_BANNER;
	public static PoweredShieldUpgradeRecipe.Serializer POWERED_SHIELD_UPGRADE;
	
	@SubscribeEvent
	public static void initRecipes(RegistryEvent.Register<IRecipeSerializer<?>> ev)
	{
		IForgeRegistry<IRecipeSerializer<?>> reg = ev.getRegistry();
		SHIELD_BANNER = new ShieldBannerRecipe.Serializer();
		POWERED_SHIELD_UPGRADE = new PoweredShieldUpgradeRecipe.Serializer();
		reg.registerAll(SHIELD_BANNER, POWERED_SHIELD_UPGRADE);
	}
}
