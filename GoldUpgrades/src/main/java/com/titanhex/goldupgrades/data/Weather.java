package com.titanhex.goldupgrades.data;

import net.minecraft.world.World;

public enum Weather {
    ANY, // Added ANY state to signify the condition is not required
    CLEAR,
    RAIN,
    THUNDERING;

    /**
     * Determines the current in-game weather state.
     */
    public static Weather getCurrentWeather(World world) {
        if (world.isThundering()) {
            return THUNDERING;
        } else if (world.isRaining()) {
            return RAIN;
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
            // Default to ANY if the string is invalid or missing
            return ANY;
        }
    }
}
