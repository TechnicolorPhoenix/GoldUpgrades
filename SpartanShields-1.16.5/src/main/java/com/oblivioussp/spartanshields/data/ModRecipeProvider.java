package com.oblivioussp.spartanshields.data;

import java.util.function.Consumer;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.data.recipes.ConditionalShapedRecipeBuilder;
import com.oblivioussp.spartanshields.data.recipes.PoweredShieldUpgradeRecipeBuilder;
import com.oblivioussp.spartanshields.data.recipes.ShieldBannerRecipeBuilder;
import com.oblivioussp.spartanshields.init.ModItems;
import com.oblivioussp.spartanshields.util.Constants;

import mekanism.common.registries.MekanismItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.SmithingRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeProvider extends RecipeProvider 
{

	public ModRecipeProvider(DataGenerator dataGen) 
	{
		super(dataGen);
	}
	
	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) 
	{
		INamedTag<Item> stick = ItemTags.bind("forge:rods/wooden");
		INamedTag<Item> cobblestone = ItemTags.bind("forge:cobblestone");
		INamedTag<Item> ironIngot = ItemTags.bind("forge:ingots/iron");
		INamedTag<Item> goldIngot = ItemTags.bind("forge:ingots/gold");
		INamedTag<Item> diamond = ItemTags.bind("forge:gems/diamond");
		INamedTag<Item> obsidian = ItemTags.bind("forge:obsidian");
		INamedTag<Item> netheriteIngot = ItemTags.bind("forge:ingots/netherite");
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
		INamedTag<Item> manaRune = ItemTags.bind("botania:runes/mana");
		INamedTag<Item> earthRune = ItemTags.bind("botania:runes/earth");
		INamedTag<Item> prideRune = ItemTags.bind("botania:runes/pride");
		INamedTag<Item> summerRune = ItemTags.bind("botania:runes/summer");
		
		INamedTag<Item> lapis = ItemTags.bind("forge:gems/lapis");
		INamedTag<Item> osmiumIngot = ItemTags.bind("forge:ingots/osmium");
		INamedTag<Item> refinedGlowstoneIngot = ItemTags.bind("forge:ingots/refined_glowstone");
		INamedTag<Item> refinedObsidianIngot = ItemTags.bind("forge:ingots/refined_obsidian");
		
		ShapedRecipeBuilder.shaped(ModItems.shieldWood).group(ModSpartanShields.ID + ":shield_wood").define('|', stick).define('#', ItemTags.PLANKS).pattern("## ").pattern("#|#").pattern(" ##").unlockedBy("has_wood_planks", has(ItemTags.PLANKS)).save(consumer);
		
		upgradeRecipe(consumer, "shield_stone", ModItems.shieldWood, cobblestone, ModItems.shieldStone, "has_cobblestone");
		upgradeRecipe(consumer, "shield_iron", ModItems.shieldWood, ironIngot, ModItems.shieldIron, "has_iron_ingot");
		upgradeRecipe(consumer, "shield_gold", ModItems.shieldWood, goldIngot, ModItems.shieldGold, "has_gold_ingot");
		upgradeRecipe(consumer, "shield_diamond", ModItems.shieldWood, diamond, ModItems.shieldDiamond, "has_diamond");
		SmithingRecipeBuilder.smithing(Ingredient.of(ModItems.shieldDiamond), Ingredient.of(netheriteIngot), ModItems.shieldNetherite).unlocks("has_netherite_ingot", has(netheriteIngot)).save(consumer, ForgeRegistries.ITEMS.getKey(ModItems.shieldNetherite) + "_smithing");
		upgradeRecipe(consumer, "shield_obsidian", ModItems.shieldWood, obsidian, ModItems.shieldObsidian, "has_obsidian");
		conditionalUpgradeRecipe(consumer, "shield_copper", ModItems.shieldWood, copperIngot, ModItems.shieldCopper, "has_copper_ingot");
		conditionalUpgradeRecipe(consumer, "shield_tin", ModItems.shieldWood, tinIngot, ModItems.shieldTin, "has_tin_ingot");
		conditionalUpgradeRecipe(consumer, "shield_bronze", ModItems.shieldWood, bronzeIngot, ModItems.shieldBronze, "has_bronze_ingot");
		conditionalUpgradeRecipe(consumer, "shield_steel", ModItems.shieldWood, steelIngot, ModItems.shieldSteel, "has_steel_ingot");
		conditionalUpgradeRecipe(consumer, "shield_silver", ModItems.shieldWood, silverIngot, ModItems.shieldSilver, "has_silver_ingot");
		conditionalUpgradeRecipe(consumer, "shield_lead", ModItems.shieldWood, leadIngot, ModItems.shieldLead, "has_lead_ingot");
		conditionalUpgradeRecipe(consumer, "shield_nickel", ModItems.shieldWood, nickelIngot, ModItems.shieldNickel, "has_nickel_ingot");
		conditionalUpgradeRecipe(consumer, "shield_invar", ModItems.shieldWood, invarIngot, ModItems.shieldInvar, "has_invar_ingot");
		conditionalUpgradeRecipe(consumer, "shield_constantan", ModItems.shieldWood, constantanIngot, ModItems.shieldConstantan, "has_constantan_ingot");
		conditionalUpgradeRecipe(consumer, "shield_platinum", ModItems.shieldWood, platinumIngot, ModItems.shieldPlatinum, "has_platinum_ingot");
		conditionalUpgradeRecipe(consumer, "shield_electrum", ModItems.shieldWood, electrumIngot, ModItems.shieldElectrum, "has_electrum_ingot");
		
		ShapedRecipeBuilder.shaped(ModItems.shieldTowerWood).group(ModSpartanShields.ID + ":shield_wood").define('|', stick).define('#', ItemTags.PLANKS).pattern("###").pattern("#|#").pattern(" # ").unlockedBy("has_wood_planks", has(ItemTags.PLANKS)).save(consumer);
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerWood).save(consumer);
		
		upgradeRecipe(consumer, "shield_stone", ModItems.shieldTowerWood, cobblestone, ModItems.shieldTowerStone, "has_cobblestone");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerStone).save(consumer);
		upgradeRecipe(consumer, "shield_iron", ModItems.shieldTowerWood, ironIngot, ModItems.shieldTowerIron, "has_iron_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerIron).save(consumer);
		upgradeRecipe(consumer, "shield_gold", ModItems.shieldTowerWood, goldIngot, ModItems.shieldTowerGold, "has_gold_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerGold).save(consumer);
		upgradeRecipe(consumer, "shield_diamond", ModItems.shieldTowerWood, diamond, ModItems.shieldTowerDiamond, "has_diamond");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerDiamond).save(consumer);
		SmithingRecipeBuilder.smithing(Ingredient.of(ModItems.shieldTowerDiamond), Ingredient.of(netheriteIngot), ModItems.shieldTowerNetherite).unlocks("has_netherite_ingot", has(netheriteIngot)).save(consumer, ForgeRegistries.ITEMS.getKey(ModItems.shieldTowerNetherite) + "_smithing");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerNetherite).save(consumer);
		upgradeRecipe(consumer, "shield_obsidian", ModItems.shieldTowerWood, obsidian, ModItems.shieldTowerObsidian, "has_obsidian");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerObsidian).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_copper", ModItems.shieldTowerWood, copperIngot, ModItems.shieldTowerCopper, "has_copper_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerCopper).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_tin", ModItems.shieldTowerWood, tinIngot, ModItems.shieldTowerTin, "has_tin_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerTin).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_bronze", ModItems.shieldTowerWood, bronzeIngot, ModItems.shieldTowerBronze, "has_bronze_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerBronze).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_steel", ModItems.shieldTowerWood, steelIngot, ModItems.shieldTowerSteel, "has_steel_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerSteel).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_silver", ModItems.shieldTowerWood, silverIngot, ModItems.shieldTowerSilver, "has_silver_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerSilver).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_lead", ModItems.shieldTowerWood, leadIngot, ModItems.shieldTowerLead, "has_lead_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerLead).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_nickel", ModItems.shieldTowerWood, nickelIngot, ModItems.shieldTowerNickel, "has_nickel_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerNickel).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_invar", ModItems.shieldTowerWood, invarIngot, ModItems.shieldTowerInvar, "has_invar_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerInvar).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_constantan", ModItems.shieldTowerWood, constantanIngot, ModItems.shieldTowerConstantan, "has_constantan_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerConstantan).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_platinum", ModItems.shieldTowerWood, platinumIngot, ModItems.shieldTowerPlatinum, "has_platinum_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerPlatinum).save(consumer);
		conditionalUpgradeRecipe(consumer, "shield_electrum", ModItems.shieldTowerWood, electrumIngot, ModItems.shieldTowerElectrum, "has_electrum_ingot");
		ShieldBannerRecipeBuilder.bannerRecipe(ModItems.shieldTowerElectrum).save(consumer);
		
		ConditionalShapedRecipeBuilder.shaped(ModItems.shieldManasteel).define('#', manasteelIngot).define('|', vazkii.botania.common.item.ModItems.livingwoodTwig).define('E', earthRune).define('M', manaRune).pattern("#E#").pattern("#|#").pattern(" M ").unlockedBy("has_manasteel", has(manasteelIngot)).condition(new ModLoadedCondition(Constants.Botania_ModID)).save(consumer);
		ConditionalShapedRecipeBuilder.shaped(ModItems.shieldTerrasteel).define('#', terrasteelIngot).define('|', vazkii.botania.common.item.ModItems.livingwoodTwig).define('P', prideRune).define('M', manaRune).pattern("#P#").pattern("#|#").pattern(" M ").unlockedBy("has_manasteel", has(terrasteelIngot)).condition(new ModLoadedCondition(Constants.Botania_ModID)).save(consumer);
		ConditionalShapedRecipeBuilder.shaped(ModItems.shieldElementium).define('#', elementiumIngot).define('|', vazkii.botania.common.item.ModItems.dreamwoodTwig).define('S', summerRune).define('M', manaRune).pattern("#S#").pattern("#|#").pattern(" M ").unlockedBy("has_manasteel", has(elementiumIngot)).condition(new ModLoadedCondition(Constants.Botania_ModID)).save(consumer);
	
		ConditionalShapedRecipeBuilder.shaped(ModItems.shieldOsmium).define('@', ModItems.shieldWood).define('#', osmiumIngot).pattern(" # ").pattern("#@#").pattern(" # ").unlockedBy("has_osmium_ingot", has(osmiumIngot)).condition(new ModLoadedCondition("mekanism")).save(consumer);		
		ConditionalShapedRecipeBuilder.shaped(ModItems.shieldLapisLazuli).define('@', ModItems.shieldWood).define('#', lapis).pattern(" # ").pattern("#@#").pattern(" # ").unlockedBy("has_lapis", has(lapis)).condition(new ModLoadedCondition("mekanism")).save(consumer);		
		ConditionalShapedRecipeBuilder.shaped(ModItems.shieldRefinedGlowstone).define('@', ModItems.shieldWood).define('#', refinedGlowstoneIngot).pattern(" # ").pattern("#@#").pattern(" # ").unlockedBy("has_refined_glowstone_ingot", has(refinedGlowstoneIngot)).condition(new ModLoadedCondition("mekanism")).save(consumer);		
		ConditionalShapedRecipeBuilder.shaped(ModItems.shieldRefinedObsidian).define('@', ModItems.shieldWood).define('#', refinedObsidianIngot).pattern(" # ").pattern("#@#").pattern(" # ").unlockedBy("has_refined_obsidian_ingot", has(refinedObsidianIngot)).condition(new ModLoadedCondition("mekanism")).save(consumer);		
		
		ConditionalShapedRecipeBuilder.shaped(ModItems.shieldPoweredMekanismBasic).define('A', MekanismItems.INFUSED_ALLOY).define('D', MekanismItems.ENRICHED_DIAMOND).define('C', MekanismItems.BASIC_CONTROL_CIRCUIT).define('B', MekanismItems.ENERGY_TABLET).define('#', steelIngot).pattern("ADA").pattern("CBC").pattern("#B#").unlockedBy("has_infused_alloy", has(MekanismItems.INFUSED_ALLOY)).condition(new ModLoadedCondition("mekanism")).save(consumer);
		PoweredShieldUpgradeRecipeBuilder.shaped(ModItems.shieldPoweredMekanismAdvanced).define('S', ModItems.shieldPoweredMekanismBasic).define('A', MekanismItems.REINFORCED_ALLOY).define('C', MekanismItems.ADVANCED_CONTROL_CIRCUIT).define('B', MekanismItems.ENERGY_TABLET).define('#', steelIngot).pattern("ASA").pattern("CBC").pattern("#B#").unlockedBy("has_reinforced_alloy", has(MekanismItems.REINFORCED_ALLOY)).unlockedBy("has_basic_mekanist_shield", has(ModItems.shieldPoweredMekanismBasic)).condition(new ModLoadedCondition("mekanism")).save(consumer);
		PoweredShieldUpgradeRecipeBuilder.shaped(ModItems.shieldPoweredMekanismElite).define('S', ModItems.shieldPoweredMekanismAdvanced).define('A', MekanismItems.ATOMIC_ALLOY).define('C', MekanismItems.ELITE_CONTROL_CIRCUIT).define('B', MekanismItems.ENERGY_TABLET).define('#', steelIngot).pattern("ASA").pattern("CBC").pattern("#B#").unlockedBy("has_atomic_alloy", has(MekanismItems.ATOMIC_ALLOY)).unlockedBy("has_advanced_mekanist_shield", has(ModItems.shieldPoweredMekanismAdvanced)).condition(new ModLoadedCondition("mekanism")).save(consumer);
		PoweredShieldUpgradeRecipeBuilder.shaped(ModItems.shieldPoweredMekanismUltimate).define('S', ModItems.shieldPoweredMekanismElite).define('A', MekanismItems.ATOMIC_ALLOY).define('C', MekanismItems.ULTIMATE_CONTROL_CIRCUIT).define('B', MekanismItems.ENERGY_TABLET).define('#', steelIngot).pattern("ASA").pattern("CBC").pattern("#B#").unlockedBy("has_atomic_alloy", has(MekanismItems.ATOMIC_ALLOY)).unlockedBy("has_elite_mekanist_shield", has(ModItems.shieldPoweredMekanismElite)).condition(new ModLoadedCondition("mekanism")).save(consumer);
	}
	
	private void upgradeRecipe(Consumer<IFinishedRecipe> consumer, String groupName, IItemProvider upgradeFrom, INamedTag<Item> upgradeMaterial, IItemProvider upgradeTo, String unlockName)
	{
		ShapedRecipeBuilder.shaped(upgradeTo).group(ModSpartanShields.ID + ":" + groupName).define('@', upgradeFrom).define('#', upgradeMaterial).pattern(" # ").pattern("#@#").pattern(" # ").unlockedBy(unlockName, has(upgradeMaterial)).save(consumer);
	}
	
	private void conditionalUpgradeRecipe(Consumer<IFinishedRecipe> consumer, String groupName, IItemProvider upgradeFrom, INamedTag<Item> upgradeMaterial, IItemProvider upgradeTo, String unlockName)
	{
		ConditionalShapedRecipeBuilder.shaped(upgradeTo).group(ModSpartanShields.ID + ":" + groupName).define('@', upgradeFrom).define('#', upgradeMaterial).pattern(" # ").pattern("#@#").pattern(" # ").unlockedBy(unlockName, has(upgradeMaterial)).condition(new NotCondition(new TagEmptyCondition(upgradeMaterial.getName().toString()))).save(consumer);
	}
	
	@Override
	public String getName()
	{
		return ModSpartanShields.NAME + " Recipes";
	}

}
