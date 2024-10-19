package net.titanhex.thex.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.titanhex.thex.TitanhexMod;
import net.titanhex.thex.block.custom.GlowdustBlock;
import net.titanhex.thex.item.ModItemGroup;
import net.titanhex.thex.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS
    = DeferredRegister.create(ForgeRegistries.BLOCKS, TitanhexMod.MOD_ID);

    public static final RegistryObject<Block> AMETHYST_ORE = registerBlock("amethyst_ore",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK).harvestLevel(2).harvestTool(ToolType.PICKAXE).hardnessAndResistance(5f)));

    public static final RegistryObject<Block> GLOWDUST_BLOCK = registerBlock("glowdust_block",
            () -> new GlowdustBlock(AbstractBlock.Properties.create(Material.GLASS).harvestLevel(1).harvestTool(ToolType.PICKAXE).hardnessAndResistance(2f)));

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItems(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItems(String name, RegistryObject<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().group(ModItemGroup.MAIN_GROUP))
        );
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
