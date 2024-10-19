package com.idtech.block;

import net.minecraft.world.item.CreativeModeTab;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BlockMod {

    //Basic Block
    public static final Block CASTLE_WALL = BlockUtils.createBasicBlock("castlewall", Material.STONE);
    public static final Item CASTLE_WALL_ITEM = BlockUtils.createBlockItem(CASTLE_WALL, CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static final Item ORE_ITEM = BlockUtils.createBlockItem(CustomBlock.INSTANCE, CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static final Block POWER_ORE =  BlockUtils.createBasicBlock("powerore", Material.STONE, 3);
    public static final Item POWER_ORE_ITEM = BlockUtils.createBlockItem(POWER_ORE, CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Block POWER_BLOCK =  BlockUtils.createBasicBlock("powerblock", Material.STONE, 5);
    public static final Item POWER_BLOCK_ITEM = BlockUtils.createBlockItem(POWER_BLOCK, CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static final Block WATER_ORE =  BlockUtils.createBasicBlock("waterore", Material.CLAY, 3);
    public static final Item WATER_ORE_ITEM = BlockUtils.createBlockItem(WATER_ORE, CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Block WATER_BLOCK =  BlockUtils.createBasicBlock("waterblock", Material.AMETHYST, 5);
    public static final Item WATER_BLOCK_ITEM = BlockUtils.createBlockItem(WATER_BLOCK, CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static final Block WIND_ORE =  BlockUtils.createBasicBlock("windore", Material.DIRT, 3);
    public static final Item WIND_ORE_ITEM = BlockUtils.createBlockItem(WIND_ORE, CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Block WIND_BLOCK =  BlockUtils.createBasicBlock("windblock", Material.STONE, 5);
    public static final Item WIND_BLOCK_ITEM = BlockUtils.createBlockItem(WIND_BLOCK, CreativeModeTab.TAB_BUILDING_BLOCKS);
    //Basic Block


    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {

        event.getRegistry().register(CASTLE_WALL_ITEM);
        event.getRegistry().register(POWER_ORE_ITEM);
        event.getRegistry().register(WATER_ORE_ITEM);
        event.getRegistry().register(WIND_ORE_ITEM);
        event.getRegistry().register(POWER_BLOCK_ITEM);
        event.getRegistry().register(WATER_BLOCK_ITEM);
        event.getRegistry().register(WIND_BLOCK_ITEM);
        event.getRegistry().register(ORE_ITEM);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {

        event.getRegistry().register(CASTLE_WALL);
        event.getRegistry().register(POWER_ORE);
        event.getRegistry().register(WATER_ORE);
        event.getRegistry().register(WIND_ORE);
        event.getRegistry().register(POWER_BLOCK);
        event.getRegistry().register(WATER_BLOCK);
        event.getRegistry().register(WIND_BLOCK);
        event.getRegistry().register(CustomBlock.INSTANCE);
    }
}





