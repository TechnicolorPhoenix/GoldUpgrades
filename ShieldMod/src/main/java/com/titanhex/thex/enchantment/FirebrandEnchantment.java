package com.titanhex.thex.enchantment;

import com.titanhex.thex.ModThexShields;

import com.titanhex.thex.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class FirebrandEnchantment extends EnchantmentSS
{

	public FirebrandEnchantment(Rarity rarityIn, EquipmentSlotType... slots) 
	{
		super(rarityIn, EnchantmentSS.TYPE_SHIELD, slots);
		this.setRegistryName(ModThexShields.ID, "firebrand");
	}
	
    @Override
    public int getMinCost(int enchantmentLevel)
    {
        return 10 + 20 * (enchantmentLevel - 1);
    }
    
    @Override
    public int getMaxCost(int enchantmentLevel)
    {
        return getMinCost(enchantmentLevel) + 50;
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }

    @Override
    public void onUserAttacked(LivingEntity user, Entity attacker, float damage, int level)
	{
        ItemStack activeStack = user.getUseItem();

        if(activeStack.isEmpty() || attacker == null)	return;
        
        attacker.setSecondsOnFire(level == 2 ? 10 : 5);
	}

}
