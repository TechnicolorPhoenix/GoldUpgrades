package com.oblivioussp.spartanshields.enchantment;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.init.ModSounds;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class PaybackEnchantment extends EnchantmentSS
{
	public static final String NBT_PAYBACK_DMG = "PaybackDamage";

	public PaybackEnchantment(Rarity rarityIn, EquipmentSlotType[] slots)
	{
		super(rarityIn, EnchantmentSS.TYPE_SHIELD_WITH_BASH, slots);
		this.setRegistryName(ModSpartanShields.ID, "payback");
	}
	
	@Override
	public int getMinCost(int enchantmentLevel) 
	{
		return 15 + (enchantmentLevel - 1) * 7;
	}
	
	@Override
	public int getMaxCost(int enchantmentLevel)
	{
		return getMinCost(enchantmentLevel) + 50;
	}
	
	@Override
	public int getMaxLevel()
	{
		return 4;
	}

	@Override
	public void onUserAttacked(LivingEntity user, Entity attacker, float damage, int level) 
	{
		ItemStack activeStack = user.getUseItem();
		if(!activeStack.isEmpty())
		{
			float currentDmg = activeStack.getOrCreateTag().getFloat(NBT_PAYBACK_DMG);
			float maxDmg = getMaxDamageCapacity(level);
			float absorbedDmg = damage * getAbsorbedDamageRatio();
			
			currentDmg = MathHelper.clamp(currentDmg + absorbedDmg, 0.0f, maxDmg);
			activeStack.getTag().putFloat(NBT_PAYBACK_DMG, currentDmg);
			
			// Let the player know that the shield is at maximum damage capacity
			if(currentDmg == maxDmg)
				user.level.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), ModSounds.SHIELD_PAYBACK_FULL_CHARGE, user.getSoundSource(), 0.5f, 2.0f);
			else
				user.level.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), ModSounds.SHIELD_PAYBACK_CHARGE, user.getSoundSource(), 0.5f, 1.0f);
		}
	}
	
	protected float getMaxDamageCapacity(int level)
	{
		return 2.0f * level;
	}
	
	protected float getAbsorbedDamageRatio()
	{
		return 0.5f;
	}

}
