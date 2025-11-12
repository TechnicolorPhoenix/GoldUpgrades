package com.titanhex.goldupgrades.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

public enum ModItemTier implements IItemTier {
    OBSIDIAN_BASE_GOLD(0, 150, 12.0F, 0.0F, 22, () -> {
        return Ingredient.of(Items.OBSIDIAN);
    }),
    OBSIDIAN_UPGRADED_GOLD(2, 350, 11.0F, 1.0F, 25, () -> {
        return Ingredient.of(Items.CRYING_OBSIDIAN);
    }),
    OBSIDIAN_POWER_GOLD(3, 1125, 13.0F, 2.0F, 28, () -> {
        return Ingredient.of(Items.GILDED_BLACKSTONE);
    }),
    FIRE_BASE_GOLD(1, 100, 9.0F, 0.5F, 22, () -> {
        return Ingredient.of(Items.MAGMA_BLOCK);
    }),
    FIRE_UPGRADED_GOLD(2, 200, 11.0F, 1.5F, 25, () -> {
        return Ingredient.of(Items.BLAZE_ROD);
    }),
    FIRE_POWER_GOLD(3, 900, 13.0F, 2.5F, 28, () -> {
        return Ingredient.of(Items.MAGMA_CREAM);
    }),
    SEA_BASE_GOLD(1, 100, 9.0F, 0.0F, 25, () -> {
        return Ingredient.of(Items.WATER_BUCKET);
    }),
    SEA_UPGRADED_GOLD(2, 300, 11.0F, 1.0F, 29, () -> {
        return Ingredient.of(Items.NAUTILUS_SHELL);
    }),
    SEA_POWER_GOLD(3, 900, 13.0F, 2.0F, 33, () -> {
        return Ingredient.of(Items.CONDUIT);
    }),
    STORM_BASE_GOLD(1, 100, 11.0F, 0.0F, 22, () -> {
        return Ingredient.of(Items.HONEYCOMB);
    }),
    STORM_UPGRADED_GOLD(2, 300, 13.0F, 1.0F, 25, () -> {
        return Ingredient.of(Items.SLIME_BLOCK);
    }),
    STORM_POWER_GOLD(3, 900, 15.0F, 2.0F, 28, () -> {
        return Ingredient.of(Items.SHULKER_SHELL);
    }),
    ;

    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final LazyValue<Ingredient> repairMaterial;

    private ModItemTier(int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
        this.harvestLevel = harvestLevelIn;
        this.maxUses = maxUsesIn;
        this.efficiency = efficiencyIn;
        this.attackDamage = attackDamageIn;
        this.enchantability = enchantabilityIn;
        this.repairMaterial = new LazyValue<>(repairMaterialIn);
    }

    @Override
    public int getUses() {
        return this.maxUses;
    }

    @Override
    public float getSpeed() {
        return this.efficiency;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.attackDamage;
    }

    @Override
    public int getLevel() {
        return harvestLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }
}
