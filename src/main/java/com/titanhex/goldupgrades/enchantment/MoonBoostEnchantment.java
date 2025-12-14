package com.titanhex.goldupgrades.enchantment;

import com.titanhex.goldupgrades.helper.ItemHelper;
import com.titanhex.goldupgrades.item.custom.inter.IMoonPhaseInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MoonBoostEnchantment extends Enchantment {

    protected MoonBoostEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentType.BREAKABLE, ItemHelper.ALL_SLOTS);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && stack.getItem() instanceof IMoonPhaseInfluencedItem;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
