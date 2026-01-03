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
    public static ForgeConfigSpec.ConfigValue<Double> FIRE_ARMOR_REGENERATION_AMOUNT;
    public static ForgeConfigSpec.ConfigValue<Integer> FIRE_ARMOR_RECOVERY_SPEED;
    public static ForgeConfigSpec.ConfigValue<Double> FIRE_ARMOR_PER_LEVEL_REGENERATION_AMOUNT;
    public static ForgeConfigSpec.ConfigValue<Integer> FIRE_ARMOR_PER_LEVEL_RECOVERY_SPEED;
    public static ForgeConfigSpec.ConfigValue<Integer> SEA_ARMOR_DURABILITY_DRAIN_FACTOR;
    public static ForgeConfigSpec.ConfigValue<Integer> SEA_ARMOR_OXYGEN_RECOVERY;
    public static ForgeConfigSpec.ConfigValue<Integer> SEA_ARMOR_RECOVER_COOLDOWN;
    public static ForgeConfigSpec.ConfigValue<Integer> SEA_ARMOR_RECOVER_KICKIN;
    public static ForgeConfigSpec.ConfigValue<Double> STORM_ARMOR_JUMP_BONUS;
    public static ForgeConfigSpec.ConfigValue<Integer> STORM_ARMOR_ELYTRA_TICK;
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
                .define("torch_durability", 8);

        STORM_ARMOR_JUMP_BONUS = COMMON_BUILDER
                .comment("Amount of Jump Bonus applied for each piece of Storm Armor.")
                .define("storm_armor_jump_bonus", 10.0D);
        STORM_ARMOR_ELYTRA_TICK = COMMON_BUILDER
                .comment("How many ticks before damage is done to armor while flying. (20ticks = 1second)")
                .define("storm_armor_elytra_tick", 20);
        FIRE_ARMOR_REGENERATION_AMOUNT = COMMON_BUILDER
                .comment("Amount of health regenerated for each piece of Fire Armor.")
                .define("fire_armor_regeneration", 1.0D); 
        FIRE_ARMOR_RECOVERY_SPEED = COMMON_BUILDER
                .comment("Speed of regeneration in ticks for Fire Armor pieces. (20ticks = 1second)")
                .define("fire_armor_speed", 500);
        FIRE_ARMOR_PER_LEVEL_REGENERATION_AMOUNT = COMMON_BUILDER
                .comment("Amount of extra health regenerated per tier.")
                .define("fire_armor_regeneration_per_level", 0.0D); 
        FIRE_ARMOR_PER_LEVEL_RECOVERY_SPEED = COMMON_BUILDER
                .comment("Amount of tick reduction per level for recovery speed.")
                .define("fire_armor_speed_per_level", 0); 
        SEA_ARMOR_DURABILITY_DRAIN_FACTOR = COMMON_BUILDER
                .comment("Amount of durability to drain proportional to the oxygen recovered")
                .define("sea_armor_drain_ratio", 15); 
        SEA_ARMOR_OXYGEN_RECOVERY = COMMON_BUILDER
                .comment("Amount of oxygen each piece of armor restores")
                .define("sea_armor_oxygen_recover", 30); 
        SEA_ARMOR_RECOVER_COOLDOWN = COMMON_BUILDER
                .comment("How many ticks before the armor can restore oxygen again. (20ticks = 1second)")
                .define("sea_armor_cooldown", 120); 
        SEA_ARMOR_RECOVER_KICKIN = COMMON_BUILDER
                .comment("Amount of oxygen remaining when Oxygen Recovery should start kicking in. (max: 300)")
                .define("sea_armor_recover_threshold", 60); 

        COMMON_BUILDER.pop(); // End section

        COMMON_BUILDER.comment("Gold Upgrades Mod Settings").push("enchants"); // Section

        DAY_NIGHT_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Day and Night Enchant enabled?")
                .define("day_night_enchant_enabled", true); 

        DIMENSION_MARAUDER_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Dimension Marauder Enchant enabled?")
                .define("dimension_marauder_enchant_enabled", true); 

        ELEMENTAL_HOE_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Elemental Hoe Enchant enabled?")
                .define("elemental_hoe_enchant_enabled", true); 

        MOONLIGHT_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Moonlight Enchant enabled?")
                .define("moonlight_enchant_enabled", true); 

        SUN_SHIFTER_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Sun Shifter Enchant enabled?")
                .define("sun_shifter_enchant_enabled", true); 

        MOON_BOOST_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Moon Boost Enchant enabled?")
                .define("moon_boost_enchant_enabled", true); 

        MOON_MAXING_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Moon Maxing Enchant enabled?")
                .define("moon_maxing_enchant_enabled", true); 

        DIVER_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Diver Enchant enabled?")
                .define("diver_enchant_enabled", true); 

        RAINLESS_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Rainless Enchant enabled?")
                .define("rainless_enchant_enabled", true); 

        WEATHER_BOOSTER_ENCHANT_ENABLED = COMMON_BUILDER
                .comment("Weather Booster Enchant enabled?")
                .define("weather_booster_enchant_enabled", true); 

        COMMON_SPEC = COMMON_BUILDER.build();
    }
}
