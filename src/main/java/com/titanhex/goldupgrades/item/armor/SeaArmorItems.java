package com.titanhex.goldupgrades.item.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.item.ModArmorMaterial;
import com.titanhex.goldupgrades.item.ModItemGroup;
import com.titanhex.goldupgrades.item.custom.armor.SeaAttributeArmorItem;
import com.titanhex.goldupgrades.item.custom.armor.SeaArmorItem;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class SeaArmorItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoldUpgrades.MOD_ID);

    private static Multimap<Attribute, Double> createBaseArmorBonuses() {
        Multimap<Attribute, Double> bonuses = HashMultimap.create();
        bonuses.put(Attributes.LUCK, 0.2D);
        bonuses.put(Attributes.ATTACK_SPEED, 1.00D);
        return bonuses;
    }
    private static Multimap<Attribute, Double> createUpgradedArmorBonuses() {
        Multimap<Attribute, Double> bonuses = HashMultimap.create();
        bonuses.put(Attributes.LUCK, 0.5D);
        bonuses.put(Attributes.ATTACK_SPEED, 3.00D);
        return bonuses;
    }
    private static Map<Effect, Integer> createPowerArmorBonuses() {
        Map<Effect, Integer> bonuses = new HashMap<>();
        return bonuses;
    }

    private static final Multimap<Attribute, Double> SEA_UPGRADED_ARMOR_BONUSES = createBaseArmorBonuses();
    private static final Multimap<Attribute, Double> SEA_BASE_ARMOR_BONUSES = createUpgradedArmorBonuses();
    private static final Map<Effect, Integer> SEA_POWER_ARMOR_BONUSES = createPowerArmorBonuses();

    public static final RegistryObject<Item> SEA_BASE_GOLD_BOOTS = ITEMS.register("sea_base_gold_boots",
            () -> new SeaAttributeArmorItem(ModArmorMaterial.SEA_BASE_GOLD, EquipmentSlotType.FEET,
                    SEA_BASE_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_BASE_GOLD_LEGGINGS = ITEMS.register("sea_base_gold_leggings",
            () -> new SeaAttributeArmorItem(ModArmorMaterial.SEA_BASE_GOLD, EquipmentSlotType.LEGS,
                    SEA_BASE_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_BASE_GOLD_CHESTPLATE = ITEMS.register("sea_base_gold_chestplate",
            () -> new SeaAttributeArmorItem(ModArmorMaterial.SEA_BASE_GOLD, EquipmentSlotType.CHEST,
                    SEA_BASE_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_BASE_GOLD_HELMET = ITEMS.register("sea_base_gold_helmet",
            () -> new SeaAttributeArmorItem(ModArmorMaterial.SEA_BASE_GOLD, EquipmentSlotType.HEAD,
                    SEA_BASE_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));

    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_BOOTS = ITEMS.register("sea_upgraded_gold_boots",
            () -> new SeaAttributeArmorItem(ModArmorMaterial.SEA_UPGRADED_GOLD, EquipmentSlotType.FEET,
                    SEA_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_LEGGINGS = ITEMS.register("sea_upgraded_gold_leggings",
            () -> new SeaAttributeArmorItem(ModArmorMaterial.SEA_UPGRADED_GOLD, EquipmentSlotType.LEGS,
                    SEA_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_CHESTPLATE = ITEMS.register("sea_upgraded_gold_chestplate",
            () -> new SeaAttributeArmorItem(ModArmorMaterial.SEA_UPGRADED_GOLD, EquipmentSlotType.CHEST,
                    SEA_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_HELMET = ITEMS.register("sea_upgraded_gold_helmet",
            () -> new SeaAttributeArmorItem(ModArmorMaterial.SEA_UPGRADED_GOLD, EquipmentSlotType.HEAD,
                    SEA_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));

    public static final RegistryObject<Item> SEA_POWER_GOLD_BOOTS = ITEMS.register("sea_power_gold_boots",
            () -> new SeaArmorItem(ModArmorMaterial.SEA_POWER_GOLD, EquipmentSlotType.FEET,
                    SEA_POWER_ARMOR_BONUSES, SEA_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_POWER_GOLD_LEGGINGS = ITEMS.register("sea_power_gold_leggings",
            () -> new SeaArmorItem(ModArmorMaterial.SEA_POWER_GOLD, EquipmentSlotType.LEGS,
                    SEA_POWER_ARMOR_BONUSES, SEA_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_POWER_GOLD_CHESTPLATE = ITEMS.register("sea_power_gold_chestplate",
            () -> new SeaArmorItem(ModArmorMaterial.SEA_POWER_GOLD, EquipmentSlotType.CHEST,
                    SEA_POWER_ARMOR_BONUSES, SEA_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_POWER_GOLD_HELMET = ITEMS.register("sea_power_gold_helmet",
            () -> new SeaArmorItem(ModArmorMaterial.SEA_POWER_GOLD, EquipmentSlotType.HEAD,
                    SEA_POWER_ARMOR_BONUSES, SEA_UPGRADED_ARMOR_BONUSES,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
}
