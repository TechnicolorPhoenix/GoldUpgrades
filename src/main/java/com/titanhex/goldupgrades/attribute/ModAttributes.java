package com.titanhex.goldupgrades.attribute; // Use your actual mod core package

import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goldupgrades", bus = Mod.EventBusSubscriber.Bus.MOD) // Replace 'goldupgrades' with your modid
public class ModAttributes {

    // Define the new Attribute
    public static final Attribute MAX_OXYGEN = new RangedAttribute(
            "attribute.name.max_oxygen", 300.0D, 1.0D, 1024.0D)
            .setRegistryName("goldupgrades", "max_oxygen");

    @SubscribeEvent
    public static void registerAttributes(final RegistryEvent.Register<Attribute> event) {
        // Register the custom attribute
        event.getRegistry().register(MAX_OXYGEN);
    }
}
