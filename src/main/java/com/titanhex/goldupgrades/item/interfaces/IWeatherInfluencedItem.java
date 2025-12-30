package com.titanhex.goldupgrades.item.interfaces;

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

    default boolean isRaining(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WATER_RAINLESS_ENCHANTMENT.get(), stack) > 0 || getWeather(stack) == Weather.RAINING;
    }
    default boolean isRaining(ItemStack stack, World world) {
        if (world == null) {
            return isRaining(stack);
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

    default boolean changeWeather(ItemStack stack, World world){
        Weather currentWeather = Weather.getCurrentWeather(world);
        Weather oldWeather = getWeather(stack);

        if (currentWeather != oldWeather) {
            setWeather(stack, currentWeather);
            return true;
        }

        return false;
    }

    static int getWeatherBoosterEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WEATHER_BOOSTER_ENCHANTMENT.get(), stack);
    }
    static boolean hasWeatherBoosterEnchantment(ItemStack stack) {
        return getWeatherBoosterEnchantmentLevel(stack) > 0;
    }
}
