package com.titanhex.goldupgrades.item.custom.inter;

import net.minecraft.item.ItemStack;

import java.util.Random;

public interface ILightInfluencedItem {
    String NBT_LIGHT_LEVEL = "ItemLightLevel";
    String NBT_LIGHT_LEVEL_BONUS = "ItemLightLevelBonus";

    default int getLightLevel(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_LIGHT_LEVEL);
    }
    default void setLightLevel(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(NBT_LIGHT_LEVEL, value);
    }
    default int getLightLevelBonus(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_LIGHT_LEVEL_BONUS);
    }
    default void setLightLevelBonus(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(NBT_LIGHT_LEVEL_BONUS, value);
    }
}
