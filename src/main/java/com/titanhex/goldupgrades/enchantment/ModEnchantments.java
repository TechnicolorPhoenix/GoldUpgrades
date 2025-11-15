package com.titanhex.goldupgrades.enchantment;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.effect.CurseOfRustEffect;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.Effect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEnchantments {
    // Define all equipment slots where an item can be damaged (Tools/Weapons in Hand, Armor)
    public static final EquipmentSlotType[] ALL_SLOTS = new EquipmentSlotType[]{
            EquipmentSlotType.MAINHAND,
            EquipmentSlotType.OFFHAND,
            EquipmentSlotType.FEET,
            EquipmentSlotType.LEGS,
            EquipmentSlotType.CHEST,
            EquipmentSlotType.HEAD
    };

    // 1. Create the DeferredRegister for Effects (Potions)
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, GoldUpgrades.MOD_ID);

    public static final RegistryObject<Enchantment> CURSE_OF_RUST =
            ENCHANTMENTS.register("curse_of_rust", () -> new CurseOfRustEnchantment(
                    Enchantment.Rarity.RARE, // Choose Rarity
                    ALL_SLOTS          // Pass the array of all possible slots
            ));
    /**
     * Call this method from your main mod constructor to register the effects.
     */
    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
