package com.titanhex.goldupgrades.item.interfaces;

import com.titanhex.goldupgrades.data.MoonPhase;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IMoonPhaseInfluencedItem {
    String NBT_MOON_PHASE = "ItemMoonPhase";
    default MoonPhase getMoonPhase(ItemStack stack) {
        return MoonPhase.getMoonPhaseByString(stack.getOrCreateTag().getString(NBT_MOON_PHASE));
    }
    default void setMoonPhase(ItemStack stack, MoonPhase value) {
        stack.getOrCreateTag().putString(NBT_MOON_PHASE, value.name());
    }

    default int getMoonPhaseValue(ItemStack stack) {
        MoonPhase phase = getMoonPhase(stack);

        return getMoonPhaseValue(stack, phase);
    }
    default int getMoonPhaseValue(ItemStack stack, MoonPhase moonPhase) {
        int phaseValue;

        if (hasMoonMaxEnchantment(stack)) {
            phaseValue = 4;
        } else if (moonPhase == MoonPhase.FULL_MOON) {
            phaseValue = 4;
        }
        else if (moonPhase == MoonPhase.WANING_GIBBOUS || moonPhase == MoonPhase.WAXING_GIBBOUS) {
            phaseValue = 3;
        }
        else if (moonPhase == MoonPhase.LAST_QUARTER || moonPhase == MoonPhase.FIRST_QUARTER) {
            phaseValue = 2;
        }
        else if (moonPhase == MoonPhase.WANING_CRESCENT || moonPhase == MoonPhase.WAXING_CRESCENT) {
            phaseValue = 1;
        }
        else if (moonPhase == MoonPhase.NEW_MOON) {
            phaseValue = 0;
        } else {
            return -1;
        }

        return phaseValue + getMoonBoostEnchantmentLevel(stack);
    }
    default int getMoonPhaseValue(ItemStack stack, World world) {
        MoonPhase phase = MoonPhase.getCurrentMoonPhase(world);

        return getMoonPhaseValue(stack, phase);
    }
    default boolean changeMoonPhase(ItemStack stack, World world) {
        MoonPhase currentMoonPhase = MoonPhase.getCurrentMoonPhase(world);
        MoonPhase oldMoonPhase = getMoonPhase(stack);

        if ( currentMoonPhase != oldMoonPhase) {
            setMoonPhase(stack, currentMoonPhase);
            return true;
        }

        return false;
    }

    static int getMoonMaxEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MOON_MAXING_ENCHANTMENT.get(), stack);
    }
    static boolean hasMoonMaxEnchantment(ItemStack stack) {
        return getMoonMaxEnchantmentLevel(stack) > 0;
    }

    static int getMoonBoostEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MOON_BOOST_ENCHANTMENT.get(), stack);
    }
    static boolean hasMoonBoostEnchantment(ItemStack stack) {
        return getMoonBoostEnchantmentLevel(stack) > 0;
    }
}
