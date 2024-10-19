package com.oblivioussp.spartanshields.item;

import net.minecraft.item.ItemStack;

public interface IItemPoweredFE
{
	public int receiveFE(ItemStack stack, int maxReceive, boolean simulate);
	public int extractFE(ItemStack stack, int maxExtract, boolean simulate);

	public int getFEStored(ItemStack stack) ;
	public int getFECapacity(ItemStack stack);
	
	public boolean canExtractFE(ItemStack stack);
	public boolean canReceiveFE(ItemStack stack);
}
