package com.titanhex.goldupgrades.item.registries.tool;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.item.ModItemGroup;
import com.titanhex.goldupgrades.item.ModItemTier;
import com.titanhex.goldupgrades.item.custom.tools.obsidian.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ObsidianToolItems {
    
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoldUpgrades.MOD_ID);
    
    // BASE
    public static final RegistryObject<Item> OBSIDIAN_BASE_GOLD_SWORD = ITEMS.register("obsidian_base_gold_sword",
            () -> new ObsidianGoldSword(ModItemTier.OBSIDIAN_BASE_GOLD, 4, -2.4F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_BASE_GOLD_SHOVEL = ITEMS.register("obsidian_base_gold_shovel",
            () -> new ObsidianGoldShovel(ModItemTier.OBSIDIAN_BASE_GOLD, 2.5F, -3.0F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_BASE_GOLD_PICKAXE = ITEMS.register("obsidian_base_gold_pickaxe",
            () -> new ObsidianGoldPickaxe(ModItemTier.OBSIDIAN_BASE_GOLD, 2, -2.8F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_BASE_GOLD_AXE = ITEMS.register("obsidian_base_gold_axe",
            () -> new ObsidianGoldAxe(ModItemTier.OBSIDIAN_BASE_GOLD, 7.0F, -3.0F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_BASE_GOLD_HOE = ITEMS.register("obsidian_base_gold_hoe",
            () -> new ObsidianGoldHoe(ModItemTier.OBSIDIAN_BASE_GOLD, 1, -3.0F,
                    new Item.Properties().rarity(Rarity.UNCOMMON).tab(ModItemGroup.GOLD_BASE_ARMOR_GROUP)));

    //UPGRADED
    public static final RegistryObject<Item> OBSIDIAN_UPGRADED_GOLD_SWORD = ITEMS.register("obsidian_upgraded_gold_sword",
            () -> new ObsidianGoldSword(ModItemTier.OBSIDIAN_UPGRADED_GOLD, 5, -2.4F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_UPGRADED_GOLD_SHOVEL = ITEMS.register("obsidian_upgraded_gold_shovel",
            () -> new ObsidianGoldShovel(ModItemTier.OBSIDIAN_UPGRADED_GOLD, 3.0F, -3.0F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_UPGRADED_GOLD_PICKAXE = ITEMS.register("obsidian_upgraded_gold_pickaxe",
            () -> new ObsidianGoldPickaxe(ModItemTier.OBSIDIAN_UPGRADED_GOLD, 2, -2.8F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_UPGRADED_GOLD_AXE = ITEMS.register("obsidian_upgraded_gold_axe",
            () -> new ObsidianGoldAxe(ModItemTier.OBSIDIAN_UPGRADED_GOLD, 8.0F, -3.0F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_UPGRADED_GOLD_HOE = ITEMS.register("obsidian_upgraded_gold_hoe",
            () -> new ObsidianGoldHoe(ModItemTier.OBSIDIAN_UPGRADED_GOLD, 1, -3.0F,
                    new Item.Properties().rarity(Rarity.RARE).tab(ModItemGroup.GOLD_UPGRADED_ARMOR_GROUP)));
    
    // POWER
    public static final RegistryObject<Item> OBSIDIAN_POWER_GOLD_SWORD = ITEMS.register("obsidian_power_gold_sword",
            () -> new ObsidianGoldSword(ModItemTier.OBSIDIAN_POWER_GOLD, 6, -2.4F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_POWER_GOLD_SHOVEL = ITEMS.register("obsidian_power_gold_shovel",
            () -> new ObsidianGoldShovel(ModItemTier.OBSIDIAN_POWER_GOLD, 3.5F, -3.0F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_POWER_GOLD_PICKAXE = ITEMS.register("obsidian_power_gold_pickaxe",
            () -> new ObsidianGoldPickaxe(ModItemTier.OBSIDIAN_POWER_GOLD, 3, -2.8F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_POWER_GOLD_AXE = ITEMS.register("obsidian_power_gold_axe",
            () -> new ObsidianGoldAxe(ModItemTier.OBSIDIAN_POWER_GOLD, 9.0F, -3.0F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
    public static final RegistryObject<Item> OBSIDIAN_POWER_GOLD_HOE = ITEMS.register("obsidian_power_gold_hoe",
            () -> new ObsidianGoldHoe(ModItemTier.OBSIDIAN_POWER_GOLD, 1, -3.0F,
                    new Item.Properties().rarity(Rarity.EPIC).tab(ModItemGroup.GOLD_POWER_ARMOR_GROUP)));
}
