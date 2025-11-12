package com.titanhex.goldupgrades.data;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;

/**
 * Represents the required dimension for a recipe to be craftable.
 * Includes ANY for optional checking.
 */
public enum DimensionType {
    ANY,
    OVERWORLD,
    NETHER,
    END;

    /**
     * Gets the DimensionType enum value corresponding to the current world.
     */
    public static DimensionType getCurrentDimension(World world) {
        // Use the ResourceLocation of the World's RegistryKey
        ResourceLocation dimId = world.dimension().location();

        if (dimId.equals(World.OVERWORLD.location())) {
            return OVERWORLD;
        } else if (dimId.equals(World.NETHER.location())) {
            return NETHER;
        } else if (dimId.equals(World.END.location())) {
            return END;
        }

        // Default to OVERWORLD for other dimensions or if key is unexpected
        return OVERWORLD;
    }

    /**
     * Parses a string (from JSON) into the DimensionType enum.
     */
    public static DimensionType getDimensionTypeByString(String name) {
        try {
            return DimensionType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Default to ANY if the string is invalid or missing
            return ANY;
        }
    }
}
