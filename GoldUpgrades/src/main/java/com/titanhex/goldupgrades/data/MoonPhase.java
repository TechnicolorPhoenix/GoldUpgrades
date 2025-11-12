package com.titanhex.goldupgrades.data;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public enum MoonPhase {
    ANY(-1), // Added ANY to signify the condition is not required
    FULL_MOON(0),
    WANING_GIBBOUS(1),
    LAST_QUARTER(2),
    WANING_CRESCENT(3),
    NEW_MOON(4),
    WAXING_CRESCENT(5),
    FIRST_QUARTER(6),
    WAXING_GIBBOUS(7);

    private final int phaseIndex;

    MoonPhase(int phaseIndex) {
        this.phaseIndex = phaseIndex;
    }

    public static MoonPhase getCurrentMoonPhase(World world) {

        long worldTime = world.getGameTime();

        long dayNumber = worldTime / 24000L;

        int index = (int)(dayNumber % 8L);

        for (MoonPhase phase : values()) {
            if (phase.phaseIndex >= 0 && phase.phaseIndex == index) {
                return phase;
            }
        }

        return FULL_MOON;
    }

    public int getPhaseIndex() {
        return this.phaseIndex;
    }

    public static MoonPhase getMoonPhaseByString(String name) {
        try {
            // Attempt to find the enum by name (case-insensitive)
            return MoonPhase.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Default to ANY if the string is invalid (or if "any" was passed from the JSON default)
            return ANY;
        }
    }
}
