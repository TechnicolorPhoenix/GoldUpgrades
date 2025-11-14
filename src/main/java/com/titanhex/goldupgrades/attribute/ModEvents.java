package com.titanhex.goldupgrades.attribute;

import net.minecraft.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// This ensures your class listens to events fired on the MOD event bus
@Mod.EventBusSubscriber(modid = "goldupgrades", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {

        // Check if the event contains the Player Entity Type
        if (event.getTypes().contains(EntityType.PLAYER)) {

            // Use the event's 'add' method to properly apply the attribute
            // It adds the attribute using its default value (300.0D),
            // which you defined in AttributeRegistry.
            event.add(EntityType.PLAYER, ModAttributes.MAX_OXYGEN);

            System.out.println("Successfully added MAX_OXYGEN attribute to PlayerEntity.");
        }
    }
}