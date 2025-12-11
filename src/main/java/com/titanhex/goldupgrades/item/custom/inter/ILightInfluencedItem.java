package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface ILightInfluencedItem {
    String NBT_LIGHT_LEVEL = "ItemLightLevel";

    default int getLightLevel(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_LIGHT_LEVEL);
    }
    default void setLightLevel(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(NBT_LIGHT_LEVEL, value);
    }
    default int getLightLevel(ItemStack stack, World world, BlockPos pos) {
        if (world != null) {
            long time = world.getDayTime() % 24000L;
            boolean isNight = time > 13000L && time < 23000L;
            return world.getRawBrightness(pos, isNight ? hasEnchantment(stack) ? 0 : 15 : 0);
        } else
            return getLightLevel(stack);
    }

    static int getEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LIGHT_LEVEL_ENCHANTMENT.get(), stack);
    }

    static boolean hasEnchantment(ItemStack stack) {
        return getEnchantmentLevel(stack) > 0;
    }
}
