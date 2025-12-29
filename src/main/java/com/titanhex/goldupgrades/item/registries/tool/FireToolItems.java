package com.titanhex.goldupgrades.item.registries.tool;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.item.ModItemGroup;
import com.titanhex.goldupgrades.item.ModItemTier;
import com.titanhex.goldupgrades.item.custom.tools.fire.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FireToolItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoldUpgrades.MOD_ID);
    
    // BASE
    public static final RegistryObject<Item> FIRE_BASE_GOLD_SWORD = ITEMS.register("fire_base_gold_sword",
            () -> new FireGoldSword(ModItemTier.FIRE_BASE_GOLD, 4, -2.4F,
                    1, 4,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_BASE_GOLD_SHOVEL = ITEMS.register("fire_base_gold_shovel",
            () -> new FireGoldShovel(ModItemTier.FIRE_BASE_GOLD, 2.5F, -3.0F,
                    3, 4,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_BASE_GOLD_PICKAXE = ITEMS.register("fire_base_gold_pickaxe",
            () -> new FireGoldPickaxe(ModItemTier.FIRE_BASE_GOLD, 2, -2.8F,
                    3, 4,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_BASE_GOLD_AXE = ITEMS.register("fire_base_gold_axe",
            () -> new FireGoldAxe(ModItemTier.FIRE_BASE_GOLD, 7.0F, -3.0F,
                    3, 4,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_BASE_GOLD_HOE = ITEMS.register("fire_base_gold_hoe",
            () -> new FireGoldHoe(ModItemTier.FIRE_BASE_GOLD, 1, -3.0F,
                    3, 4,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));

    //UPGRADED
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_SWORD = ITEMS.register("fire_upgraded_gold_sword",
            () -> new FireGoldSword(ModItemTier.FIRE_UPGRADED_GOLD, 5, -2.4F,
                    2, 3,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_SHOVEL = ITEMS.register("fire_upgraded_gold_shovel",
            () -> new FireGoldShovel(ModItemTier.FIRE_UPGRADED_GOLD, 3.0F, -3.0F,
                    6, 3,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_PICKAXE = ITEMS.register("fire_upgraded_gold_pickaxe",
            () -> new FireGoldPickaxe(ModItemTier.FIRE_UPGRADED_GOLD, 2, -2.8F,
                    6, 3,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_AXE = ITEMS.register("fire_upgraded_gold_axe",
            () -> new FireGoldAxe(ModItemTier.FIRE_UPGRADED_GOLD, 8.0F, -3.0F,
                    6, 3,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_UPGRADED_GOLD_HOE = ITEMS.register("fire_upgraded_gold_hoe",
            () -> new FireGoldHoe(ModItemTier.FIRE_UPGRADED_GOLD, 1, -3.0F, 6, 3,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));

    // POWER
    public static final RegistryObject<Item> FIRE_POWER_GOLD_SWORD = ITEMS.register("fire_power_gold_sword",
            () -> new FireGoldSword(ModItemTier.FIRE_POWER_GOLD, 6, -2.4F,
                    4, 2,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_POWER_GOLD_SHOVEL = ITEMS.register("fire_power_gold_shovel",
            () -> new FireGoldShovel(ModItemTier.FIRE_POWER_GOLD, 3.5F, -3.0F,
                    9, 2,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_POWER_GOLD_PICKAXE = ITEMS.register("fire_power_gold_pickaxe",
            () -> new FireGoldPickaxe(ModItemTier.FIRE_POWER_GOLD, 3, -2.8F,
                    9, 2,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_POWER_GOLD_AXE = ITEMS.register("fire_power_gold_axe",
            () -> new FireGoldAxe(ModItemTier.FIRE_POWER_GOLD, 9.0F, -3.0F,
                    9, 2,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> FIRE_POWER_GOLD_HOE = ITEMS.register("fire_power_gold_hoe",
            () -> new FireGoldHoe(ModItemTier.FIRE_POWER_GOLD, 1, -3.0F,
                    9, 2,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
}
