package com.oblivioussp.spartanshields.init;

import com.oblivioussp.spartanshields.ModSpartanShields;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSounds 
{
	public static final ResourceLocation LOC_SHIELD_BASH_HIT = new ResourceLocation(ModSpartanShields.ID, "shield.bash.hit");
	public static final ResourceLocation LOC_SHIELD_BASH_MISS = new ResourceLocation(ModSpartanShields.ID, "shield.bash.miss");
	public static final ResourceLocation LOC_SHIELD_PAYBACK_CHARGE = new ResourceLocation(ModSpartanShields.ID, "shield.payback.charge");
	public static final ResourceLocation LOC_SHIELD_PAYBACK_FULL_CHARGE = new ResourceLocation(ModSpartanShields.ID, "shield.payback.full_charge");
	
	public static SoundEvent SHIELD_BASH_HIT;
	public static SoundEvent SHIELD_BASH_MISS;
	public static SoundEvent SHIELD_PAYBACK_CHARGE;
	public static SoundEvent SHIELD_PAYBACK_FULL_CHARGE;
	
	@SubscribeEvent()
	public static void initSoundEvents(RegistryEvent.Register<SoundEvent> ev)
	{
		IForgeRegistry<SoundEvent> reg = ev.getRegistry();
		
		SHIELD_BASH_HIT = new SoundEvent(LOC_SHIELD_BASH_HIT).setRegistryName(LOC_SHIELD_BASH_HIT);
		SHIELD_BASH_MISS = new SoundEvent(LOC_SHIELD_BASH_MISS).setRegistryName(LOC_SHIELD_BASH_MISS);
		SHIELD_PAYBACK_CHARGE = new SoundEvent(LOC_SHIELD_PAYBACK_CHARGE).setRegistryName(LOC_SHIELD_PAYBACK_CHARGE);
		SHIELD_PAYBACK_FULL_CHARGE = new SoundEvent(LOC_SHIELD_PAYBACK_FULL_CHARGE).setRegistryName(LOC_SHIELD_PAYBACK_FULL_CHARGE);
		
		reg.registerAll(SHIELD_BASH_HIT, SHIELD_BASH_MISS, SHIELD_PAYBACK_CHARGE, SHIELD_PAYBACK_FULL_CHARGE);
	}
}
