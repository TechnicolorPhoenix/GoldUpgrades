package com.titanhex.goldupgrades.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.attribute.ModAttributes;
import com.titanhex.goldupgrades.item.ModItemGroup;
import com.titanhex.goldupgrades.item.ModItemTier;
import com.titanhex.goldupgrades.item.ModToolItems;
import com.titanhex.goldupgrades.item.custom.tools.sea.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class SeaToolItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoldUpgrades.MOD_ID);

    private static final int baseDuration = 20;
    private static final int baseCost = 8;
    private static final int upgradedDuration = 30;
    private static final int upgradedCost = 6;
    private static final int powerDuration = 60;
    private static final int powerCost = 4;

    private static Map<Effect, Integer> createBonuses() {
        // This is where the executable code (.put) is now correctly placed (inside a method).
        Map<Effect, Integer> bonuses = new HashMap<>();
        bonuses.put(Effects.ABSORPTION, 1);
        bonuses.put(Effects.GLOWING, 1);
        bonuses.put(Effects.MOVEMENT_SLOWDOWN, 1);
        return bonuses;
    }

    static Map<Effect, Integer> baseToolBonus = createBonuses();
    static Map<Effect, Integer> upgradedToolBonus = createBonuses();
    static Map<Effect, Integer> powerToolBonus = createBonuses();
    
    // BASE
    public static final RegistryObject<Item> SEA_BASE_GOLD_SWORD = ITEMS.register("sea_base_gold_sword",
            () -> new SeaGoldSword(ModItemTier.SEA_BASE_GOLD, 4, -2.4F,
                    baseToolBonus, baseDuration, baseCost,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_BASE_GOLD_SHOVEL = ITEMS.register("sea_base_gold_shovel",
            () -> new SeaGoldShovel(ModItemTier.SEA_BASE_GOLD, 2.5F, -3.0F,
                    baseToolBonus, baseDuration, baseCost,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_BASE_GOLD_PICKAXE = ITEMS.register("sea_base_gold_pickaxe",
            () -> new SeaGoldPickaxe(ModItemTier.SEA_BASE_GOLD, 2, -2.8F,
                    baseToolBonus, baseDuration, baseCost,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_BASE_GOLD_AXE = ITEMS.register("sea_base_gold_axe",
            () -> new SeaGoldAxe(ModItemTier.SEA_BASE_GOLD, 7.0F, -3.0F,
                    baseToolBonus, baseDuration, baseCost,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_BASE_GOLD_HOE = ITEMS.register("sea_base_gold_hoe",
            () -> new SeaGoldHoe(ModItemTier.SEA_BASE_GOLD, 1, -3.0F,
                    baseToolBonus, baseDuration, baseCost,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));

    //UPGRADED
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_SWORD = ITEMS.register("sea_upgraded_gold_sword",
            () -> new SeaGoldSword(ModItemTier.SEA_UPGRADED_GOLD, 5, -2.4F,
                    upgradedToolBonus, upgradedDuration, upgradedCost,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_SHOVEL = ITEMS.register("sea_upgraded_gold_shovel",
            () -> new SeaGoldShovel(ModItemTier.SEA_UPGRADED_GOLD, 3.0F, -3.0F,
                    upgradedToolBonus, upgradedDuration, upgradedCost,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_PICKAXE = ITEMS.register("sea_upgraded_gold_pickaxe",
            () -> new SeaGoldPickaxe(ModItemTier.SEA_UPGRADED_GOLD, 3, -2.8F,
                    upgradedToolBonus, upgradedDuration, upgradedCost,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_AXE = ITEMS.register("sea_upgraded_gold_axe",
            () -> new SeaGoldAxe(ModItemTier.SEA_UPGRADED_GOLD, 8.0F, -3.0F,
                    upgradedToolBonus, upgradedDuration, upgradedCost,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_UPGRADED_GOLD_HOE = ITEMS.register("sea_upgraded_gold_hoe",
            () -> new SeaGoldHoe(ModItemTier.SEA_UPGRADED_GOLD, 1, -3.0F,
                    upgradedToolBonus, upgradedDuration, upgradedCost,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    
    // POWER
    public static final RegistryObject<Item> SEA_POWER_GOLD_SWORD = ITEMS.register("sea_power_gold_sword",
            () -> new SeaGoldSword(ModItemTier.SEA_POWER_GOLD, 6, -2.4F,
                    powerToolBonus, powerDuration, powerCost,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_POWER_GOLD_SHOVEL = ITEMS.register("sea_power_gold_shovel",
            () -> new SeaGoldShovel(ModItemTier.SEA_POWER_GOLD, 3.5F, -3.0F,
                    powerToolBonus, powerDuration, powerCost,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_POWER_GOLD_PICKAXE = ITEMS.register("sea_power_gold_pickaxe",
            () -> new SeaGoldPickaxe(ModItemTier.SEA_POWER_GOLD, 3, -2.8F,
                    powerToolBonus, powerDuration, powerCost,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_POWER_GOLD_AXE = ITEMS.register("sea_power_gold_axe",
            () -> new SeaGoldAxe(ModItemTier.SEA_POWER_GOLD, 9.0F, -3.0F,
                    powerToolBonus, powerDuration, powerCost,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> SEA_POWER_GOLD_HOE = ITEMS.register("sea_power_gold_hoe",
            () -> new SeaGoldHoe(ModItemTier.SEA_POWER_GOLD, 1, -3.0F,
                    powerToolBonus, powerDuration, powerCost,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
}
