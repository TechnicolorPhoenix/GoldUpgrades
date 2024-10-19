package com.idtech.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ItemMod {

    //BASIC ITEMS
    public static final Item STRUCTURE_GEL = ItemUtils.buildBasicItem("structuregel", CreativeModeTab.TAB_MISC);

    // ORE
    public static final Item POWER_ORE = ItemUtils.buildBasicItem("poweroreitem", CreativeModeTab.TAB_MISC);
    public static final Item WIND_ORE = ItemUtils.buildBasicItem("windoreitem", CreativeModeTab.TAB_MISC);
    public static final Item WATER_ORE = ItemUtils.buildBasicItem("wateroreitem", CreativeModeTab.TAB_MISC);

    //ADVANCED ITEMS
    public static final Item STAFF = ItemUtils.buildBasicItem("staff", CreativeModeTab.TAB_TOOLS);
    public static final Item DUST = ItemUtils.buildBasicItem("dust", CreativeModeTab.TAB_MISC);

    // BOWS
    public static final Item HEX_BOW = ItemUtils.buildHexbowItem("hexbow", CreativeModeTab.TAB_COMBAT);

    //FOODS


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {

        //BASIC ITEMS
        event.getRegistry().register(STRUCTURE_GEL);

        // ORE
        event.getRegistry().register(POWER_ORE);
        event.getRegistry().register(WIND_ORE);
        event.getRegistry().register(WATER_ORE);

        // ITEMS
        event.getRegistry().register(STAFF);
        event.getRegistry().register(DUST);

        // TOOLS
        event.getRegistry().register(HEX_BOW);
        ItemUtils.registerCrossbow(HEX_BOW);
        event.getRegistry().register(SpawnItem.SPAWN_ITEM);

        // FOOD

        // ARMOR

        //PROJECTILES
    }
}
