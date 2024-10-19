package com.oblivioussp.spartanshields.item;

import java.util.List;

import javax.annotation.Nullable;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.client.ModKeyBinds;
import com.oblivioussp.spartanshields.enchantment.PaybackEnchantment;
import com.oblivioussp.spartanshields.init.ModEnchantments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShieldBaseItem extends ShieldItem
{
    protected int maxDmg;

	public ShieldBaseItem(String unlocName, int defaultMaxDamage, Item.Properties prop)
	{
		super(prop.defaultDurability(defaultMaxDamage));
		this.setRegistryName(ModSpartanShields.ID, unlocName);
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
    	if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.payback, stack) != 0)
    	{
    		float paybackDamage = stack.getOrCreateTag().getFloat(PaybackEnchantment.NBT_PAYBACK_DMG);
    		tooltip.add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + ".payback_bonus", TextFormatting.GRAY.toString() + Float.toString(paybackDamage)).withStyle(TextFormatting.LIGHT_PURPLE));
    	}
    }
	
    @OnlyIn(Dist.CLIENT)
    public void addShieldBashTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
    	KeyBinding boundKey = ModKeyBinds.KEY_ALT_SHIELD_BASH.isUnbound() ? Minecraft.getInstance().options.keyAttack : ModKeyBinds.KEY_ALT_SHIELD_BASH;
    	tooltip.add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + ".shield_bash", TextFormatting.AQUA + I18n.get(boundKey.getTranslatedKeyMessage().getString().toUpperCase()) + TextFormatting.BLUE).withStyle(TextFormatting.BLUE));
    }
}
