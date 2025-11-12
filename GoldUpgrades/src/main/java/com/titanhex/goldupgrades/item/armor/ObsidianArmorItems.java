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

public class ObsidianArmorItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoldUpgrades.MOD_ID);

    private static Multimap<Attribute, Double> createHealthArmorBonuses(double amount) {
        // This is where the executable code (.put) is now correctly placed (inside a method).
        Multimap<Attribute, Double> bonuses = HashMultimap.create();
        bonuses.put(Attributes.MAX_HEALTH, amount);
        return bonuses;
    }

    // --- Static constant to hold the reusable attribute map ---x
    private static final Multimap<Attribute, Double> OBSIDIAN_UPGRADED_ARMOR_BONUSES = createHealthArmorBonuses(0.5);
    private static final Multimap<Attribute, Double> OBSIDIAN_POWER_ARMOR_BONUSES = createHealthArmorBonuses(3);

    public static final RegistryObject<Item> OBSIDIAN_BASE_GOLD_BOOTS = ITEMS.register(
            "obsidian_base_gold_boots",
            () -> new ArmorItem(ModArmorMaterial.OBSIDIAN_BASE_GOLD, EquipmentSlotType.FEET,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_BASE_GOLD_LEGGINGS = ITEMS.register(
            "obsidian_base_gold_leggings",
            () -> new ArmorItem(ModArmorMaterial.OBSIDIAN_BASE_GOLD, EquipmentSlotType.LEGS,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_BASE_GOLD_CHESTPLATE = ITEMS.register(
            "obsidian_base_gold_chestplate",
            () -> new ArmorItem(ModArmorMaterial.OBSIDIAN_BASE_GOLD, EquipmentSlotType.CHEST,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_BASE_GOLD_HELMET = ITEMS.register(
            "obsidian_base_gold_helmet",
            () -> new ArmorItem(ModArmorMaterial.OBSIDIAN_BASE_GOLD, EquipmentSlotType.HEAD,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));

    public static final RegistryObject<Item> OBSIDIAN_UPGRADED_GOLD_BOOTS = ITEMS.register(
            "obsidian_upgraded_gold_boots",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.OBSIDIAN_UPGRADED_GOLD,
                    EquipmentSlotType.FEET,
                    OBSIDIAN_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> OBSIDIAN_UPGRADED_GOLD_LEGGINGS = ITEMS.register(
            "obsidian_upgraded_gold_leggings",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.OBSIDIAN_UPGRADED_GOLD,
                    EquipmentSlotType.LEGS,
                    OBSIDIAN_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> OBSIDIAN_UPGRADED_GOLD_CHESTPLATE = ITEMS.register(
            "obsidian_upgraded_gold_chestplate",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.OBSIDIAN_UPGRADED_GOLD,
                    EquipmentSlotType.CHEST,
                    OBSIDIAN_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> OBSIDIAN_UPGRADED_GOLD_HELMET = ITEMS.register(
            "obsidian_upgraded_gold_helmet",
            () -> new CustomAttributeArmor(ModArmorMaterial.OBSIDIAN_UPGRADED_GOLD,
                    EquipmentSlotType.HEAD,
                    OBSIDIAN_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)
            )
    );

    public static final RegistryObject<Item> OBSIDIAN_POWER_GOLD_BOOTS = ITEMS.register(
            "obsidian_power_gold_boots",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.OBSIDIAN_POWER_GOLD,
                    EquipmentSlotType.FEET,
                    OBSIDIAN_POWER_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> OBSIDIAN_POWER_GOLD_LEGGINGS = ITEMS.register(
            "obsidian_power_gold_leggings",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.OBSIDIAN_POWER_GOLD,
                    EquipmentSlotType.LEGS,
                    OBSIDIAN_POWER_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> OBSIDIAN_POWER_GOLD_CHESTPLATE = ITEMS.register(
            "obsidian_power_gold_chestplate",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.OBSIDIAN_POWER_GOLD,
                    EquipmentSlotType.CHEST,
                    OBSIDIAN_POWER_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
    public static final RegistryObject<Item> OBSIDIAN_POWER_GOLD_HELMET = ITEMS.register(
            "obsidian_power_gold_helmet",
            () -> new CustomAttributeArmor(
                    ModArmorMaterial.OBSIDIAN_POWER_GOLD,
                    EquipmentSlotType.HEAD,
                    OBSIDIAN_POWER_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)
            )
    );
}
