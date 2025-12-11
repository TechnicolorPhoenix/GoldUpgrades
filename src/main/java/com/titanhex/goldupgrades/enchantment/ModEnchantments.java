package com.titanhex.goldupgrades.enchantment;

import com.titanhex.goldupgrades.GoldUpgrades;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, GoldUpgrades.MOD_ID);

    public static final RegistryObject<Enchantment> DAY_AND_NIGHT_ENCHANTMENT = ENCHANTMENTS.register(
            "day_and_night_enchantment", com.titanhex.goldupgrades.enchantment.DayNightEnchantment::new
    );

    public static final RegistryObject<Enchantment> WEATHER_ENCHANTMENT = ENCHANTMENTS.register(
            "weather_enchantment", WeatherableEnchantment::new
    );

    public static final RegistryObject<Enchantment> LIGHT_LEVEL_ENCHANTMENT = ENCHANTMENTS.register(
            "light_enchantment", LightLevelMoonlightEnchantment::new
    );

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
