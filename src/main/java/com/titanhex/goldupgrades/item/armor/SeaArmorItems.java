package com.titanhex.goldupgrades.item.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.item.ModArmorMaterial;
import com.titanhex.goldupgrades.item.ModItemGroup;
import com.titanhex.goldupgrades.item.custom.armor.ScubaAttributeArmorItem;
import com.titanhex.goldupgrades.item.custom.armor.ScubaEffectArmorItem;
import com.titanhex.goldupgrades.item.custom.armor.ScubaEffectAttributeArmor;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
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

    private static Multimap<Attribute, Double> createUpgradedArmorBonuses() {
        Multimap<Attribute, Double> bonuses = HashMultimap.create();
        bonuses.put(Attributes.LUCK, 0.5D);
        bonuses.put(Attributes.ATTACK_SPEED, 3.00D);
        return bonuses;
    }
    private static Map<Effect, Integer> createPowerArmorBonuses() {
        Map<Effect, Integer> bonuses = new HashMap<>();
        bonuses.put(Effects.WATER_BREATHING, 1);
        bonuses.put(Effects.DOLPHINS_GRACE, 1);
        return bonuses;
    }

    private static final Multimap<Attribute, Double> SEA_UPGRADED_ARMOR_BONUSES = createUpgradedArmorBonuses();
    private static final Map<Effect, Integer> SEA_POWER_ARMOR_BONUSES = createPowerArmorBonuses();

    public static final RegistryObject<Item> SEA_BASE_GOLD_BOOTS = ITEMS.register("sea_base_gold_boots",
            () -> new ArmorItem(ModArmorMaterial.SEA_BASE_GOLD, EquipmentSlotType.FEET,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_BASE_GOLD_LEGGINGS = ITEMS.register("sea_base_gold_leggings",
            () -> new ArmorItem(ModArmorMaterial.SEA_BASE_GOLD, EquipmentSlotType.LEGS,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_BASE_GOLD_CHESTPLATE = ITEMS.register("sea_base_gold_chestplate",
            () -> new ArmorItem(ModArmorMaterial.SEA_BASE_GOLD, EquipmentSlotType.CHEST,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_BASE_GOLD_HELMET = ITEMS.register("sea_base_gold_helmet",
            () -> new ArmorItem(ModArmorMaterial.SEA_BASE_GOLD, EquipmentSlotType.HEAD,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));

    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_BOOTS = ITEMS.register("sea_upgraded_gold_boots",
            () -> new ScubaAttributeArmorItem(ModArmorMaterial.SEA_UPGRADED_GOLD, EquipmentSlotType.FEET,
                    SEA_UPGRADED_ARMOR_BONUSES, 15,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_LEGGINGS = ITEMS.register("sea_upgraded_gold_leggings",
            () -> new ScubaAttributeArmorItem(ModArmorMaterial.SEA_UPGRADED_GOLD, EquipmentSlotType.LEGS,
                    SEA_UPGRADED_ARMOR_BONUSES, 15,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_CHESTPLATE = ITEMS.register("sea_upgraded_gold_chestplate",
            () -> new ScubaAttributeArmorItem(ModArmorMaterial.SEA_UPGRADED_GOLD, EquipmentSlotType.CHEST,
                    SEA_UPGRADED_ARMOR_BONUSES, 15,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_HELMET = ITEMS.register("sea_upgraded_gold_helmet",
            () -> new ScubaAttributeArmorItem(ModArmorMaterial.SEA_UPGRADED_GOLD, EquipmentSlotType.HEAD,
                    SEA_UPGRADED_ARMOR_BONUSES, 15,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));

    public static final RegistryObject<Item> SEA_POWER_GOLD_BOOTS = ITEMS.register("sea_power_gold_boots",
            () -> new ScubaEffectAttributeArmor(ModArmorMaterial.SEA_POWER_GOLD, EquipmentSlotType.FEET,
                    SEA_POWER_ARMOR_BONUSES, SEA_UPGRADED_ARMOR_BONUSES, 15,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_POWER_GOLD_LEGGINGS = ITEMS.register("sea_power_gold_leggings",
            () -> new ScubaEffectAttributeArmor(ModArmorMaterial.SEA_POWER_GOLD, EquipmentSlotType.LEGS,
                    SEA_POWER_ARMOR_BONUSES, SEA_UPGRADED_ARMOR_BONUSES, 15,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_POWER_GOLD_CHESTPLATE = ITEMS.register("sea_power_gold_chestplate",
            () -> new ScubaEffectAttributeArmor(ModArmorMaterial.SEA_POWER_GOLD, EquipmentSlotType.CHEST,
                    SEA_POWER_ARMOR_BONUSES, SEA_UPGRADED_ARMOR_BONUSES, 15,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_POWER_GOLD_HELMET = ITEMS.register("sea_power_gold_helmet",
            () -> new ScubaEffectAttributeArmor(ModArmorMaterial.SEA_POWER_GOLD, EquipmentSlotType.HEAD,
                    SEA_POWER_ARMOR_BONUSES, SEA_UPGRADED_ARMOR_BONUSES, 15,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
}
