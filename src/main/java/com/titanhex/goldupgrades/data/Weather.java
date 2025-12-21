package com.titanhex.goldupgrades.data;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public enum Weather {
    ANY,
    CLEAR,
    RAINING,
    THUNDERING;

    /**
     * Determines the current in-game weather state.
     */
    public static Weather getCurrentWeather(World world) {
        if (world.isThundering()) {
            return THUNDERING;
        } else if (world.isRaining()) {
            return RAINING;
        }
        return CLEAR;
    }

    /**
     * Parses a string (from JSON) into the Weather enum.
     */
    public static Weather getWeatherByString(String name) {
        try {
            return Weather.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ANY;
        }
    }
}
