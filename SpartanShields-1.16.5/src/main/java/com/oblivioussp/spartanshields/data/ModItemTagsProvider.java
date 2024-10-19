package com.oblivioussp.spartanshields.data;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.init.ModItems;
import com.oblivioussp.spartanshields.tags.ModItemTags;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider
{

	public ModItemTagsProvider(DataGenerator dataGenIn, ExistingFileHelper existingFileHelper)
	{
		super(dataGenIn, new BlockTagsProvider(dataGenIn, ModSpartanShields.ID, existingFileHelper) 
		{
			@Override
			protected void addTags() {}
		}, ModSpartanShields.ID, existingFileHelper);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void addTags()
	{
		// TODO: Add Tags here and add as a provider for the data generation event

		INamedTag<Item> copperIngot = ItemTags.bind("forge:ingots/copper");
		INamedTag<Item> tinIngot = ItemTags.bind("forge:ingots/tin");
		INamedTag<Item> bronzeIngot = ItemTags.bind("forge:ingots/bronze");
		INamedTag<Item> steelIngot = ItemTags.bind("forge:ingots/steel");
		INamedTag<Item> silverIngot = ItemTags.bind("forge:ingots/silver");
		INamedTag<Item> leadIngot = ItemTags.bind("forge:ingots/lead");
		INamedTag<Item> nickelIngot = ItemTags.bind("forge:ingots/nickel");
		INamedTag<Item> invarIngot = ItemTags.bind("forge:ingots/invar");
		INamedTag<Item> constantanIngot = ItemTags.bind("forge:ingots/constantan");
		INamedTag<Item> platinumIngot = ItemTags.bind("forge:ingots/platinum");
		INamedTag<Item> electrumIngot = ItemTags.bind("forge:ingots/electrum");

		INamedTag<Item> manasteelIngot = ItemTags.bind("forge:ingots/manasteel");
		INamedTag<Item> terrasteelIngot = ItemTags.bind("forge:ingots/terrasteel");
		INamedTag<Item> elementiumIngot = ItemTags.bind("forge:ingots/elementium");
		
		INamedTag<Item> osmiumIngot = ItemTags.bind("forge:ingots/osmium");
		INamedTag<Item> refinedGlowstoneIngot = ItemTags.bind("forge:ingots/refined_glowstone");
		INamedTag<Item> refinedObsidianIngot = ItemTags.bind("forge:ingots/refined_obsidian");
		
		INamedTag<Item> dragonbone = ItemTags.bind("forge:bones/dragon");
		
		tag(copperIngot);
		tag(tinIngot);
		tag(bronzeIngot);
		tag(steelIngot);
		tag(silverIngot);
		tag(leadIngot);
		tag(nickelIngot);
		tag(invarIngot);
		tag(constantanIngot);
		tag(platinumIngot);
		tag(electrumIngot);
		tag(manasteelIngot);
		tag(terrasteelIngot);
		tag(elementiumIngot);
		tag(osmiumIngot);
		tag(refinedGlowstoneIngot);
		tag(refinedObsidianIngot);
		tag(dragonbone);
		
		tag(ModItemTags.basicShields).add(ModItems.shieldWood, ModItems.shieldStone, ModItems.shieldIron, ModItems.shieldGold, ModItems.shieldDiamond, ModItems.shieldNetherite, ModItems.shieldObsidian,
				ModItems.shieldCopper, ModItems.shieldTin, ModItems.shieldBronze, ModItems.shieldSteel, ModItems.shieldSilver, ModItems.shieldLead, ModItems.shieldNickel, ModItems.shieldInvar,
				ModItems.shieldConstantan, ModItems.shieldPlatinum, ModItems.shieldElectrum, ModItems.shieldManasteel, ModItems.shieldTerrasteel, ModItems.shieldElementium,
				ModItems.shieldOsmium, ModItems.shieldLapisLazuli, ModItems.shieldRefinedGlowstone, ModItems.shieldRefinedObsidian, 
				ModItems.shieldPoweredMekanismBasic, ModItems.shieldPoweredMekanismAdvanced, ModItems.shieldPoweredMekanismElite, ModItems.shieldPoweredMekanismUltimate);
		
		tag(ModItemTags.towerShields).add(ModItems.shieldTowerWood, ModItems.shieldTowerStone, ModItems.shieldTowerIron, ModItems.shieldTowerGold, ModItems.shieldTowerDiamond, ModItems.shieldTowerNetherite, ModItems.shieldTowerObsidian,
				ModItems.shieldTowerCopper, ModItems.shieldTowerTin, ModItems.shieldTowerBronze, ModItems.shieldTowerSteel, ModItems.shieldTowerSilver, ModItems.shieldTowerLead, ModItems.shieldTowerNickel, ModItems.shieldTowerInvar,
				ModItems.shieldTowerConstantan, ModItems.shieldTowerPlatinum, ModItems.shieldTowerElectrum);
		
		tag(ModItemTags.shieldsWithBash).add(Items.SHIELD).addTags(ModItemTags.basicShields, ModItemTags.towerShields);
	}
}
