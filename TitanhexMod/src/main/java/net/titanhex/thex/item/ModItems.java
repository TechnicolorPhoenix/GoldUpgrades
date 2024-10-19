package net.titanhex.thex.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.titanhex.thex.TitanhexMod;
import net.titanhex.thex.item.custom.SeaGrazer;
import net.titanhex.thex.item.custom.TitanhexShieldItem;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TitanhexMod.MOD_ID);

    public static final RegistryObject<Item> AMETHYST = ITEMS.register("amethyst",
            () -> new Item(new Item.Properties().maxStackSize(16).group(ModItemGroup.MAIN_GROUP)));

    public static final RegistryObject<Item> SEA_GRAZER = ITEMS.register("sea_grazer",
            () -> new SeaGrazer(new Item.Properties().maxStackSize(4).maxDamage(6).group(ModItemGroup.MAIN_GROUP)));

    public static final RegistryObject<Item> HEX_SHIELD = ITEMS.register("hex_shield",
            () -> new TitanhexShieldItem(new Item.Properties().maxStackSize(1).group(ModItemGroup.MAIN_GROUP)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
