package com.titanhex.goldupgrades.item.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.item.ModArmorItems;
import com.titanhex.goldupgrades.item.ModArmorMaterial;
import com.titanhex.goldupgrades.item.ModItemGroup;
import com.titanhex.goldupgrades.item.custom.CustomAttributeArmor;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FireArmorItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoldUpgrades.MOD_ID);

    private static Multimap<Attribute, Double> createHealthArmorBonuses(double amount) {
        // This is where the executable code (.put) is now correctly placed (inside a method).
        Multimap<Attribute, Double> bonuses = HashMultimap.create();
        bonuses.put(Attributes.ATTACK_DAMAGE, amount);
        return bonuses;
    }

    // --- Static constant to hold the reusable attribute map ---
    private static final Multimap<Attribute, Double> FIRE_UPGRADED_ARMOR_BONUSES = createHealthArmorBonuses(0.5);
    private static final Multimap<Attribute, Double> FIRE_POWER_ARMOR_BONUSES = createHealthArmorBonuses(2);

    public static final RegistryObject<Item> FIRE_BASE_GOLD_BOOTS = ITEMS.register(
            "fire_base_gold_boots",
            () -> new ArmorItem(
                    ModArmorMaterial.FIRE_BASE_GOLD,
                    EquipmentSlotType.FEET,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_BASE_GOLD_LEGGINGS = ITEMS.register(
            "fire_base_gold_leggings",
            () -> new ArmorItem(
                    ModArmorMaterial.FIRE_BASE_GOLD,
                    EquipmentSlotType.LEGS,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_BASE_GOLD_CHESTPLATE = ITEMS.register(
            "fire_base_gold_chestplate",
            () -> new ArmorItem(
                    ModArmorMaterial.FIRE_BASE_GOLD,
                    EquipmentSlotType.CHEST,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_BASE_GOLD_HELMET = ITEMS.register(
            "fire_base_gold_helmet",
            () -> new ArmorItem(
                    ModArmorMaterial.FIRE_BASE_GOLD,
                    EquipmentSlotType.HEAD,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)
            )
    );

    // UPGRADED
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_BOOTS = ITEMS.register(
            "fire_upgraded_gold_boots",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD,
                    EquipmentSlotType.FEET,
                    FIRE_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_LEGGINGS = ITEMS.register(
            "fire_upgraded_gold_leggings",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD,
                    EquipmentSlotType.LEGS,
                    FIRE_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_CHESTPLATE = ITEMS.register(
            "fire_upgraded_gold_chestplate",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD,
                    EquipmentSlotType.CHEST,
                    FIRE_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_HELMET = ITEMS.register(
            "fire_upgraded_gold_helmet",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD,
                    EquipmentSlotType.HEAD,
                    FIRE_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );

    // POWER
    public static final RegistryObject<Item> FIRE_POWER_GOLD_BOOTS = ITEMS.register(
            "fire_power_gold_boots",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.FIRE_POWER_GOLD,
                    EquipmentSlotType.FEET,
                    FIRE_POWER_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_POWER_GOLD_LEGGINGS = ITEMS.register(
            "fire_power_gold_leggings",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.FIRE_POWER_GOLD,
                    EquipmentSlotType.LEGS,
                    FIRE_POWER_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_POWER_GOLD_CHESTPLATE = ITEMS.register(
            "fire_power_gold_chestplate",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.FIRE_POWER_GOLD,
                    EquipmentSlotType.CHEST,
                    FIRE_POWER_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_POWER_GOLD_HELMET = ITEMS.register(
            "fire_power_gold_helmet",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.FIRE_POWER_GOLD,
                    EquipmentSlotType.HEAD,
                    FIRE_POWER_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );

}
