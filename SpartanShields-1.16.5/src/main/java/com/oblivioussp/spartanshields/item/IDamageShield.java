package com.oblivioussp.spartanshields.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IDamageShield 
{
	public void damageShield(ItemStack shieldStack, PlayerEntity player, Entity attacker, float damage);
}
