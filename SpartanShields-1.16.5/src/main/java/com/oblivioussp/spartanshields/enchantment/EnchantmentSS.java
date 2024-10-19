package com.oblivioussp.spartanshields.enchantment;

import com.oblivioussp.spartanshields.item.ShieldBaseItem;
import com.oblivioussp.spartanshields.tags.ModItemTags;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public abstract class EnchantmentSS extends Enchantment
{
	public static EnchantmentType TYPE_SHIELD = EnchantmentType.create("ss_shield", (item) -> item instanceof ShieldBaseItem);
	public static EnchantmentType TYPE_SHIELD_WITH_BASH = EnchantmentType.create("ss_shield_with_bash", (item) -> ModItemTags.shieldsWithBash.contains(item));
	
	protected EnchantmentSS(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots)
	{
		super(rarityIn, typeIn, slots);
	}
	
	/**
	 * Called when a user with the enchanted item is attacked (regardless if the user is blocking)
	 * Allows the application of Spikes when the user is blocking.
	 * @param user
	 * @param attacker
	 * @param level
	 */
	public abstract void onUserAttacked(LivingEntity user, Entity attacker, float damage, int level);

}
