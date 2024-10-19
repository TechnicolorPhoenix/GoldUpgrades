package com.titanhex.thex.enchantment;

import com.titanhex.thex.item.ShieldBaseItem;
import com.titanhex.thex.tags.ModItemTags;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public abstract class EnchantmentSS extends Enchantment
{
	public static EnchantmentType TYPE_SHIELD = EnchantmentType.create("thex_shield", (item) -> item instanceof ShieldBaseItem);

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
