package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public interface IElementalHoe {

    static int getElementalHoeEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ELEMENTAL_HOE_ENCHANTMENT.get(), stack);
    }

    static boolean hasElementalHoeEnchantment(ItemStack stack) {
        return getElementalHoeEnchantmentLevel(stack) > 0;
    }

    @OnlyIn(Dist.CLIENT)
    static void appendHoverText(ItemStack stack, List<ITextComponent> tooltip, String holdingText) {
        if (holdingText != null && !holdingText.isEmpty()) {
            boolean hasElementalHoeEnchantment = hasElementalHoeEnchantment(stack);
            if (hasElementalHoeEnchantment)
                tooltip.add(new StringTextComponent(holdingText));
        }
    }
}
