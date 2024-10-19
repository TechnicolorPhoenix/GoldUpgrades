package com.titanhex.thex.event;

import com.titanhex.thex.data.ModItemTagsProvider;
import com.titanhex.thex.data.ModRecipeProvider;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DataGenEventHandler 
{
	@SubscribeEvent
	public static void onGenerateData(GatherDataEvent ev)
	{
		DataGenerator gen = ev.getGenerator();
		ExistingFileHelper existingFileHelper = ev.getExistingFileHelper();
		gen.addProvider(new ModItemTagsProvider(gen, existingFileHelper));
		gen.addProvider(new ModRecipeProvider(gen));
	}
}
