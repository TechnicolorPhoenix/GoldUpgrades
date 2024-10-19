package com.oblivioussp.spartanshields.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.core.handler.PixieHandler;

public class ElementiumShieldItem extends BotaniaShieldItem
{

	public ElementiumShieldItem(String unlocName, IItemTier toolMaterial, int defaultMaxDamage, int manaPerDamage,
			Properties prop) 
	{
		super(unlocName, toolMaterial, defaultMaxDamage, manaPerDamage, prop);
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack)
	{
		Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.<Attribute, AttributeModifier>create();
		if(slot == EquipmentSlotType.OFFHAND || slot == EquipmentSlotType.MAINHAND)
			modifiers.put(PixieHandler.PIXIE_SPAWN_CHANCE, PixieHandler.makeModifier(slot, "Shield modifier", 0.1));
		return modifiers;
	}

}
