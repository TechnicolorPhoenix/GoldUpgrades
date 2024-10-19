package com.oblivioussp.spartanshields.item;

import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

public class BotaniaShieldItem extends BasicShieldItem implements IManaUsingItem
{
	protected int manaPerDamage = 60;
	
	public BotaniaShieldItem(String unlocName, IItemTier toolMaterial, int defaultMaxDamage, int manaPerDamage, Properties prop) 
	{
		super(unlocName, toolMaterial, defaultMaxDamage, prop);
		this.manaPerDamage = manaPerDamage;
	}

	@Override
	public boolean usesMana(ItemStack arg0) 
	{
		return true;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) 
	{
		if(!worldIn.isClientSide && entityIn instanceof PlayerEntity && 
				stack.getDamageValue() > 0 && 
				ManaItemHandler.instance().requestManaExactForTool(stack, (PlayerEntity)entityIn, manaPerDamage * 2, true))
		{
			stack.setDamageValue(stack.getDamageValue() - 1);
		}
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
	{
		return ToolCommons.damageItemIfPossible(stack, amount, entity, manaPerDamage);
	}
}
