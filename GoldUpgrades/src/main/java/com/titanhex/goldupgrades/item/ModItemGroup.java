package com.titanhex.goldupgrades.item;

import com.titanhex.goldupgrades.item.armor.FireArmorItems;
import com.titanhex.goldupgrades.item.armor.ObsidianArmorItems;
import com.titanhex.goldupgrades.item.armor.RaidArmorItems;
import com.titanhex.goldupgrades.item.armor.SeaArmorItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup {

    public static final ItemGroup RAID_ARMOR_GROUP = new ItemGroup("raidArmorTab")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(RaidArmorItems.RAID_BASE_ARMOR_CHESTPLATE.get());
        }
    };
    public static final ItemGroup GOLD_BASE_ARMOR_GROUP = new ItemGroup("goldBaseArmorTab")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(ObsidianArmorItems.OBSIDIAN_BASE_GOLD_BOOTS.get());
        }
    };
    public static final ItemGroup GOLD_UPGRADED_ARMOR_GROUP = new ItemGroup("goldUpgradedArmorTab")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(FireArmorItems.FIRE_UPGRADED_GOLD_HELMET.get());
        }
    };
    public static final ItemGroup GOLD_POWER_ARMOR_GROUP = new ItemGroup("goldPowerArmorTab")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(SeaArmorItems.SEA_POWER_GOLD_CHESTPLATE.get());
        }
    };
}
