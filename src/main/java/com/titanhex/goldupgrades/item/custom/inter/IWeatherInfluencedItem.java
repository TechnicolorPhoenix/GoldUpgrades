package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public interface IWeatherInfluencedItem {
    String NBT_WEATHER = "ItemWeather";

    default Weather getWeather(ItemStack stack) {
        return Weather.getWeatherByString(stack.getOrCreateTag().getString(NBT_WEATHER));
    }
    default void setWeather(ItemStack stack, Weather value) {
        stack.getOrCreateTag().putString(NBT_WEATHER, value.name());
    }

    static int getEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WEATHER_ENCHANTMENT.get(), stack);
    }

    static boolean hasEnchantment(ItemStack stack) {
        return getEnchantmentLevel(stack) > 0;
    }
}
