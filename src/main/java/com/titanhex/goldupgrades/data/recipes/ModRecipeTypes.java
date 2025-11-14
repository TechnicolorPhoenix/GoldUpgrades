package com.titanhex.goldupgrades.data.recipes;

import com.titanhex.goldupgrades.GoldUpgrades;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeTypes {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZER =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, GoldUpgrades.MOD_ID);

    // Renamed to ENVIRONMENTAL_SHAPED_SERIALIZER and points to the new class
    public static final RegistryObject<EnvironmentalShapedRecipe.Serializer> ENVIRONMENTAL_SHAPED_SERIALIZER =
            RECIPE_SERIALIZER.register("environmental_shaped", EnvironmentalShapedRecipe.Serializer::new);

    public static final RegistryObject<EnvironmentalShapelessRecipe.Serializer> ENVIRONMENTAL_SHAPELESS_SERIALIZER =
            RECIPE_SERIALIZER.register("environmental_shapeless", EnvironmentalShapelessRecipe.Serializer::new);

    public static void register(IEventBus eventBus){
        RECIPE_SERIALIZER.register(eventBus);
    }
}
