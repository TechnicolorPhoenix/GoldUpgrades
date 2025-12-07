package com.titanhex.goldupgrades.item.armor;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.item.ModArmorMaterial;
import com.titanhex.goldupgrades.item.ModItemGroup;
import com.titanhex.goldupgrades.item.custom.CustomEffectJumpArmor;
import com.titanhex.goldupgrades.item.custom.armor.StormArmorItem;
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

public class StormArmorItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoldUpgrades.MOD_ID);

    private static Map<Effect, Integer> createPowerArmorBonuses(Integer amount) {
        // This is where the executable code (.put) is now correctly placed (inside a method).
        Map<Effect, Integer> bonuses = new HashMap<>();
        bonuses.put(Effects.SLOW_FALLING, amount);
        bonuses.put(Effects.DIG_SPEED, amount);
        return bonuses;
    }

    private static final Map<Effect, Integer> STORM_POWER_ARMOR_EFFECTS = createPowerArmorBonuses(0);

    public static final RegistryObject<Item> STORM_BASE_GOLD_BOOTS = ITEMS.register("storm_base_gold_boots",
            () -> new StormArmorItem(ModArmorMaterial.STORM_BASE_GOLD, EquipmentSlotType.FEET,
                    0.033F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_BASE_GOLD_LEGGINGS = ITEMS.register("storm_base_gold_leggings",
            () -> new StormArmorItem(ModArmorMaterial.STORM_BASE_GOLD, EquipmentSlotType.LEGS,
                    0.033F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_BASE_GOLD_CHESTPLATE = ITEMS.register("storm_base_gold_chestplate",
            () -> new StormArmorItem(ModArmorMaterial.STORM_BASE_GOLD, EquipmentSlotType.CHEST,
                    0.033F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_BASE_GOLD_HELMET = ITEMS.register("storm_base_gold_helmet",
            () -> new StormArmorItem(ModArmorMaterial.STORM_BASE_GOLD, EquipmentSlotType.HEAD,
                    0.033F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));

    public static final RegistryObject<Item> STORM_UPGRADED_GOLD_BOOTS = ITEMS.register("storm_upgraded_gold_boots",
            () -> new StormArmorItem(ModArmorMaterial.STORM_UPGRADED_GOLD, EquipmentSlotType.FEET, 0.05F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_UPGRADED_GOLD_LEGGINGS = ITEMS.register("storm_upgraded_gold_leggings",
            () -> new StormArmorItem(ModArmorMaterial.STORM_UPGRADED_GOLD, EquipmentSlotType.LEGS, 0.05F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_UPGRADED_GOLD_CHESTPLATE = ITEMS.register("storm_upgraded_gold_chestplate",
            () -> new StormArmorItem(ModArmorMaterial.STORM_UPGRADED_GOLD, EquipmentSlotType.CHEST, 0.05F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_UPGRADED_GOLD_HELMET = ITEMS.register("storm_upgraded_gold_helmet",
            () -> new StormArmorItem(ModArmorMaterial.STORM_UPGRADED_GOLD, EquipmentSlotType.HEAD, 0.05F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));

    public static final RegistryObject<Item> STORM_POWER_GOLD_BOOTS = ITEMS.register("storm_power_gold_boots",
            () -> new CustomEffectJumpArmor(ModArmorMaterial.STORM_POWER_GOLD, EquipmentSlotType.FEET, 0.1F,
                    STORM_POWER_ARMOR_EFFECTS,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_POWER_GOLD_LEGGINGS = ITEMS.register("storm_power_gold_leggings",
            () -> new CustomEffectJumpArmor(ModArmorMaterial.STORM_POWER_GOLD, EquipmentSlotType.LEGS, 0.1F,
                    STORM_POWER_ARMOR_EFFECTS,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_POWER_GOLD_CHESTPLATE = ITEMS.register("storm_power_gold_chestplate",
            () -> new CustomEffectJumpArmor(ModArmorMaterial.STORM_POWER_GOLD, EquipmentSlotType.CHEST, 0.1F,
                    STORM_POWER_ARMOR_EFFECTS,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_POWER_GOLD_HELMET = ITEMS.register("storm_power_gold_helmet",
            () -> new CustomEffectJumpArmor(ModArmorMaterial.STORM_POWER_GOLD, EquipmentSlotType.HEAD, 0.1F,
                    STORM_POWER_ARMOR_EFFECTS,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
}
