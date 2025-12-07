package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.MoonPhase;
import net.minecraft.item.ItemStack;

public interface IDimensionInfluencedItem {
    String NBT_DIMENSION = "ItemDimension";

    default DimensionType getDimension(ItemStack stack) {
        return DimensionType.getDimensionTypeByString(stack.getOrCreateTag().getString(NBT_DIMENSION));
    }
    default void setDimension(ItemStack stack, DimensionType value) {
        stack.getOrCreateTag().putString(NBT_DIMENSION, value.name());
    }

}
