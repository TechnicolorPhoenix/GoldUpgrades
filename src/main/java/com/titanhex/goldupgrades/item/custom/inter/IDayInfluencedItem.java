package com.titanhex.goldupgrades.item.custom.inter;

import net.minecraft.item.ItemStack;

public interface IDayInfluencedItem {
    String NBT_IS_DAY = "ItemDay";
    default boolean getIsDay(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IS_DAY);
    }
    default void setIsDay(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IS_DAY, value);
    }
}
