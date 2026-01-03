package com.titanhex.goldupgrades.item;

import com.titanhex.goldupgrades.GoldUpgrades;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum ModArmorMaterial implements IArmorMaterial {
    OBSIDIAN_BASE_GOLD("obsidian_base_gold", 15, new int[]{2, 4, 5, 1}, 20, 1,
            SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.OBSIDIAN);
    }),
    OBSIDIAN_UPGRADED_GOLD("obsidian_upgraded_gold", 19, new int[]{2, 5, 6, 2}, 20, 2,
            SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.CRYING_OBSIDIAN);
    }),
    OBSIDIAN_POWER_GOLD("obsidian_power_gold", 33, new int[]{3, 6, 8, 3}, 20, 3,
            SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.GILDED_BLACKSTONE);
    }),
    FIRE_BASE_GOLD("fire_base_gold", 13, new int[]{2, 4, 5, 1}, 28, 1,
            SoundEvents.BUCKET_FILL_LAVA, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.MAGMA_BLOCK);
    }),
    FIRE_UPGRADED_GOLD("fire_upgraded_gold", 16, new int[]{2, 5, 6, 2}, 28, 2,
            SoundEvents.BUCKET_FILL_LAVA, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.MAGMA_CREAM);
    }),
    FIRE_POWER_GOLD("fire_power_gold", 28, new int[]{3, 6, 8, 3}, 28, 3,
            SoundEvents.BUCKET_FILL_LAVA, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.DRAGON_BREATH);
    }),
    SEA_BASE_GOLD("sea_base_gold", 13, new int[]{2, 4, 5, 1}, 30, 1,
            SoundEvents.BUCKET_FILL, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.SALMON_BUCKET);
    }),
    SEA_UPGRADED_GOLD("sea_upgraded_gold", 16, new int[]{2, 5, 6, 2}, 30, 2,
            SoundEvents.BUCKET_FILL, 0.5F, 0.0F, () -> {
        return Ingredient.of(Items.NAUTILUS_SHELL);
    }),
    SEA_POWER_GOLD("sea_power_gold", 28, new int[]{3, 6, 8, 3}, 30, 3,
            SoundEvents.BUCKET_FILL, 1.0F, 0.0F, () -> {
        return Ingredient.of(Items.CONDUIT);
    }),
    STORM_BASE_GOLD("storm_base_gold", 13, new int[]{2, 4, 5, 1}, 25, 1,
            SoundEvents.LIGHTNING_BOLT_IMPACT, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.HONEYCOMB);
    }),
    STORM_UPGRADED_GOLD("storm_upgraded_gold", 16, new int[]{2, 5, 6, 2}, 25, 2,
            SoundEvents.LIGHTNING_BOLT_IMPACT, 0.5F, 0.0F, () -> {
        return Ingredient.of(Items.SLIME_BLOCK);
    }),
    STORM_POWER_GOLD("storm_power_gold", 28, new int[]{3, 6, 8, 3}, 25, 3,
            SoundEvents.LIGHTNING_BOLT_IMPACT, 1.0F, 0.0F, () -> {
        return Ingredient.of(Items.SHULKER_SHELL);
    })
    ;
;

    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int maxDamageFactor;
    private final int[] damageReductionAmountArray;
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyValue<Ingredient> repairMaterial;
    private final int level;

    ModArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, int level, SoundEvent soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionAmountArray;
        this.enchantability = enchantability;
        this.soundEvent = soundEvent;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairMaterial = new LazyValue<>(repairMaterial);
        this.level = level;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlotType slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slotIn) {
        return this.damageReductionAmountArray[slotIn.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @NotNull
    @Override
    public SoundEvent getEquipSound() {
        return this.soundEvent;
    }

    @NotNull
    @Override
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }

    @NotNull
    @OnlyIn(Dist.CLIENT)
    public String getName() {
        return GoldUpgrades.MOD_ID + ":" + this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public int getLevel() {
        return this.level;
    }
}
