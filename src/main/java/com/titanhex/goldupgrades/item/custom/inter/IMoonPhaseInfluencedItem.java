package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.data.MoonPhase;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public interface IMoonPhaseInfluencedItem {
    String NBT_MOON_PHASE = "ItemMoonPhase";
    default MoonPhase getMoonPhase(ItemStack stack) {
        return MoonPhase.getMoonPhaseByString(stack.getOrCreateTag().getString(NBT_MOON_PHASE));
    }
    default void setMoonPhase(ItemStack stack, MoonPhase value) {
        stack.getOrCreateTag().putString(NBT_MOON_PHASE, value.name());
    }
    default int getMoonPhaseValue(MoonPhase moonPhase) {
        if (moonPhase == MoonPhase.FULL_MOON) {
            return 4;
        }
        else if (moonPhase == MoonPhase.WANING_GIBBOUS || moonPhase == MoonPhase.WAXING_GIBBOUS) {
            return 3;
        }
        else if (moonPhase == MoonPhase.LAST_QUARTER || moonPhase == MoonPhase.FIRST_QUARTER) {
            return 2;
        }
        else if (moonPhase == MoonPhase.WANING_CRESCENT || moonPhase == MoonPhase.WAXING_CRESCENT) {
            return 1;
        }
        else if (moonPhase == MoonPhase.NEW_MOON) {
            return 0;
        } else {
            return -1;
        }
    }

    static int getEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MOON_PHASE_ENCHANTMENT.get(), stack);
    }

    static boolean hasEnchantment(ItemStack stack) {
        return getEnchantmentLevel(stack) > 0;
    }
}
