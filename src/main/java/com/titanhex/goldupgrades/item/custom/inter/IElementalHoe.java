package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public interface IElementalHoe {
    default int getElementalHoeEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ELEMENTAL_HOE_ENCHANTMENT.get(), stack);
    }

    default boolean hasElementalHoeEnchantment(ItemStack stack) {
        return getElementalHoeEnchantmentLevel(stack) > 0;
    }
}
