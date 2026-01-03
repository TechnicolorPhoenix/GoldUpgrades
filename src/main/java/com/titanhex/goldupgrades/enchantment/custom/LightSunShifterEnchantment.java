package com.titanhex.goldupgrades.enchantment.custom;

import com.titanhex.goldupgrades.GoldUpgradesConfig;
import com.titanhex.goldupgrades.helper.ItemHelper;
import com.titanhex.goldupgrades.item.interfaces.ILightInfluencedItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LightSunShifterEnchantment extends Enchantment {
    public LightSunShifterEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentType.BREAKABLE, ItemHelper.ALL_SLOTS);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) &&
                stack.getItem() instanceof ILightInfluencedItem &&
                GoldUpgradesConfig.SUN_SHIFTER_ENCHANT_ENABLED.get();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
