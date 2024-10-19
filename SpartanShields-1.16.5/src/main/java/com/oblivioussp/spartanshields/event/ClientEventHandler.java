package com.oblivioussp.spartanshields.event;

import com.oblivioussp.spartanshields.ModSpartanShields;
import com.oblivioussp.spartanshields.client.ModKeyBinds;
import com.oblivioussp.spartanshields.network.NetworkHandler;
import com.oblivioussp.spartanshields.network.ShieldBashPacket;
import com.oblivioussp.spartanshields.tags.ModItemTags;
import com.oblivioussp.spartanshields.util.Config;
import com.oblivioussp.spartanshields.util.Log;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler 
{
	//public static boolean alphaMessage = false;

	@SubscribeEvent
	public static void onMouseInputEvent(MouseInputEvent ev)
	{
		checkForShieldBash();
	}
	
	@SubscribeEvent
	public static void onKeyboardInputEvent(KeyInputEvent ev)
	{
		checkForShieldBash();
	}
	
	protected static void checkForShieldBash()
	{
		Minecraft mc = Minecraft.getInstance();
		
		PlayerEntity player = mc.player;

		// Ensure the following
		// - The game is NOT paused
		// - The game is NOT in any GUI
		// - The game is loaded into a world
		// - The player is valid. If there is no valid player, do not execute this event as it will cause a crash
		// If not, then don't continue the attack
		if(Minecraft.getInstance().level == null || Minecraft.getInstance().screen != null || Minecraft.getInstance().isPaused() || player == null)
			return;
		
		if(!Config.INSTANCE.disableShieldBash.get() && player.isBlocking() && (ModKeyBinds.KEY_ALT_SHIELD_BASH.isUnbound() ? mc.options.keyAttack.isDown() : ModKeyBinds.KEY_ALT_SHIELD_BASH.isDown()))
		{
			ItemStack shieldStack = ItemStack.EMPTY;
			Hand shieldHand = null;
			// TODO: Add a bashable shield tag to allow non-Spartan Shields Shields to work with this
//			if(player.getUseItem().getItem() instanceof ShieldBaseItem)
			if(ModItemTags.shieldsWithBash.contains(player.getUseItem().getItem()) && player.getUseItem().getItem().isShield(player.getUseItem(), player))
			{
				shieldStack = player.getUseItem();
				shieldHand = player.getUsedItemHand();
			}
			else
				return;
			
			if(player.getCooldowns().isOnCooldown(shieldStack.getItem()))
				return;
			
			RayTraceResult result = getEntityMouseOverExtended(5.0f);
			
			if(result != null)
			{
				int entId = -1;
				boolean attackEntity = true;
				EntityRayTraceResult entityRayTrace = null;
				if(result instanceof EntityRayTraceResult)
					entityRayTrace = (EntityRayTraceResult)result;

				if(entityRayTrace != null && entityRayTrace.getEntity() != null && entityRayTrace.getEntity() != player)
				{
					Log.debug("Hit Entity with Shield Bash! - " + entityRayTrace.getEntity().toString());
					entId = entityRayTrace.getEntity().getId();
				}
				
				if(entId == -1)
				{
					entId = 0;
					attackEntity = false;
					//Log.debug("Shield Bash has missed!");
				}
				
				player.swing(shieldHand);
				//Log.debug("Shield Hand: " + shieldHand.toString());
				NetworkHandler.sendPacketToServer(new ShieldBashPacket(shieldHand, entId, attackEntity));
			}
		}
	}
	
	private static RayTraceResult getEntityMouseOverExtended(float reach)
	{
		RayTraceResult result = null;
		Minecraft mc = Minecraft.getInstance();
		Entity viewEntity = mc.getCameraEntity();
		
		if(viewEntity != null && mc.level != null)
		{
			double d0 = (double)reach;
			RayTraceResult rayTrace = viewEntity.pick(d0, 0.0f, false);
			Vector3d eyePos = viewEntity.getEyePosition(0);
			boolean flag = false;
			double d1 = d0;
			
			if(mc.gameMode.hasFarPickRange() && d1 < 6.0D)
			{
				d1 = 6.0D;
				d0 = d1;
			}
			else if(d0 > reach)
				flag = true;
			
			if(rayTrace != null)
				d1 = rayTrace.getLocation().distanceToSqr(eyePos);
			
			Vector3d lookVec = viewEntity.getViewVector(1.0f);
			Vector3d attackVec = eyePos.add(lookVec.x * d0, lookVec.y * d0, lookVec.z * d0);
			
			AxisAlignedBB expBounds = viewEntity.getBoundingBox().expandTowards(lookVec.scale(d0)).inflate(1.0D, 1.0D, 1.0D);
			EntityRayTraceResult entityRayTrace = ProjectileHelper.getEntityHitResult(viewEntity, eyePos, attackVec, expBounds, (entity) -> 
			{ 
				return !entity.isSpectator() && entity.canBeCollidedWith();
			}, d1);
			
			if(entityRayTrace != null)
			{
				Vector3d hitVec = entityRayTrace.getLocation();
				double d2 = eyePos.distanceToSqr(hitVec);
				if(flag && d2 > (reach * reach))
					result = BlockRayTraceResult.miss(hitVec, Direction.getNearest(lookVec.x, lookVec.y, lookVec.z), new BlockPos(hitVec));
				
				else if(d2 < d1 || result == null)
					result = entityRayTrace;
			}
			else
			{
				result = BlockRayTraceResult.miss(attackVec, Direction.getNearest(lookVec.x, lookVec.y, lookVec.z), new BlockPos(attackVec));
			}
		}
		
		return result;
	}
	
	@SubscribeEvent
	public static void onTooltipEvent(ItemTooltipEvent ev)
	{
		PlayerEntity player = ev.getPlayer();
		ItemStack stack = ev.getItemStack();
		if(!stack.isEmpty() && ModItemTags.shieldsWithBash.contains(stack.getItem()) && stack.getItem().isShield(stack, player))
		{
			KeyBinding boundKey = ModKeyBinds.KEY_ALT_SHIELD_BASH.isUnbound() ? Minecraft.getInstance().options.keyAttack : ModKeyBinds.KEY_ALT_SHIELD_BASH;
			ev.getToolTip().add(new TranslationTextComponent("tooltip." + ModSpartanShields.ID + ".shield_bash", 
					TextFormatting.GRAY + I18n.get("tooltip." + ModSpartanShields.ID + ".shield_bash.value", 
					TextFormatting.AQUA + I18n.get(boundKey.getTranslatedKeyMessage().getString().toUpperCase()) + TextFormatting.GRAY)).withStyle(TextFormatting.GOLD));
		}
	}
}
