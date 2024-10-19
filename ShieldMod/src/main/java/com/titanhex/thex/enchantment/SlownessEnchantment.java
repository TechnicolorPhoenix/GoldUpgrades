package com.titanhex.thex.enchantment;


import com.titanhex.thex.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import java.util.Random;

public class SlownessEnchantment extends EnchantmentSS
{
	public SlownessEnchantment(Rarity rarityIn, EquipmentSlotType... slots)
	{
		super(rarityIn, EnchantmentSS.TYPE_SHIELD, slots);
		//this.setName("spikes");
		this.setRegistryName("slowness");
	}
	
	/**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMinCost(int enchantmentLevel)
    {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    /**
     * Returns the maximum value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMaxCost(int enchantmentLevel)
    {
        return getMinCost(enchantmentLevel) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    @Override
    public int getMaxLevel()
    {
        return 5;
    }
    
    @Override
    public void onUserAttacked(LivingEntity user, Entity attacker, float damage, int level)
    {
        Random random = user.getRandom();
        ItemStack itemStack = EnchantmentHelper.getRandomItemWith(ModEnchantments.slowness, user).getValue();
        ItemStack activeStack = user.getUseItem();

        // Only deal damage when Shield is blocking (is active)
        if(!activeStack.isEmpty() && activeStack == itemStack)
        {
	        if (shouldHit(level, random))
	        {
	            int weaknessLevel = getDamage(level, random);
	            if (attacker != null && attacker instanceof LivingEntity )
                    ((LivingEntity) attacker).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, level, 2));
	
	            if (!itemStack.isEmpty())
	            	itemStack.hurtAndBreak(9, user, (livingEntity) -> livingEntity.broadcastBreakEvent(livingEntity.getUsedItemHand()));
	        }
	        else if (!itemStack.isEmpty())
	        	itemStack.hurtAndBreak(3, user, (livingEntity) -> livingEntity.broadcastBreakEvent(livingEntity.getUsedItemHand()));
        }
    }

    public static boolean shouldHit(int level, Random rnd)
    {
        return level <= 0 ? false : rnd.nextFloat() < 0.20F * (float)level;
    }

    public static int getDamage(int level, Random rnd)
    {
        return level;
    }
}
