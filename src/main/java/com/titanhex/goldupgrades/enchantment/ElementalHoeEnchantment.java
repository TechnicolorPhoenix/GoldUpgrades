package com.titanhex.goldupgrades.enchantment;

import com.titanhex.goldupgrades.helper.ItemHelper;
import com.titanhex.goldupgrades.item.custom.tools.fire.FireGoldHoe;
import com.titanhex.goldupgrades.item.custom.tools.obsidian.ObsidianGoldHoe;
import com.titanhex.goldupgrades.item.custom.tools.sea.SeaGoldHoe;
import com.titanhex.goldupgrades.item.custom.tools.storm.StormGoldHoe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ElementalHoeEnchantment extends Enchantment {

    protected ElementalHoeEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentType.BREAKABLE, ItemHelper.ALL_SLOTS);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && (
                stack.getItem() instanceof FireGoldHoe ||
                    stack.getItem() instanceof SeaGoldHoe ||
                        stack.getItem() instanceof ObsidianGoldHoe ||
                        stack.getItem() instanceof StormGoldHoe
        );
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
