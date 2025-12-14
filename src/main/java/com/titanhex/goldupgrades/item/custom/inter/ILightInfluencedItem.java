package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
            int lightLevel;
            int sunShift = getSunShifterEnchantmentLevel(stack);
            if (isDayInfluenced()) {
                lightLevel = world.getRawBrightness(pos, isNight ? hasMoonlightEnchantment(stack) ? 0 : 15 : 0);
            } else {
              lightLevel =  world.getRawBrightness(pos, 0);
              sunShift *= -1;
            }
            return Math.max(0, lightLevel + sunShift);
        } else
            return getLightLevel(stack);
    }

    static int getMoonlightEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MOONLIGHT_ENCHANTMENT.get(), stack);
    }
    static boolean hasMoonlightEnchantment(ItemStack stack) {
        return getMoonlightEnchantmentLevel(stack) > 0;
    }

    static int getSunShifterEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SUN_SHIFTER_ENCHANTMENT.get(), stack);
    }
    static boolean hasSunShifterEnchantment(ItemStack stack) {
        return getSunShifterEnchantmentLevel(stack) > 0;
    }

    default boolean isDayInfluenced() {
        return true;
    }
}
