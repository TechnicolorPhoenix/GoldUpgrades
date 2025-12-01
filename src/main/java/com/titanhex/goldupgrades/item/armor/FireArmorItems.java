package com.titanhex.goldupgrades.item.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.item.ModArmorItems;
import com.titanhex.goldupgrades.item.ModArmorMaterial;
import com.titanhex.goldupgrades.item.ModItemGroup;
import com.titanhex.goldupgrades.item.custom.CustomAttributeArmor;
import com.titanhex.goldupgrades.item.custom.armor.FireArmorItem;
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
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD,
                    0.1F,
                    60,
                    EquipmentSlotType.FEET,
                    0.5F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_LEGGINGS = ITEMS.register(
            "fire_upgraded_gold_leggings",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD,
                    0.1F,
                    60,
                    EquipmentSlotType.LEGS,
                    0.5F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_CHESTPLATE = ITEMS.register(
            "fire_upgraded_gold_chestplate",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD,
                    0.1F,
                    60,
                    EquipmentSlotType.CHEST,
                    0.5F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_HELMET = ITEMS.register(
            "fire_upgraded_gold_helmet",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD,
                    0.1F,
                    60,
                    EquipmentSlotType.HEAD,
                    0.5F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );

    // POWER
    public static final RegistryObject<Item> FIRE_POWER_GOLD_BOOTS = ITEMS.register(
            "fire_power_gold_boots",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_POWER_GOLD,
                    0.1F,
                    20,
                    EquipmentSlotType.FEET,
                    1F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_POWER_GOLD_LEGGINGS = ITEMS.register(
            "fire_power_gold_leggings",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_POWER_GOLD,
                    0.1F,
                    20,
                    EquipmentSlotType.LEGS,
                    1F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_POWER_GOLD_CHESTPLATE = ITEMS.register(
            "fire_power_gold_chestplate",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_POWER_GOLD,
                    0.1F,
                    20,
                    EquipmentSlotType.CHEST,
                    1F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_POWER_GOLD_HELMET = ITEMS.register(
            "fire_power_gold_helmet",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_POWER_GOLD,
                    0.1F,
                    20,
                    EquipmentSlotType.HEAD,
                    1F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );

}
