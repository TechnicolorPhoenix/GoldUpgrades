package com.titanhex.thex.item;

import java.util.List;

import javax.annotation.Nullable;

import com.titanhex.thex.ModThexShields;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShieldBaseItem extends ShieldItem
{
    protected int maxDmg;

	public ShieldBaseItem(String unlocName, int defaultMaxDamage, Item.Properties prop)
	{
		super(prop.defaultDurability(defaultMaxDamage));
		this.setRegistryName(ModThexShields.ID, unlocName);
		maxDmg = defaultMaxDamage;
	}

    /*@Override
    public String getTranslationKey(ItemStack stack)
    {
        return this.getTranslationKey();
    }*/
    
    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair)
    {
    	return false;
    }
    
    @Override
    public boolean isShield(ItemStack stack, @Nullable LivingEntity entity)
    {
    	return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        return maxDmg;
    }

    public void setMaxDamage(int maxDamage)
    {
        maxDmg = maxDamage;
    }

    @OnlyIn(Dist.CLIENT)
    public void addEffectsTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) 
    {
/*
    	if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.payback, stack) != 0)
    	{
    		float paybackDamage = stack.getOrCreateTag().getFloat(PaybackEnchantment.NBT_PAYBACK_DMG);
    		tooltip.add(new TranslationTextComponent("tooltip." + ModThexShields.ID + ".payback_bonus", TextFormatting.GRAY.toString() + Float.toString(paybackDamage)).withStyle(TextFormatting.LIGHT_PURPLE));
    	}
*/
    }
}
