package com.oblivioussp.spartanshields.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.oblivioussp.spartanshields.util.Constants;
import com.oblivioussp.spartanshields.util.ItemTierSS;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ObsidianShieldItem extends BasicShieldItem 
{
	public ObsidianShieldItem(String unlocName, ItemTierSS toolMaterial, int defaultMaxDamage, Item.Properties prop) 
	{
		super(unlocName, toolMaterial, defaultMaxDamage, prop);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack)
    {
		Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.<Attribute, AttributeModifier>create();
		
		if (slot == EquipmentSlotType.MAINHAND || slot == EquipmentSlotType.OFFHAND)
        {
			modifiers.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(Constants.SHIELD_MOVE_SPEED_UUID, "Shield modifier", -0.3, Operation.MULTIPLY_TOTAL));
			modifiers.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(Constants.SHIELD_KNOCKBACK_UUID, "Shield modifier", 0.5, Operation.ADDITION));
        }
		
		return modifiers;
    }
}
