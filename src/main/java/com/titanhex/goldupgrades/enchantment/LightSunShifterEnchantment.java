package com.titanhex.goldupgrades.enchantment;

import com.titanhex.goldupgrades.helper.ItemHelper;
import com.titanhex.goldupgrades.item.custom.inter.ILightInfluencedItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LightSunShifterEnchantment extends Enchantment {
    protected LightSunShifterEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentType.BREAKABLE, ItemHelper.ALL_SLOTS);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && stack.getItem() instanceof ILightInfluencedItem;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
