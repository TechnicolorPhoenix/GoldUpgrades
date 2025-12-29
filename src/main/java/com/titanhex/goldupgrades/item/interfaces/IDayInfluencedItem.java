package com.titanhex.goldupgrades.item.interfaces;

import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface IDayInfluencedItem {
    String NBT_IS_DAY = "ItemDay";
    default boolean getIsDay(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IS_DAY);
    }
    default void setIsDay(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IS_DAY, value);
    }

    default boolean isNight(ItemStack stack){
        return hasEnchantment(stack) || !getIsDay(stack);
    }
    default boolean isNight(ItemStack stack, @Nullable World world){
        if (world == null)
            return isNight(stack);
        else {
            long time = world.getDayTime() % 24000L;
            boolean isNight = time > 13000L && time < 23000L;
            return hasEnchantment(stack) || isNight;
        }
    }

    default boolean isDay(ItemStack stack){
        return hasEnchantment(stack) || getIsDay(stack);
    }
    default boolean isDay(ItemStack stack, @Nullable World world){
        if (world == null)
            return isDay(stack);
        else {
            long time = world.getDayTime() % 24000L;
            boolean isDay = time < 13000L;
            return hasEnchantment(stack) || isDay;
        }
    }

    default boolean changeDay(ItemStack stack, World world) {
        boolean currentIsDay = isDay(stack, world);
        boolean oldIsDay = isDay(stack);

        if (oldIsDay != currentIsDay) {
            setIsDay(stack, currentIsDay);
            return true;
        }

        return false;
    }

    static int getEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DAY_AND_NIGHT_ENCHANTMENT.get(), stack);
    }

    static boolean hasEnchantment(ItemStack stack) {
        return getEnchantmentLevel(stack) > 0;
    }
}
