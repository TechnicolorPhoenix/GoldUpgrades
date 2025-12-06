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

    public static final RegistryObject<Item> FIRE_BASE_GOLD_BOOTS = ITEMS.register(
            "fire_base_gold_boots",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_BASE_GOLD, EquipmentSlotType.FEET,
                    0.1F, 100, 0.25F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_BASE_GOLD_LEGGINGS = ITEMS.register(
            "fire_base_gold_leggings",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_BASE_GOLD, EquipmentSlotType.LEGS,
                    0.1F, 100, 0.25F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_BASE_GOLD_CHESTPLATE = ITEMS.register(
            "fire_base_gold_chestplate",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_BASE_GOLD, EquipmentSlotType.CHEST,
                    0.1F, 100, 0.25F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_BASE_GOLD_HELMET = ITEMS.register(
            "fire_base_gold_helmet",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_BASE_GOLD, EquipmentSlotType.HEAD,
                    0.1F, 100, 0.25F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)
            )
    );

    // UPGRADED
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_BOOTS = ITEMS.register(
            "fire_upgraded_gold_boots",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD, EquipmentSlotType.FEET,
                    0.1F, 60, 0.5F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_LEGGINGS = ITEMS.register(
            "fire_upgraded_gold_leggings",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD, EquipmentSlotType.LEGS,
                    0.1F, 60, 0.5F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_CHESTPLATE = ITEMS.register(
            "fire_upgraded_gold_chestplate",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD, EquipmentSlotType.CHEST,
                    0.1F, 60, 0.5F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_HELMET = ITEMS.register(
            "fire_upgraded_gold_helmet",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_UPGRADED_GOLD, EquipmentSlotType.HEAD,
                    0.1F, 60, 0.5F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );

    // POWER
    public static final RegistryObject<Item> FIRE_POWER_GOLD_BOOTS = ITEMS.register(
            "fire_power_gold_boots",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_POWER_GOLD, EquipmentSlotType.FEET,
                    0.1F, 20, 1F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_POWER_GOLD_LEGGINGS = ITEMS.register(
            "fire_power_gold_leggings",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_POWER_GOLD, EquipmentSlotType.LEGS,
                    0.1F, 20, 1F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_POWER_GOLD_CHESTPLATE = ITEMS.register(
            "fire_power_gold_chestplate",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_POWER_GOLD, EquipmentSlotType.CHEST,
                    0.1F, 20, 1F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> FIRE_POWER_GOLD_HELMET = ITEMS.register(
            "fire_power_gold_helmet",
            () -> new FireArmorItem(
                    ModArmorMaterial.FIRE_POWER_GOLD, EquipmentSlotType.HEAD,
                    0.1F, 20, 1F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );

}
