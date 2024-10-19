package com.oblivioussp.spartanshields.item;

import java.util.List;

import javax.annotation.Nullable;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.util.ItemTierSS;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SilverShieldItem extends BasicShieldItem implements IDamageShield
{
	
	public SilverShieldItem(String unlocName, ItemTierSS toolMaterial, int defaultMaxDamage, Item.Properties prop) 
	{
		super(unlocName, toolMaterial, defaultMaxDamage, prop);
	}
	
    @OnlyIn(Dist.CLIENT)
    @Override
    public void addEffectsTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
    	tooltip.add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + ".on_block", I18n.get("tooltip." + ModSpartanShields.ID + ".shield_silver.desc")).withStyle(TextFormatting.GOLD));
    }

	/**
     * Allows shields to take damage. Called from EventHandlerSS
     * @param shieldStack The Shield ItemStack
     * @param player The Player wielding the shield
     * @param damage The damage taken
     */
    @Override
    public void damageShield(ItemStack shieldStack, PlayerEntity player, Entity attacker, float damage)
    {
    	// Damage undead mobs that attack directly
    	if(attacker instanceof LivingEntity)
    	{
    		LivingEntity attackerLiving = (LivingEntity)attacker;
    		
    		if(attackerLiving.getMobType() == CreatureAttribute.UNDEAD)
    		{
    			attackerLiving.hurt(DamageSource.playerAttack(player), 2.0f);
    		}
    	}
    }
}
