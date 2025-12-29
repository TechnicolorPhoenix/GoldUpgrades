package com.titanhex.goldupgrades.item.registries.tool;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.item.ModItemGroup;
import com.titanhex.goldupgrades.item.ModItemTier;
import com.titanhex.goldupgrades.item.ModToolItems;
import com.titanhex.goldupgrades.item.custom.tools.effect.*;
import com.titanhex.goldupgrades.item.custom.tools.storm.*;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class StormToolItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoldUpgrades.MOD_ID);

    private static Map<Effect, Integer> createBaseBonuses() {
        Map<Effect, Integer> bonuses = new HashMap<>();
        bonuses.put(Effects.DIG_SPEED, 0);
        return bonuses;
    }
    private static Map<Effect, Integer> createUpgradedBonuses() {
        Map<Effect, Integer> bonuses = new HashMap<>();
        bonuses.put(Effects.DIG_SPEED, 1);
        bonuses.put(Effects.MOVEMENT_SPEED, 1);
        return bonuses;
    }
    private static Map<Effect, Integer> createPowerBonuses() {
        Map<Effect, Integer> bonuses = new HashMap<>();
        bonuses.put(Effects.DIG_SPEED, 2);
        bonuses.put(Effects.MOVEMENT_SPEED, 1);
        bonuses.put(Effects.DAMAGE_BOOST, 1);
        return bonuses;
    }

    static Map<Effect, Integer> baseToolBonus = createBaseBonuses();
    static Map<Effect, Integer> upgradedToolBonus = createUpgradedBonuses();
    static Map<Effect, Integer> powerToolBonus = createPowerBonuses();
    
    // BASE
    public static final RegistryObject<Item> STORM_BASE_GOLD_SWORD = ITEMS.register("storm_base_gold_sword",
            () -> new StormGoldSword(ModItemTier.STORM_BASE_GOLD, 4, -2.4F,
                    baseToolBonus, 600, 4,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_BASE_GOLD_SHOVEL = ITEMS.register("storm_base_gold_shovel",
            () -> new StormGoldShovel(ModItemTier.STORM_BASE_GOLD, 2.5F, -3.0F,
                    baseToolBonus, 600, 4,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_BASE_GOLD_PICKAXE = ITEMS.register("storm_base_gold_pickaxe",
            () -> new StormGoldPickaxe(ModItemTier.STORM_BASE_GOLD, 2, -2.8F,
                    baseToolBonus, 600, 4,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_BASE_GOLD_AXE = ITEMS.register("storm_base_gold_axe",
            () -> new StormGoldAxe(ModItemTier.STORM_BASE_GOLD, 7, -3.0F,
                    baseToolBonus, 600, 4,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_BASE_GOLD_HOE = ITEMS.register("storm_base_gold_hoe",
            () -> new StormGoldHoe(ModItemTier.STORM_BASE_GOLD, 1, -3.0F,
                    baseToolBonus, 600, 4,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));

    //UPGRADED
    public static final RegistryObject<Item> STORM_UPGRADED_GOLD_SWORD = ITEMS.register("storm_upgraded_gold_sword",
            () -> new StormGoldSword(ModItemTier.STORM_UPGRADED_GOLD, 5, -2.4F,
                    upgradedToolBonus, 600, 4,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_UPGRADED_GOLD_SHOVEL = ITEMS.register("storm_upgraded_gold_shovel",
            () -> new StormGoldShovel(ModItemTier.STORM_UPGRADED_GOLD, 3.0F, -3.0F,
                    upgradedToolBonus, 600, 4,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_UPGRADED_GOLD_PICKAXE = ITEMS.register("storm_upgraded_gold_pickaxe",
            () -> new StormGoldPickaxe(ModItemTier.STORM_UPGRADED_GOLD, 2, -2.8F,
                    upgradedToolBonus, 600, 4,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_UPGRADED_GOLD_AXE = ITEMS.register("storm_upgraded_gold_axe",
            () -> new StormGoldAxe(ModItemTier.STORM_UPGRADED_GOLD, 8, -3.0F,
                    upgradedToolBonus, 600, 4,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_UPGRADED_GOLD_HOE = ITEMS.register("storm_upgraded_gold_hoe",
            () -> new StormGoldHoe(ModItemTier.STORM_UPGRADED_GOLD, 1, -3.0F,
                    upgradedToolBonus, 600, 4,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    
    // POWER
    public static final RegistryObject<Item> STORM_POWER_GOLD_SWORD = ITEMS.register("storm_power_gold_sword",
            () -> new StormGoldSword(ModItemTier.STORM_POWER_GOLD, 6, -2.4F,
                    powerToolBonus, 1200, 5,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_POWER_GOLD_SHOVEL = ITEMS.register("storm_power_gold_shovel",
            () -> new StormGoldShovel(ModItemTier.STORM_POWER_GOLD, 3.5F, -3.0F,
                    powerToolBonus, 1200, 5,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_POWER_GOLD_PICKAXE = ITEMS.register("storm_power_gold_pickaxe",
            () -> new StormGoldPickaxe(ModItemTier.STORM_POWER_GOLD, 3, -2.8F,
                    powerToolBonus, 1200, 5,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_POWER_GOLD_AXE = ITEMS.register("storm_power_gold_axe",
            () -> new StormGoldAxe(ModItemTier.STORM_POWER_GOLD, 9, -3.0F,
                    powerToolBonus, 1200, 5,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> STORM_POWER_GOLD_HOE = ITEMS.register("storm_power_gold_hoe",
            () -> new StormGoldHoe(ModItemTier.STORM_POWER_GOLD, 1, -3.0F,
                    powerToolBonus, 1200, 5,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
}
