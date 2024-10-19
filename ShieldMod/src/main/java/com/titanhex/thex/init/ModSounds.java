package com.titanhex.thex.init;

import com.titanhex.thex.ModThexShields;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSounds 
{
	public static final ResourceLocation LOC_SHIELD_PAYBACK_CHARGE = new ResourceLocation(ModThexShields.ID, "shield.payback.charge");
	public static final ResourceLocation LOC_SHIELD_PAYBACK_FULL_CHARGE = new ResourceLocation(ModThexShields.ID, "shield.payback.full_charge");

	public static SoundEvent SHIELD_PAYBACK_CHARGE;
	public static SoundEvent SHIELD_PAYBACK_FULL_CHARGE;
	
	@SubscribeEvent()
	public static void initSoundEvents(RegistryEvent.Register<SoundEvent> ev)
	{
		IForgeRegistry<SoundEvent> reg = ev.getRegistry();

		SHIELD_PAYBACK_CHARGE = new SoundEvent(LOC_SHIELD_PAYBACK_CHARGE).setRegistryName(LOC_SHIELD_PAYBACK_CHARGE);
		SHIELD_PAYBACK_FULL_CHARGE = new SoundEvent(LOC_SHIELD_PAYBACK_FULL_CHARGE).setRegistryName(LOC_SHIELD_PAYBACK_FULL_CHARGE);
	}
}
