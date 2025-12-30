package com.titanhex.goldupgrades.item.interfaces;

import net.minecraft.item.ItemStack;

public interface IArmorCooldown {
    String NBT_ARMOR_TIMER_KEY = "ArmorTimer";

    default int getArmorCooldown(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_ARMOR_TIMER_KEY);
    }
    default void setArmorCooldown(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(NBT_ARMOR_TIMER_KEY, value);
    }
    default void reduceArmorCooldown(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(NBT_ARMOR_TIMER_KEY, getArmorCooldown(stack) - value);
    }
    default void reduceArmorCooldown(ItemStack stack) {
        reduceArmorCooldown(stack, 1);
    }
}
