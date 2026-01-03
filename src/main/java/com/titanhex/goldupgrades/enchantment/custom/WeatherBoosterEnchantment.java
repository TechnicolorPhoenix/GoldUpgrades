package com.titanhex.goldupgrades.enchantment.custom;

import com.titanhex.goldupgrades.GoldUpgradesConfig;
import com.titanhex.goldupgrades.helper.ItemHelper;
import com.titanhex.goldupgrades.item.interfaces.IWeatherInfluencedItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WeatherBoosterEnchantment extends Enchantment {

    public WeatherBoosterEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentType.BREAKABLE, ItemHelper.ALL_SLOTS);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) &&
                stack.getItem() instanceof IWeatherInfluencedItem &&
                GoldUpgradesConfig.WEATHER_BOOSTER_ENCHANT_ENABLED.get();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
