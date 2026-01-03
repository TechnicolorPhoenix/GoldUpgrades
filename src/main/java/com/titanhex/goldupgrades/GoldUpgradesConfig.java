package com.titanhex.goldupgrades;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class GoldUpgradesConfig {
    // Builder for Common Config
    public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_SPEC;

    // Define your values
    public static ForgeConfigSpec.ConfigValue<Integer> TORCH_DURABILITY_COST;
    public static ForgeConfigSpec.ConfigValue<Integer> SEA_ARMOR_DURABILITY_DRAIN_FACTOR;
    public static ForgeConfigSpec.ConfigValue<Integer> SEA_ARMOR_OXYGEN_RECOVERY;
    public static ForgeConfigSpec.ConfigValue<Integer> SEA_ARMOR_RECOVER_COOLDOWN;
    public static ForgeConfigSpec.ConfigValue<Integer> SEA_ARMOR_RECOVER_KICKIN;
    public static ForgeConfigSpec.ConfigValue<Boolean> DAY_NIGHT_ENCHANT_ENABLED;
    public static ForgeConfigSpec.ConfigValue<Boolean> DIMENSION_MARAUDER_ENCHANT_ENABLED;
    public static ForgeConfigSpec.ConfigValue<Boolean> ELEMENTAL_HOE_ENCHANT_ENABLED;
    public static ForgeConfigSpec.ConfigValue<Boolean> MOONLIGHT_ENCHANT_ENABLED;
    public static ForgeConfigSpec.ConfigValue<Boolean> SUN_SHIFTER_ENCHANT_ENABLED;
    public static ForgeConfigSpec.ConfigValue<Boolean> MOON_BOOST_ENCHANT_ENABLED;
    public static ForgeConfigSpec.ConfigValue<Boolean> MOON_MAXING_ENCHANT_ENABLED;
    public static ForgeConfigSpec.ConfigValue<Boolean> DIVER_ENCHANT_ENABLED;
    public static ForgeConfigSpec.ConfigValue<Boolean> RAINLESS_ENCHANT_ENABLED;
    public static ForgeConfigSpec.ConfigValue<Boolean> WEATHER_BOOSTER_ENCHANT_ENABLED;

    static {
        COMMON_BUILDER.comment("Gold Upgrades Mod Settings").push("general"); // Section

        TORCH_DURABILITY_COST = COMMON_BUILDER
                .comment("Durability cost to place down a torch")
                .define("torch_durability", 8); // Name, Default Value

        SEA_ARMOR_DURABILITY_DRAIN_FACTOR = COMMON_BUILDER
                .comment("Amount of durability to drain proportional to the oxygen recovered")
                .define("sea_armor_drain_ratio", 15); // Name, Default Value
        SEA_ARMOR_OXYGEN_RECOVERY = COMMON_BUILDER
                .comment("Amount of oxygen each piece of armor restores")
                .define("sea_armor_oxygen_recover", 30); // Name, Default Value
        SEA_ARMOR_RECOVER_COOLDOWN = COMMON_BUILDER
                .comment("How many ticks before the armor can restore oxygen again. (20ticks = 1second)")
                .define("sea_armor_cooldown", 120); // Name, Default Value
        SEA_ARMOR_RECOVER_KICKIN = COMMON_BUILDER
                .comment("Amount of oxygen remaining when Oxygen Recovery should start kicking in. (max: 300)")
                .define("sea_armor_recover_threshold", 60); // Name, Default Value

        COMMON_BUILDER.pop(); // End section

        COMMON_BUILDER.comment("Gold Upgrades Mod Settings").push("enchants"); // Section

        DAY_NIGHT_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Day and Night Enchant enabled?")
                .define("day_night_enchant_enabled", true); // Name, Default Value

        DIMENSION_MARAUDER_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Dimension Marauder Enchant enabled?")
                .define("dimension_marauder_enchant_enabled", true); // Name, Default Value

        ELEMENTAL_HOE_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Elemental Hoe Enchant enabled?")
                .define("elemental_hoe_enchant_enabled", true); // Name, Default Value

        MOONLIGHT_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Moonlight Enchant enabled?")
                .define("moonlight_enchant_enabled", true); // Name, Default Value

        SUN_SHIFTER_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Sun Shifter Enchant enabled?")
                .define("sun_shifter_enchant_enabled", true); // Name, Default Value

        MOON_BOOST_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Moon Boost Enchant enabled?")
                .define("moon_boost_enchant_enabled", true); // Name, Default Value

        MOON_MAXING_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Moon Maxing Enchant enabled?")
                .define("moon_maxing_enchant_enabled", true); // Name, Default Value

        DIVER_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Diver Enchant enabled?")
                .define("diver_enchant_enabled", true); // Name, Default Value

        RAINLESS_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Rainless Enchant enabled?")
                .define("rainless_enchant_enabled", true); // Name, Default Value

        WEATHER_BOOSTER_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Weather Booster Enchant enabled?")
                .define("weather_booster_enchant_enabled", true); // Name, Default Value

        COMMON_SPEC = COMMON_BUILDER.build();
    }
}
