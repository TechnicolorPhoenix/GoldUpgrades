package com.titanhex.goldupgrades;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class GoldUpgradesConfig {
    // Builder for Common Config
    public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_SPEC;

    // Define your values
    public static ForgeConfigSpec.ConfigValue<Integer> EXAMPLE_INTEGER;

    static {
        COMMON_BUILDER.comment("Gold Upgrades Mod Settings").push("general"); // Section

        EXAMPLE_INTEGER = COMMON_BUILDER
                .comment("An example integer setting")
                .define("example_int", 100); // Name, Default Value

        COMMON_BUILDER.pop(); // End section

        COMMON_SPEC = COMMON_BUILDER.build();
    }

}
