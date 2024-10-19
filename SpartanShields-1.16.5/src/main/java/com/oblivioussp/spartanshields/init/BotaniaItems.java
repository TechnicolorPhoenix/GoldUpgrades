package com.oblivioussp.spartanshields.init;

import com.oblivioussp.spartanshields.item.BotaniaShieldItem;
import com.oblivioussp.spartanshields.item.ElementiumShieldItem;
import com.oblivioussp.spartanshields.util.Defaults;

import net.minecraft.item.Item;
import vazkii.botania.api.BotaniaAPI;

public class BotaniaItems 
{
	// These items are only created and registered when Botania is loaded, otherwise nothing happens. This is because otherwise the mod will crash due to missing classes.
	public static void createItems()
	{
		ModItems.shieldManasteel = new BotaniaShieldItem("shield_botania_manasteel", BotaniaAPI.instance().getManasteelItemTier(), Defaults.DefaultDurabilityManasteelShield, 60, new Item.Properties().tab(ModItems.groupSS));
		ModItems.shieldTerrasteel = new BotaniaShieldItem("shield_botania_terrasteel", BotaniaAPI.instance().getTerrasteelItemTier(), Defaults.DefaultDurabilityTerrasteelShield, 100, new Item.Properties().tab(ModItems.groupSS));
		ModItems.shieldElementium = new ElementiumShieldItem("shield_botania_elementium", BotaniaAPI.instance().getElementiumItemTier(), Defaults.DefaultDurabilityElementiumShield, 60, new Item.Properties().tab(ModItems.groupSS));
	}
}
