package com.oblivioussp.spartanshields.util;

import java.util.function.Supplier;

import net.minecraft.block.Blocks;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;

public class ItemTierSS implements IItemTier 
{
	public static final ItemTierSS OBSIDIAN = new ItemTierSS(3, 1024, 5.0f, 1.5f, 5, () -> {
		return Ingredient.of(new ItemStack(Blocks.OBSIDIAN, 1));
		});
	
	public static final ItemTierSS COPPER = new ItemTierSS(1, 200, 5.0f, 1.5f, 8, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/copper")));
		});
	public static final ItemTierSS TIN = new ItemTierSS(1, 180, 5.25f, 1.75f, 6, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/tin")));
		});
	public static final ItemTierSS BRONZE = new ItemTierSS(2, 320, 5.75f, 2.0f, 12, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/bronze")));
		});
	public static final ItemTierSS STEEL = new ItemTierSS(3, 480, 6.5f, 3.0f, 14, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/steel")));
		});
	public static final ItemTierSS SILVER = new ItemTierSS(1, 48, 5.0f, 1.5f, 16, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/silver")));
		});
	public static final ItemTierSS LEAD = new ItemTierSS(1, 240, 4.5f, 2.0f, 5, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/lead")));
		});
	public static final ItemTierSS NICKEL = new ItemTierSS(1, 200, 5.0f, 2.0f, 6, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/nickel")));
		});
	public static final ItemTierSS INVAR = new ItemTierSS(2, 440, 6.0f, 2.2f, 12, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/invar")));
		});
	public static final ItemTierSS CONSTANTAN = new ItemTierSS(2, 300, 5.5f, 2.75f, 7, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/constantan")));
		});
	public static final ItemTierSS PLATINUM = new ItemTierSS(3, 1024, 4.0f, 4.0f, 18, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/platinum")));
		});
	public static final ItemTierSS ELECTRUM = new ItemTierSS(1, 180, 3.5f, 3.5f, 16, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/electrum")));
		});
	public static final ItemTierSS MANASTEEL = new ItemTierSS(3, 300, 6.2f, 2.0f, 20, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/manasteel")));
		});
	public static final ItemTierSS TERRASTEEL = new ItemTierSS(4, 2300, 9.0f, 3.0f, 26, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/terrasteel")));
		});
	public static final ItemTierSS ELEMENTIUM = new ItemTierSS(3, 720, 6.2f, 2.0f, 20, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/elementium")));
		});
	public static final ItemTierSS OSMIUM = new ItemTierSS(2, 750, 10.0f, 2.0f, 12, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/osmium")));
		});
	public static final ItemTierSS LAPIS_LAZULI = new ItemTierSS(2, 200, 5.0f, 2.0f, 8, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:gems/lapis")));
		});
	public static final ItemTierSS REFINED_GLOWSTONE = new ItemTierSS(2, 300, 14.0f, 2.0f, 18, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/refined_glowstone")));
		});
	public static final ItemTierSS REFINED_OBSIDIAN = new ItemTierSS(3, 2500, 20.0f, 10.0f, 40, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:ingots/refined_obsidian")));
		});
	
	// TODO: Adjust this to match Dragonbone tools
	public static final ItemTierSS DRAGONBONE = new ItemTierSS(3, 2500, 20.0f, 10.0f, 40, () -> {
		return Ingredient.of(ItemTags.getAllTags().getTag(new ResourceLocation("forge:bones/dragon")));
		});

	
	/** The level of material this tool can harvest (3 = DIAMOND, 2 = IRON, 1 = STONE, 0 = WOOD/GOLD) */
	private final int harvestLevel;
	/** The number of uses this material allows. (wood = 59, stone = 131, iron = 250, diamond = 1561, gold = 32) */
	private final int maxUses;
   	/** The strength of this tool material against blocks which it is effective against. */
   	private final float efficiency;
   	/** Damage versus entities. */
   	private final float attackDamage;
   	/** Defines the natural enchantability factor of the material. */
   	private final int enchantability;
   	private final LazyValue<Ingredient> repairMaterial;
   
   	public ItemTierSS(int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Supplier<Ingredient> repairMaterialIn)
   	{
   		harvestLevel = harvestLevelIn;
   		maxUses = maxUsesIn;
   		efficiency = efficiencyIn;
   		attackDamage = attackDamageIn;
   		enchantability = enchantabilityIn;
   		repairMaterial = new LazyValue<>(repairMaterialIn);
   	}
	
	@Override
	public int getUses() 
	{
		return maxUses;
	}

	@Override
	public float getSpeed() 
	{
		return efficiency;
	}

	@Override
	public float getAttackDamageBonus() 
	{
		return attackDamage;
	}

	@Override
	public int getLevel() 
	{
		return harvestLevel;
	}

	@Override
	public int getEnchantmentValue() 
	{
		return enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() 
	{
		return repairMaterial.get();
	}
}
