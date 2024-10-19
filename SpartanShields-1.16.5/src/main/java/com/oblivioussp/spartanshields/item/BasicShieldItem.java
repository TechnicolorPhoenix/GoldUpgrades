package com.oblivioussp.spartanshields.item;

import java.util.List;

import javax.annotation.Nullable;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.client.ClientHelper;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class BasicShieldItem extends ShieldBaseItem
{
	protected IItemTier material;	// The base material used for this shield

	public BasicShieldItem(String unlocName, IItemTier toolMaterial, int defaultMaxDamage, Item.Properties prop)
	{
		super(unlocName, defaultMaxDamage, prop);
		this.material = toolMaterial;
		
		if(FMLEnvironment.dist.isClient())
			ClientHelper.registerShieldPropertyOverrides(this);
	}
	/**
     * allows items to add custom lines of information to the mouseover description
     */
	@OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
		tooltip.add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + ".protection", this.getMaxDamage(stack)));
    	
    	if(!stack.isEmpty() && stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
    	{
    		DyeColor dyeColor = ShieldItem.getColor(stack);
    		tooltip.add(new StringTextComponent(""));
    		tooltip.add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + ".has_patterns"));
    		tooltip.add(new TranslationTextComponent(String.format("block.minecraft.%s_banner", dyeColor.name().toLowerCase())).withStyle(TextFormatting.BOLD, TextFormatting.GRAY));
    		BannerItem.appendHoverTextFromBannerBlockEntityTag(stack, tooltip);
    	}
    	
    	addEffectsTooltip(stack, worldIn, tooltip, flagIn);

//    	this.addShieldBashTooltip(stack, worldIn, tooltip, flagIn);
    }
	
	@Override
	public String getDescriptionId(ItemStack stack) 
	{
		return getOrCreateDescriptionId();
	}

	/**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    @Override
    public int getEnchantmentValue()
    {
        return this.material.getEnchantmentValue();
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair)
    {
    	return material.getRepairIngredient().test(repair) || super.isValidRepairItem(toRepair, repair);
    }
}
