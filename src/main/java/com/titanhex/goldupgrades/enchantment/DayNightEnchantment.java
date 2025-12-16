package com.titanhex.goldupgrades.enchantment;

import com.titanhex.goldupgrades.helper.ItemHelper;
import com.titanhex.goldupgrades.item.custom.inter.IDayInfluencedItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DayNightEnchantment extends Enchantment {
    protected DayNightEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentType.BREAKABLE, ItemHelper.ALL_SLOTS);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && stack.getItem() instanceof IDayInfluencedItem;
    }

    @NotNull
    @Override
    public ITextComponent getFullname(int p_200305_1_) {
        return super.getFullname(p_200305_1_);
    }
}
