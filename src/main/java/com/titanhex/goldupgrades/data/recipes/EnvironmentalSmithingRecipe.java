package com.titanhex.goldupgrades.data.recipes;

import com.google.gson.JsonObject;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.MoonPhase;
import com.titanhex.goldupgrades.data.Weather;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

/**
 * A custom smithing crafting recipe that requires specific environmental conditions
 * (Weather, Moon Phase, and Dimension Type) to be met for crafting.
 */
public class EnvironmentalSmithingRecipe extends SmithingRecipe {
    private final Weather requiredWeather;
    private final MoonPhase requiredMoonPhase;
    private final DimensionType requiredDimensionType;
    private final long minTime;
    private final long maxTime;

    public EnvironmentalSmithingRecipe(ResourceLocation idIn, Ingredient firstRecipeItemIn, Ingredient secondRecipeItemIn, ItemStack recipeOutputIn,
                                       Weather requiredWeather,
                                       MoonPhase requiredMoonPhase, DimensionType requiredDimensionType,
                                       long minTime, long maxTime) {
        super(idIn, firstRecipeItemIn, secondRecipeItemIn, recipeOutputIn);
        this.requiredWeather = requiredWeather;
        this.requiredMoonPhase = requiredMoonPhase;
        this.requiredDimensionType = requiredDimensionType;
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    /**
     * The core matching logic, including the smithing check (from super) and the custom environmental checks.
     */
    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (!super.matches(inv, worldIn)) {
            return false;
        }

        Weather currentWeather = Weather.getCurrentWeather(worldIn);
        boolean weatherMatch = this.requiredWeather == Weather.ANY || this.requiredWeather == currentWeather;

        MoonPhase currentMoonPhase = MoonPhase.getCurrentMoonPhase(worldIn);
        boolean moonPhaseMatch = this.requiredMoonPhase == MoonPhase.ANY || this.requiredMoonPhase == currentMoonPhase;

        DimensionType currentDimension = DimensionType.getCurrentDimension(worldIn);
        boolean dimensionMatch = this.requiredDimensionType == DimensionType.ANY || this.requiredDimensionType == currentDimension;

        long currentTime = worldIn.getDayTime() % 24000L;
        boolean timeMatches;

        if (this.minTime > this.maxTime) {
            timeMatches = (currentTime >= this.minTime || this.minTime == Long.MIN_VALUE) ||
                    (currentTime <= this.maxTime || this.maxTime == Long.MAX_VALUE);
        } else {
            timeMatches = (currentTime >= this.minTime || this.minTime == Long.MIN_VALUE) &&
                    (currentTime <= this.maxTime || this.maxTime == Long.MAX_VALUE);
        }

        return weatherMatch && moonPhaseMatch && dimensionMatch && timeMatches;
    }

    /**
     * Defines the Serializer for this recipe type.
     */
    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ENVIRONMENTAL_SMITHING_SERIALIZER.get();
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EnvironmentalSmithingRecipe> {

        // Use the vanilla serializer to handle the non-custom parts of the recipe
        private final SmithingRecipe.Serializer vanillaSerializer = new SmithingRecipe.Serializer();

        /**
         * Reads the recipe data from a JSON file.
         */
        @Override
        public EnvironmentalSmithingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {            // 1. Read all vanilla fields using the vanilla serializer
            SmithingRecipe vanillaRecipe = vanillaSerializer.fromJson(recipeId, json);

            Ingredient baseIngredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "base"));
            Ingredient additionIngredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "addition"));

            String weatherStr = JSONUtils.getAsString(json, "weather", Weather.ANY.toString());
            Weather weather = Weather.getWeatherByString(weatherStr);

            String moonPhaseStr = JSONUtils.getAsString(json, "moon_phase", MoonPhase.ANY.toString());
            MoonPhase moonPhase = MoonPhase.getMoonPhaseByString(moonPhaseStr);

            String dimensionStr = JSONUtils.getAsString(json, "dimension", DimensionType.ANY.toString());
            DimensionType dimension = DimensionType.getDimensionTypeByString(dimensionStr);

            long minTime = json.has("min_time") ? JSONUtils.getAsLong(json, "min_time") : Long.MIN_VALUE;
            long maxTime = json.has("max_time") ? JSONUtils.getAsLong(json, "max_time") : Long.MAX_VALUE;

            return new EnvironmentalSmithingRecipe(
                    vanillaRecipe.getId(),
                    baseIngredient,
                    additionIngredient,
                    vanillaRecipe.getResultItem(),
                    weather,
                    moonPhase,
                    dimension,
                    minTime,
                    maxTime
            );
        }

        /**
         * Writes the recipe data to the network buffer (PacketBuffer).
         */
        @Override
        public void toNetwork(PacketBuffer buffer, EnvironmentalSmithingRecipe recipe) {
            this.vanillaSerializer.toNetwork(buffer, recipe);

            buffer.writeEnum(recipe.requiredWeather);
            buffer.writeEnum(recipe.requiredMoonPhase);
            buffer.writeEnum(recipe.requiredDimensionType);
            buffer.writeLong(recipe.minTime);
            buffer.writeLong(recipe.maxTime);
        }


        /**
         * Reads the recipe data from the network buffer (PacketBuffer).
         */
        @Override
        public EnvironmentalSmithingRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            SmithingRecipe vanillaRecipe = vanillaSerializer.fromNetwork(recipeId, buffer);

            Ingredient baseIngredient = Ingredient.fromNetwork(buffer);
            Ingredient additionIngredient = Ingredient.fromNetwork(buffer);

            Weather weather = buffer.readEnum(Weather.class);
            MoonPhase moonPhase = buffer.readEnum(MoonPhase.class);
            DimensionType dimension = buffer.readEnum(DimensionType.class);
            long minTime = buffer.readLong();
            long maxTime = buffer.readLong();

            return new EnvironmentalSmithingRecipe(
                    vanillaRecipe.getId(),
                    baseIngredient,
                    additionIngredient,
                    vanillaRecipe.getResultItem(),
                    weather,
                    moonPhase,
                    dimension,
                    minTime,
                    maxTime
            );
        }
    }
}
