package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.MoonPhase;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public interface IDimensionInfluencedItem {
    String NBT_DIMENSION = "ItemDimension";

    default DimensionType getDimension(ItemStack stack) {
        return DimensionType.getDimensionTypeByString(stack.getOrCreateTag().getString(NBT_DIMENSION));
    }
    default void setDimension(ItemStack stack, DimensionType value) {
        stack.getOrCreateTag().putString(NBT_DIMENSION, value.name());
    }

    static int getEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DAY_AND_NIGHT_ENCHANTMENT.get(), stack);
    }

    static boolean hasEnchantment(ItemStack stack) {
        return getEnchantmentLevel(stack) > 0;
    }
}
