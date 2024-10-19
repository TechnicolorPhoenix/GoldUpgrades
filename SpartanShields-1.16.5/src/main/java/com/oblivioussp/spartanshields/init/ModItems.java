package com.oblivioussp.spartanshields.init;

import com.oblivioussp.spartanshields.client.render.item.ItemStackTileEntityRendererSS;
import com.oblivioussp.spartanshields.enchantment.EnchantmentSS;
import com.oblivioussp.spartanshields.item.BasicShieldItem;
import com.oblivioussp.spartanshields.item.ExternalModShieldItem;
import com.oblivioussp.spartanshields.item.FEPoweredShieldItem;
import com.oblivioussp.spartanshields.item.ObsidianShieldItem;
import com.oblivioussp.spartanshields.item.ShieldBaseItem;
import com.oblivioussp.spartanshields.item.SilverShieldItem;
import com.oblivioussp.spartanshields.util.Constants;
import com.oblivioussp.spartanshields.util.Defaults;
import com.oblivioussp.spartanshields.util.ItemTierSS;
import com.oblivioussp.spartanshields.util.Log;
import com.oblivioussp.spartanshields.util.PowerUnit;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
    public static ItemGroup groupSS = new ItemGroup("spartanshields")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(shieldStone);
        }
    };
    
	public enum TowerShieldRenderType
	{
		WOOD,
		STONE,
		IRON,
		GOLD,
		DIAMOND,
		NETHERITE,
		OBSIDIAN,
		COPPER,
		TIN,
		BRONZE,
		STEEL,
		SILVER,
		LEAD,
		NICKEL,
		INVAR,
		CONSTANTAN,
		PLATINUM,
		ELECTRUM,
		DRAGONBONE
	}

    public static ShieldBaseItem shieldWood, shieldStone, shieldIron, shieldGold, shieldDiamond, shieldNetherite, shieldObsidian;
    public static ShieldBaseItem shieldCopper, shieldTin, shieldBronze, shieldSteel, shieldSilver, shieldLead;
    public static ShieldBaseItem shieldNickel, shieldInvar, shieldConstantan, shieldPlatinum, shieldElectrum;

//    public static ItemShieldBase shieldEnderium, shieldSignalum, shieldLumium;
//	public static ItemShieldBase shieldManasteel, shieldTerrasteel, shieldElementium, shieldSoulforgedSteel;
//	public static ItemShieldBase shieldDarkstone, shieldAbyssalnite, shieldCoralium, shieldDreadium, shieldEthaxium;

    public static ShieldBaseItem shieldTowerWood, shieldTowerStone, shieldTowerIron, shieldTowerGold, shieldTowerDiamond, shieldTowerNetherite, shieldTowerObsidian;
    public static ShieldBaseItem shieldTowerCopper, shieldTowerTin, shieldTowerBronze, shieldTowerSteel, shieldTowerSilver, shieldTowerLead;
    public static ShieldBaseItem shieldTowerNickel, shieldTowerInvar, shieldTowerConstantan, shieldTowerPlatinum, shieldTowerElectrum;

	public static ShieldBaseItem shieldManasteel, shieldTerrasteel, shieldElementium;
	
	public static ShieldBaseItem shieldOsmium, shieldLapisLazuli, shieldRefinedGlowstone, shieldRefinedObsidian;
	public static ShieldBaseItem shieldPoweredMekanismBasic, shieldPoweredMekanismAdvanced, shieldPoweredMekanismElite, shieldPoweredMekanismUltimate;
	
	public static ShieldBaseItem shieldDragonbone, shieldTowerDragonbone;
	
    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<Item> ev)
    {
        Log.debug("Initialising Items");
        
        Item.Properties propShield = new Item.Properties().tab(groupSS);
        Item.Properties propNetheriteShield = new Item.Properties().tab(groupSS).fireResistant();
        groupSS.setEnchantmentCategories(EnchantmentSS.TYPE_SHIELD);
        
        shieldWood = new BasicShieldItem("shield_basic_wood", ItemTier.WOOD, Defaults.DefaultDurabilityWoodShield, propShield);
        shieldStone = new BasicShieldItem("shield_basic_stone", ItemTier.STONE, Defaults.DefaultDurabilityStoneShield, propShield);
        shieldIron = new BasicShieldItem("shield_basic_iron", ItemTier.IRON, Defaults.DefaultDurabilityIronShield, propShield);
        shieldGold = new BasicShieldItem("shield_basic_gold", ItemTier.GOLD, Defaults.DefaultDurabilityGoldShield, propShield);
        shieldDiamond = new BasicShieldItem("shield_basic_diamond", ItemTier.DIAMOND, Defaults.DefaultDurabilityDiamondShield, propShield);
        shieldNetherite = new BasicShieldItem("shield_basic_netherite", ItemTier.NETHERITE, Defaults.DefaultDurabilityNetheriteShield, propNetheriteShield);
        shieldObsidian = new ObsidianShieldItem("shield_basic_obsidian", ItemTierSS.OBSIDIAN, Defaults.DefaultDurabilityObsidianShield, propShield);
        
        shieldCopper = new BasicShieldItem("shield_basic_copper", ItemTierSS.COPPER, Defaults.DefaultDurabilityCopperShield, /*"ingot_copper",*/ propShield);
        shieldTin = new BasicShieldItem("shield_basic_tin", ItemTierSS.TIN, Defaults.DefaultDurabilityTinShield, propShield);
        shieldBronze = new BasicShieldItem("shield_basic_bronze", ItemTierSS.BRONZE, Defaults.DefaultDurabilityBronzeShield, propShield);
        shieldSteel = new BasicShieldItem("shield_basic_steel", ItemTierSS.STEEL, Defaults.DefaultDurabilitySteelShield, propShield);
        shieldSilver = new SilverShieldItem("shield_basic_silver", ItemTierSS.SILVER, Defaults.DefaultDurabilitySilverShield, propShield);
        shieldLead = new BasicShieldItem("shield_basic_lead", ItemTierSS.LEAD, Defaults.DefaultDurabilityLeadShield, propShield);
        shieldNickel = new BasicShieldItem("shield_basic_nickel", ItemTierSS.NICKEL, Defaults.DefaultDurabilityNickelShield, propShield);
        shieldInvar = new BasicShieldItem("shield_basic_invar", ItemTierSS.INVAR, Defaults.DefaultDurabilityInvarShield, propShield);
        shieldConstantan = new BasicShieldItem("shield_basic_constantan", ItemTierSS.CONSTANTAN, Defaults.DefaultDurabilityConstantanShield, propShield);
        shieldPlatinum = new BasicShieldItem("shield_basic_platinum", ItemTierSS.PLATINUM, Defaults.DefaultDurabilityPlatinumShield, propShield);
        shieldElectrum = new BasicShieldItem("shield_basic_electrum", ItemTierSS.ELECTRUM, Defaults.DefaultDurabilityElectrumShield, propShield);        
        
        shieldTowerWood = new BasicShieldItem("shield_tower_wood", ItemTier.WOOD, Defaults.DefaultDurabilityWoodShield, applyISTER(propShield, TowerShieldRenderType.WOOD));
        shieldTowerStone = new BasicShieldItem("shield_tower_stone", ItemTier.STONE, Defaults.DefaultDurabilityStoneShield, applyISTER(propShield, TowerShieldRenderType.STONE));
        shieldTowerIron = new BasicShieldItem("shield_tower_iron", ItemTier.IRON, Defaults.DefaultDurabilityIronShield, applyISTER(propShield, TowerShieldRenderType.IRON));
        shieldTowerGold = new BasicShieldItem("shield_tower_gold", ItemTier.GOLD, Defaults.DefaultDurabilityGoldShield, applyISTER(propShield, TowerShieldRenderType.GOLD));
        shieldTowerDiamond = new BasicShieldItem("shield_tower_diamond", ItemTier.DIAMOND, Defaults.DefaultDurabilityDiamondShield, applyISTER(propShield, TowerShieldRenderType.DIAMOND));
        shieldTowerNetherite = new BasicShieldItem("shield_tower_netherite", ItemTier.NETHERITE, Defaults.DefaultDurabilityNetheriteShield, applyISTER(propNetheriteShield, TowerShieldRenderType.NETHERITE));
        shieldTowerObsidian = new ObsidianShieldItem("shield_tower_obsidian", ItemTierSS.OBSIDIAN, Defaults.DefaultDurabilityObsidianShield, applyISTER(propShield, TowerShieldRenderType.OBSIDIAN));
        
        shieldTowerCopper = new BasicShieldItem("shield_tower_copper", ItemTierSS.COPPER, Defaults.DefaultDurabilityCopperShield, applyISTER(propShield, TowerShieldRenderType.COPPER));
        shieldTowerTin = new BasicShieldItem("shield_tower_tin", ItemTierSS.TIN, Defaults.DefaultDurabilityTinShield, applyISTER(propShield, TowerShieldRenderType.TIN));
        shieldTowerBronze = new BasicShieldItem("shield_tower_bronze", ItemTierSS.BRONZE, Defaults.DefaultDurabilityBronzeShield, applyISTER(propShield, TowerShieldRenderType.BRONZE));
        shieldTowerSteel = new BasicShieldItem("shield_tower_steel", ItemTierSS.STEEL, Defaults.DefaultDurabilitySteelShield, applyISTER(propShield, TowerShieldRenderType.STEEL));
        shieldTowerSilver = new BasicShieldItem("shield_tower_silver", ItemTierSS.SILVER, Defaults.DefaultDurabilitySilverShield, applyISTER(propShield, TowerShieldRenderType.SILVER));
        shieldTowerLead = new BasicShieldItem("shield_tower_lead", ItemTierSS.LEAD, Defaults.DefaultDurabilityLeadShield, applyISTER(propShield, TowerShieldRenderType.LEAD));
        shieldTowerNickel = new BasicShieldItem("shield_tower_nickel", ItemTierSS.NICKEL, Defaults.DefaultDurabilityNickelShield, applyISTER(propShield, TowerShieldRenderType.NICKEL));
        shieldTowerInvar = new BasicShieldItem("shield_tower_invar", ItemTierSS.INVAR, Defaults.DefaultDurabilityInvarShield, applyISTER(propShield, TowerShieldRenderType.INVAR));
        shieldTowerConstantan = new BasicShieldItem("shield_tower_constantan", ItemTierSS.CONSTANTAN, Defaults.DefaultDurabilityConstantanShield, applyISTER(propShield, TowerShieldRenderType.CONSTANTAN));
        shieldTowerPlatinum = new BasicShieldItem("shield_tower_platinum", ItemTierSS.PLATINUM, Defaults.DefaultDurabilityPlatinumShield, applyISTER(propShield, TowerShieldRenderType.PLATINUM));
        shieldTowerElectrum = new BasicShieldItem("shield_tower_electrum", ItemTierSS.ELECTRUM, Defaults.DefaultDurabilityElectrumShield, applyISTER(propShield, TowerShieldRenderType.ELECTRUM));
        
        if(ModList.get().isLoaded(Constants.Botania_ModID))
        {
        	// The item creation functions are placed in another class due to imports causing crashes when missing.
        	BotaniaItems.createItems();
        }
        else
        {
        	shieldManasteel = new ExternalModShieldItem("shield_botania_manasteel", ItemTierSS.MANASTEEL, Defaults.DefaultDurabilityManasteelShield, Constants.Botania_ModID, new Item.Properties().tab(ModItems.groupSS));
    		shieldTerrasteel = new ExternalModShieldItem("shield_botania_terrasteel", ItemTierSS.TERRASTEEL, Defaults.DefaultDurabilityTerrasteelShield, Constants.Botania_ModID, new Item.Properties().tab(ModItems.groupSS));
    		shieldElementium = new ExternalModShieldItem("shield_botania_elementium", ItemTierSS.ELEMENTIUM, Defaults.DefaultDurabilityElementiumShield, Constants.Botania_ModID, new Item.Properties().tab(ModItems.groupSS));
        }
        
        shieldOsmium = new BasicShieldItem("shield_mekanism_osmium", ItemTierSS.OSMIUM, Defaults.DefaultDurabilityOsmiumShield, propShield);
        shieldLapisLazuli = new BasicShieldItem("shield_mekanism_lapis_lazuli", ItemTierSS.LAPIS_LAZULI, Defaults.DefaultDurabilityLapisLazuliShield, propShield);
        shieldRefinedGlowstone = new BasicShieldItem("shield_mekanism_refined_glowstone", ItemTierSS.REFINED_GLOWSTONE, Defaults.DefaultDurabilityRefinedGlowstoneShield, propShield);
        shieldRefinedObsidian = new BasicShieldItem("shield_mekanism_refined_obsidian", ItemTierSS.REFINED_OBSIDIAN, Defaults.DefaultDurabilityRefinedObsidianShield, propShield);
        shieldPoweredMekanismBasic = new FEPoweredShieldItem("shield_mekanism_powered_basic", 800000, 2000, "mekanism", PowerUnit.ForgeEnergy, propShield);
        shieldPoweredMekanismAdvanced = new FEPoweredShieldItem("shield_mekanism_powered_advanced", 1600000, 4000, "mekanism", PowerUnit.ForgeEnergy, propShield);
        shieldPoweredMekanismElite = new FEPoweredShieldItem("shield_mekanism_powered_elite", 2400000, 6000, "mekanism", PowerUnit.ForgeEnergy, propShield);
        shieldPoweredMekanismUltimate = new FEPoweredShieldItem("shield_mekanism_powered_ultimate", 3200000, 8000, "mekanism", PowerUnit.ForgeEnergy, propShield);
        
        shieldDragonbone = new BasicShieldItem("shield_iceandfire_dragonbone", ItemTierSS.DRAGONBONE, Defaults.DefaultDurabilityDragonboneShield, propShield);
        shieldTowerDragonbone = new BasicShieldItem("shield_iceandfire_tower_dragonbone", ItemTierSS.DRAGONBONE, Defaults.DefaultDurabilityDragonboneShield, applyISTER(propShield, TowerShieldRenderType.DRAGONBONE));
        
        Log.info("Registering Items");
        IForgeRegistry<Item> reg = ev.getRegistry();
        reg.registerAll(shieldWood, shieldStone, shieldIron, shieldGold, shieldDiamond, shieldNetherite, shieldObsidian,
        		shieldCopper, shieldTin, shieldBronze, shieldSteel, shieldSilver, shieldLead,
        		shieldNickel, shieldInvar, shieldConstantan, shieldPlatinum, shieldElectrum,
        		shieldTowerWood, shieldTowerStone, shieldTowerIron, shieldTowerGold, shieldTowerDiamond, shieldTowerNetherite, shieldTowerObsidian,
        		shieldTowerCopper, shieldTowerTin, shieldTowerBronze, shieldTowerSteel, shieldTowerSilver, shieldTowerLead,
        		shieldTowerNickel, shieldTowerInvar, shieldTowerConstantan, shieldTowerPlatinum, shieldTowerElectrum);
        reg.registerAll(shieldManasteel, shieldTerrasteel, shieldElementium,
        		shieldOsmium, shieldLapisLazuli, shieldRefinedGlowstone, shieldRefinedObsidian);
        reg.registerAll(shieldPoweredMekanismBasic, shieldPoweredMekanismAdvanced, shieldPoweredMekanismElite, shieldPoweredMekanismUltimate);
        reg.registerAll(shieldDragonbone, shieldTowerDragonbone);
        Log.info("Registered Items successfully!");
    }
    
    protected static Item.Properties applyISTER(Item.Properties prop, TowerShieldRenderType modelType)
    {
    	if(FMLEnvironment.dist.isClient())
    	{
    		return setISTER(prop, modelType);
    	}
    	return prop;
    }
    
    @OnlyIn(Dist.CLIENT)
    protected static Item.Properties setISTER(Item.Properties prop, TowerShieldRenderType modelType)
    {
    	prop.setISTER(() -> () -> new ItemStackTileEntityRendererSS(modelType));
    	return prop;
    }
}
