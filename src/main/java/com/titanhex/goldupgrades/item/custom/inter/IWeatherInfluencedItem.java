package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IWeatherInfluencedItem {
    String NBT_WEATHER = "ItemWeather";

    default Weather getWeather(ItemStack stack) {
        return Weather.getWeatherByString(stack.getOrCreateTag().getString(NBT_WEATHER));
    }
    default void setWeather(ItemStack stack, Weather value) {
        stack.getOrCreateTag().putString(NBT_WEATHER, value.name());
    }

    static int getEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WEATHER_BOOSTER_ENCHANTMENT.get(), stack);
    }

    static boolean hasEnchantment(ItemStack stack) {
        return getEnchantmentLevel(stack) > 0;
    }

    default boolean isClear(ItemStack stack) {
        return getWeather(stack) == Weather.CLEAR;
    }
    default boolean isClear(ItemStack stack, World world) {
        if (world == null) {
            return isClear(stack);
        } else {
            return Weather.getCurrentWeather(world) == Weather.CLEAR;
        }
    }

    default boolean isRain(ItemStack stack) {
        return getWeather(stack) == Weather.RAINING;
    }
    default boolean isRain(ItemStack stack, World world) {
        if (world == null) {
            return isRain(stack);
        } else {
            return Weather.getCurrentWeather(world) == Weather.RAINING;
        }
    }

    default boolean isThundering(ItemStack stack) {
        return getWeather(stack) == Weather.THUNDERING;
    }
    default boolean isThundering(ItemStack stack, World world) {
        if (world == null) {
            return isThundering(stack);
        } else {
            return Weather.getCurrentWeather(world) == Weather.THUNDERING;
        }
    }

    default int getWeatherBoosterEnchantment(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WEATHER_BOOSTER_ENCHANTMENT.get(), stack);
    }
    default boolean hasWeatherBoosterEnchantment(ItemStack stack) {
        return getWeatherBoosterEnchantment(stack) > 0;
    }
}
