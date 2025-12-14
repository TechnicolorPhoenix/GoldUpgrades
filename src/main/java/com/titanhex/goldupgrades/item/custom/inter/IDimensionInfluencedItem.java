package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IDimensionInfluencedItem {
    String NBT_DIMENSION = "ItemDimension";

    default DimensionType getDimension(ItemStack stack) {
        return DimensionType.getDimensionTypeByString(stack.getOrCreateTag().getString(NBT_DIMENSION));
    }
    default void setDimension(ItemStack stack, DimensionType value) {
        stack.getOrCreateTag().putString(NBT_DIMENSION, value.name());
    }

    DimensionType primaryDimension();

    default boolean inValidDimension(ItemStack stack, @Nullable World world) {
        if (hasEnchantment(stack)) return true;
        if (world == null) return false;
        return DimensionType.getCurrentDimension(world) == primaryDimension();
    }
    default boolean inValidDimension(ItemStack stack) {
        return hasEnchantment(stack) || getDimension(stack) == primaryDimension();
    }

    static int getEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DIMENSION_MARAUDER_ENCHANTMENT.get(), stack);
    }

    static boolean hasEnchantment(ItemStack stack) {
        return getEnchantmentLevel(stack) > 0;
    }
}
