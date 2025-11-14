package com.titanhex.goldupgrades.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class CurseOfRustEnchantment extends Enchantment {

    // You can apply this curse to any type of item/armor
    // For simplicity, I'm setting it to ALL, but you might want to create a specific type.
    public CurseOfRustEnchantment(Rarity rarity, EquipmentSlotType... slots) {
        super(rarity, EnchantmentType.BREAKABLE, slots);
    }

    // --- Enchantment Properties ---

    /**
     * Set the max level for the enchantment (up to 3 as requested).
     */
    @Override
    public int getMaxLevel() {
        return 3;
    }

    /**
     * Ensure this enchantment can be found in the enchantment table at level 1.
     */
    @Override
    public int getMinCost(int level) {
        return 1 + (level - 1) * 10;
    }

    /**
     * Make it a curse (like Curse of Vanishing or Binding).
     */
    @Override
    public boolean isCurse() {
        return true;
    }

    /**
     * Allows this enchantment to be applied to any item.
     * Note: You might want to restrict this later (e.g., only `isDamageable(stack)`).
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        // Only allow enchanting on items that actually have durability
        return stack.isDamageableItem();
    }

    /**
     * Curses are not allowed to be sold by Villagers by default.
     */
    @Override
    public boolean isTradeable() {
        return false;
    }

    /**
     * Curses are not allowed to be looted in fishing by default.
     */
    @Override
    public boolean isDiscoverable() {
        return false;
    }

    /**
     * Allow levels > 1 to be obtained via an anvil or commands.
     */
    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
