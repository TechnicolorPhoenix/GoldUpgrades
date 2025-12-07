package com.titanhex.goldupgrades.item.custom.inter;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface IWaterInfluencedItem {
    String NBT_IS_SUBMERGED = "ItemSubmerged";
    String NBT_IS_IN_RAIN = "ItemInRain";
    default boolean getIsInRain(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IS_IN_RAIN);
    }
    default void setIsInRain(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IS_SUBMERGED, value);
    }
    default boolean getIsSubmerged(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IS_SUBMERGED);
    }
    default void setIsSubmerged(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IS_SUBMERGED, value);
    }
}
