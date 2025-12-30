package com.titanhex.goldupgrades.item;

import com.titanhex.goldupgrades.item.registries.armor.FireArmorItems;
import com.titanhex.goldupgrades.item.registries.armor.ObsidianArmorItems;
import com.titanhex.goldupgrades.item.registries.armor.SeaArmorItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModItemGroup {

    public static final ItemGroup GOLD_BASE_ARMOR_GROUP = new ItemGroup("goldBaseArmorTab")
    {
        @NotNull
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(ObsidianArmorItems.OBSIDIAN_BASE_GOLD_BOOTS.get());
        }
    };
    public static final ItemGroup GOLD_UPGRADED_ARMOR_GROUP = new ItemGroup("goldUpgradedArmorTab")
    {
        @NotNull
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(FireArmorItems.FIRE_UPGRADED_GOLD_HELMET.get());
        }
    };
    public static final ItemGroup GOLD_POWER_ARMOR_GROUP = new ItemGroup("goldPowerArmorTab")
    {
        @NotNull
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(SeaArmorItems.SEA_POWER_GOLD_CHESTPLATE.get());
        }
    };
}
