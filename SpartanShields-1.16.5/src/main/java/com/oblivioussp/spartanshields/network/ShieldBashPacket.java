package com.oblivioussp.spartanshields.network;

import java.util.function.Supplier;

import com.oblivioussp.spartanshields.enchantment.PaybackEnchantment;
import com.oblivioussp.spartanshields.init.ModEnchantments;
import com.oblivioussp.spartanshields.init.ModSounds;
import com.oblivioussp.spartanshields.tags.ModItemTags;
import com.oblivioussp.spartanshields.util.Config;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.NetworkEvent;

public class ShieldBashPacket
{
	protected Hand hand;
	protected int entityId;
	protected boolean attackEntity = false;
	
	public ShieldBashPacket(Hand handIn, int entityIdIn, boolean attackEntityIn)
	{
		hand = handIn;
		entityId = entityIdIn;
		attackEntity = attackEntityIn;
	}
	
	public static void encode(ShieldBashPacket packet, PacketBuffer buf)
	{
		buf.writeEnum(packet.hand);
		buf.writeInt(packet.entityId);
		buf.writeBoolean(packet.attackEntity);
	}
	
	public static ShieldBashPacket decode(PacketBuffer buf)
	{
		return new ShieldBashPacket(buf.readEnum(Hand.class), buf.readInt(), buf.readBoolean());
	}
	
	public static class Handler
	{
		public static void handle(final ShieldBashPacket packet, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> 
			{
				ServerPlayerEntity player = ctx.get().getSender();
				Entity victim = player.level.getEntity(packet.entityId);
				
				if(player.isBlocking())
				{
					ItemStack shieldStack = player.getItemInHand(packet.hand);
					
//					if(!shieldStack.isEmpty() && !player.getCooldowns().isOnCooldown(shieldStack.getItem()) && shieldStack.getItem() instanceof ShieldBaseItem)
					if(!shieldStack.isEmpty() && !player.getCooldowns().isOnCooldown(shieldStack.getItem()) && 
							ModItemTags.shieldsWithBash.contains(player.getUseItem().getItem()) && player.getUseItem().getItem().isShield(player.getUseItem(), player))
					{
						if(packet.attackEntity && victim != null && victim instanceof LivingEntity)
						{
							// Deal minimal damage and knock back foes
							int knockLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, shieldStack);
							victim.invulnerableTime = 0;
							((LivingEntity)victim).knockback(1.0f + (knockLvl), (double)MathHelper.sin(player.yRot * 0.017453292F), (double)(-MathHelper.cos(player.yRot * 0.017453292F)));
							
							float bashDamage = 1.0f;
							// Apply the Payback damage bonus if necessary
							if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.payback, shieldStack) != 0)
							{
								bashDamage += shieldStack.getOrCreateTag().getFloat(PaybackEnchantment.NBT_PAYBACK_DMG);
								shieldStack.getTag().putFloat(PaybackEnchantment.NBT_PAYBACK_DMG, 0.0f);
							}
							
							// Finally deal damage.
							victim.hurt(DamageSource.playerAttack(player), bashDamage);
							shieldStack.hurtAndBreak(5, player, (entity) -> entity.broadcastBreakEvent(packet.hand));
							
							// Set foes on fire when hit with the Shield Bash
							int firebrandLvl = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.firebrand, shieldStack);
							if(firebrandLvl != 0)
								victim.setSecondsOnFire(firebrandLvl == 2 ? 10 : 5);
							
							// Increase the pitch whenever the bash damage is higher
							player.level.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), ModSounds.SHIELD_BASH_HIT, player.getSoundSource(), 1.0F, bashDamage == 1.0f ? 1.0f : 2.0f);
							player.crit(victim);
						}
						else
						{
							// ...swing and a miss...
							player.level.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), ModSounds.SHIELD_BASH_MISS, player.getSoundSource(), 0.5f, 0.01f);
						}
						player.stopUsingItem();
						player.swing(packet.hand);
						player.getCooldowns().addCooldown(shieldStack.getItem(), Config.INSTANCE.cooldownShieldBash.get());

					}
				}
			});
		}
	}
}