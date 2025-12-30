package com.titanhex.goldupgrades.enchantment;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.enchantment.custom.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, GoldUpgrades.MOD_ID);

    public static final RegistryObject<Enchantment> DAY_AND_NIGHT_ENCHANTMENT = ENCHANTMENTS.register(
            "day_and_night_enchantment", DayNightEnchantment::new
    );

    public static final RegistryObject<Enchantment> WEATHER_BOOSTER_ENCHANTMENT = ENCHANTMENTS.register(
            "weather_booster_enchantment", WeatherBoosterEnchantment::new
    );

    public static final RegistryObject<Enchantment> MOONLIGHT_ENCHANTMENT = ENCHANTMENTS.register(
            "moonlight_enchantment", LightLevelMoonlightEnchantment::new
    );
    public static final RegistryObject<Enchantment> SUN_SHIFTER_ENCHANTMENT = ENCHANTMENTS.register(
            "sun_shifter_enchantment", LightSunShifterEnchantment::new
    );

    public static final RegistryObject<Enchantment> MOON_MAXING_ENCHANTMENT = ENCHANTMENTS.register(
            "moon_maxing_enchantment", MoonMaxingEnchantment::new
    );
    public static final RegistryObject<Enchantment> MOON_BOOST_ENCHANTMENT = ENCHANTMENTS.register(
            "moon_boost_enchantment", MoonBoostEnchantment::new
    );

    public static final RegistryObject<Enchantment> DIMENSION_MARAUDER_ENCHANTMENT = ENCHANTMENTS.register(
            "dimension_marauder_enchantment", DimensionEnchantment::new
    );

    public static final RegistryObject<Enchantment> WATER_DIVER_ENCHANTMENT = ENCHANTMENTS.register(
            "diver_enchantment", WaterDiverEnchantment::new
    );
    public static final RegistryObject<Enchantment> WATER_RAINLESS_ENCHANTMENT = ENCHANTMENTS.register(
            "rainless_enchantment", WaterRainlessEnchantment::new
    );

    public static final RegistryObject<Enchantment> ELEMENTAL_HOE_ENCHANTMENT = ENCHANTMENTS.register(
            "elemental_hoe_enchantment", ElementalHoeEnchantment::new
    );

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
