package com.titanhex.goldupgrades.item;

import com.titanhex.goldupgrades.GoldUpgrades;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public enum ModArmorMaterial implements IArmorMaterial {
    OBSIDIAN_BASE_GOLD("obsidian_base_gold", 15, new int[]{2, 4, 5, 1}, 20,
            SoundEvents.ARMOR_EQUIP_GOLD, 0.5F, 0.0F, () -> {
        return Ingredient.of(Items.OBSIDIAN);
    }),
    OBSIDIAN_UPGRADED_GOLD("obsidian_upgraded_gold", 19, new int[]{2, 5, 6, 2}, 20,
            SoundEvents.ARMOR_EQUIP_GOLD, 1.5F, 0.0F, () -> {
        return Ingredient.of(Items.CRYING_OBSIDIAN);
    }),
    OBSIDIAN_POWER_GOLD("obsidian_power_gold", 33, new int[]{3, 6, 8, 3}, 20,
            SoundEvents.ARMOR_EQUIP_GOLD, 2.5F, 0.0F, () -> {
        return Ingredient.of(Items.GILDED_BLACKSTONE);
    }),
    FIRE_BASE_GOLD("fire_base_gold", 13, new int[]{2, 4, 5, 1}, 28,
            SoundEvents.BUCKET_FILL_LAVA, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.MAGMA_BLOCK);
    }),
    FIRE_UPGRADED_GOLD("fire_upgraded_gold", 16, new int[]{2, 5, 6, 2}, 28,
            SoundEvents.BUCKET_FILL_LAVA, 1.0F, 0.0F, () -> {
        return Ingredient.of(Items.MAGMA_CREAM);
    }),
    FIRE_POWER_GOLD("fire_power_gold", 28, new int[]{3, 6, 8, 3}, 28,
            SoundEvents.BUCKET_FILL_LAVA, 2.0F, 0.0F, () -> {
        return Ingredient.of(Items.WITHER_SKELETON_SKULL);
    }),
    SEA_BASE_GOLD("sea_base_gold", 13, new int[]{2, 4, 5, 1}, 30,
            SoundEvents.BUCKET_FILL, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.SALMON_BUCKET);
    }),
    SEA_UPGRADED_GOLD("sea_upgraded_gold", 16, new int[]{2, 5, 6, 2}, 30,
            SoundEvents.BUCKET_FILL, 1.0F, 0.0F, () -> {
        return Ingredient.of(Items.NAUTILUS_SHELL);
    }),
    SEA_POWER_GOLD("sea_power_gold", 28, new int[]{3, 6, 8, 3}, 30,
            SoundEvents.BUCKET_FILL, 2.0F, 0.0F, () -> {
        return Ingredient.of(Items.CONDUIT);
    }),
    STORM_BASE_GOLD("storm_base_gold", 13, new int[]{2, 4, 5, 1}, 25,
            SoundEvents.LIGHTNING_BOLT_IMPACT, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.HONEYCOMB);
    }),
    STORM_UPGRADED_GOLD("storm_upgraded_gold", 16, new int[]{2, 5, 6, 2}, 25,
            SoundEvents.LIGHTNING_BOLT_IMPACT, 1.0F, 0.0F, () -> {
        return Ingredient.of(Items.SLIME_BLOCK);
    }),
    STORM_POWER_GOLD("storm_power_gold", 28, new int[]{3, 6, 8, 3}, 25,
            SoundEvents.LIGHTNING_BOLT_IMPACT, 2.0F, 0.0F, () -> {
        return Ingredient.of(Items.SHULKER_SHELL);
    }),
    RAID_BASE("raid_base", 7, new int[]{2, 4, 5, 1}, 10,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.LEATHER);
    }),
    RAID_UPGRADED("raid_upgraded", 10, new int[]{2, 5, 6, 2}, 13,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.EMERALD);
    }),
    RAID_POWER("raid_power", 13, new int[]{3, 6, 8, 3}, 16,
            SoundEvents.ARMOR_EQUIP_LEATHER, 2.0F, 0.0F, () -> {
        return Ingredient.of(Items.EMERALD_BLOCK);
    }),
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
    private final float jumpHeight;
    private final int healthBonus;
    private final LazyValue<Ingredient> repairMaterial;

    private ModArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionAmountArray;
        this.enchantability = enchantability;
        this.soundEvent = soundEvent;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.jumpHeight = 0f;
        this.healthBonus = 0;
        this.repairMaterial = new LazyValue<>(repairMaterial);
    }

    private ModArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent, float toughness, float knockbackResistance, float jumpHeight, int healthBonus, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionAmountArray;
        this.enchantability = enchantability;
        this.soundEvent = soundEvent;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.jumpHeight = jumpHeight;
        this.healthBonus = healthBonus;
        this.repairMaterial = new LazyValue<>(repairMaterial);
    }

    private ModArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent, float toughness, float knockbackResistance, float jumpHeight, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionAmountArray;
        this.enchantability = enchantability;
        this.soundEvent = soundEvent;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.jumpHeight = jumpHeight;
        this.healthBonus = 0;
        this.repairMaterial = new LazyValue<>(repairMaterial);
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

    @Override
    public SoundEvent getEquipSound() {
        return this.soundEvent;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }

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
}
