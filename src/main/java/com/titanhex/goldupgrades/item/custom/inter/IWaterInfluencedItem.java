package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IWaterInfluencedItem {
    String NBT_IS_SUBMERGED = "ItemSubmerged";
    String NBT_IS_IN_RAIN = "ItemInRain";

    default boolean getIsInRain(ItemStack stack) {
        return hasWaterRainlessEnchantment(stack) || stack.getOrCreateTag().getBoolean(NBT_IS_IN_RAIN);
    }
    default boolean getIsInRain(ItemStack stack, Entity holdingEntity){
        if (hasWaterRainlessEnchantment(stack)) {
            return true;
        } else if (holdingEntity == null) {
            return getIsInRain(stack);
        }
        return holdingEntity.isInWaterOrRain();
    }
    default void setIsInRain(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IS_IN_RAIN, value);
    }
    default boolean getIsSubmerged(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IS_SUBMERGED);
    }
    default boolean getIsSubmerged(Entity holdingEntity) {
        return holdingEntity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
    }
    default void setIsSubmerged(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IS_SUBMERGED, value);
    }

    default boolean changeSubmerged(ItemStack stack, Entity holdingEntity){
        boolean currentSubmerged = holdingEntity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);

        if (currentSubmerged != getIsSubmerged(stack)) {
            setIsSubmerged(stack, currentSubmerged);
            return true;
        }

        return false;
    }

    default boolean changeIsInRain(ItemStack stack, Entity holdingEntity){
        boolean isInRainOrWaterNow = ;
        boolean currentInRain = isInRainOrWaterNow && !getIsSubmerged(holdingEntity);

        if (currentInRain != getIsInRain(stack)) {
            setIsInRain(stack, currentInRain);
            return true;
        }

        return false;
    }

    default int getWaterDiverEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WATER_DIVER_ENCHANTMENT.get(), stack);
    }
    default boolean hasWaterDiverEnchantment(ItemStack stack) {
        return getWaterDiverEnchantmentLevel(stack) > 0;
    }

    default int getWaterRainlessEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WATER_RAINLESS_ENCHANTMENT.get(), stack);
    }
    default boolean hasWaterRainlessEnchantment(ItemStack stack) {
        return getWaterRainlessEnchantmentLevel(stack) > 0;
    }
}
