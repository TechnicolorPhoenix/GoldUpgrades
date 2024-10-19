package com.oblivioussp.spartanshields.item;

import java.util.List;

import javax.annotation.Nullable;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.util.Config;
import com.oblivioussp.spartanshields.util.ItemTierSS;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ExternalModShieldItem extends BasicShieldItem
{
	protected String modName;

	public ExternalModShieldItem(String unlocName, ItemTierSS toolMaterial, int defaultMaxDamage, String externalModName, Item.Properties prop) 
	{
		super(unlocName, toolMaterial, defaultMaxDamage, prop);
		modName = externalModName;
	}

	/**
     * allows items to add custom lines of information to the mouseover description
     */
	@OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
    	if(!Config.INSTANCE.forceDisableUncraftableTooltips.get())
    	{
    		tooltip.add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + ".cant_craft_missing_mods", I18n.get("mod." + ModSpartanShields.ID + "." + modName)).withStyle(TextFormatting.RED));
    	}
    	
    	super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
