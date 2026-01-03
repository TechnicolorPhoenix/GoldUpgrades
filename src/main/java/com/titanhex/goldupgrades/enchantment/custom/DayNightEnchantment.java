package com.titanhex.goldupgrades.enchantment.custom;

import com.titanhex.goldupgrades.GoldUpgradesConfig;
import com.titanhex.goldupgrades.helper.ItemHelper;
import com.titanhex.goldupgrades.item.interfaces.IDayInfluencedItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class DayNightEnchantment extends Enchantment {
    public DayNightEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentType.BREAKABLE, ItemHelper.ALL_SLOTS);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) &&
                stack.getItem() instanceof IDayInfluencedItem &&
                GoldUpgradesConfig.DAY_NIGHT_ENCHANT_ENABLED.get();
    }

    @NotNull
    @Override
    public ITextComponent getFullname(int p_200305_1_) {
        return super.getFullname(p_200305_1_);
    }
}
