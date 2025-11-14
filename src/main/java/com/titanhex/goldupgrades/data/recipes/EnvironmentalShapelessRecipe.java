package com.titanhex.goldupgrades.data.recipes;

import com.google.gson.JsonObject;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.MoonPhase;
import com.titanhex.goldupgrades.data.Weather;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * A custom shapeless crafting recipe that requires specific environmental conditions
 * (Weather, Moon Phase, and Dimension Type) to be met for crafting.
 */
public class EnvironmentalShapelessRecipe extends ShapelessRecipe {
    // Custom fields to hold the required environmental conditions
    private final Weather requiredWeather;
    private final MoonPhase requiredMoonPhase;
    private final DimensionType requiredDimensionType;
    private final long minTime; // NEW FIELD
    private final long maxTime; // NEW FIELD

    public EnvironmentalShapelessRecipe(
            ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn,
            NonNullList<Ingredient> recipeItemsIn, Weather requiredWeather,
            MoonPhase requiredMoonPhase, DimensionType requiredDimensionType,
            long minTime, long maxTime) { // UPDATED CONSTRUCTOR

        super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
        this.requiredWeather = requiredWeather;
        this.requiredMoonPhase = requiredMoonPhase;
        this.requiredDimensionType = requiredDimensionType;
        this.minTime = minTime; // INITIALIZE NEW FIELD
        this.maxTime = maxTime; // INITIALIZE NEW FIELD
    }

    /**
     * The core matching logic, including the shapeless check (from super) and the custom environmental checks.
     */
    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        // 1. Check vanilla shapeless match logic first
        if (!super.matches(inv, worldIn)) {
            return false;
        }

        // 2. Custom environmental checks
        // Check 1: Weather
        Weather currentWeather = Weather.getCurrentWeather(worldIn);
        boolean weatherMatch = this.requiredWeather == Weather.ANY || this.requiredWeather == currentWeather;

        // Check 2: Moon Phase
        MoonPhase currentMoonPhase = MoonPhase.getCurrentMoonPhase(worldIn);
        boolean moonPhaseMatch = this.requiredMoonPhase == MoonPhase.ANY || this.requiredMoonPhase == currentMoonPhase;

        // Check 3: Dimension
        DimensionType currentDimension = DimensionType.getCurrentDimension(worldIn);
        boolean dimensionMatch = this.requiredDimensionType == DimensionType.ANY || this.requiredDimensionType == currentDimension;

        // Check Time
        // Get time of day (0 to 23999)
        long currentTime = worldIn.getDayTime() % 24000L;
        boolean timeMatches = false;

        if (this.minTime > this.maxTime) {
            // This handles time ranges that wrap around midnight (e.g., 22000 to 2000)
            timeMatches = (currentTime >= this.minTime || this.minTime == Long.MIN_VALUE) ||
                    (currentTime <= this.maxTime || this.maxTime == Long.MAX_VALUE);
        } else {
            // Standard time range check
            timeMatches = (currentTime >= this.minTime || this.minTime == Long.MIN_VALUE) &&
                    (currentTime <= this.maxTime || this.maxTime == Long.MAX_VALUE);
        }

        // All checks must pass
        return weatherMatch && moonPhaseMatch && dimensionMatch && timeMatches;
    }

    /**
     * Defines the Serializer for this recipe type.
     */
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ENVIRONMENTAL_SHAPELESS_SERIALIZER.get();
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EnvironmentalShapelessRecipe> {

        // Use the vanilla serializer to handle the non-custom parts of the recipe
        private final ShapelessRecipe.Serializer vanillaSerializer = new ShapelessRecipe.Serializer();

        /**
         * Reads the recipe data from a JSON file.
         */
        @Override
        public EnvironmentalShapelessRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // 1. Read all vanilla fields using the vanilla serializer
            ShapelessRecipe vanillaRecipe = vanillaSerializer.fromJson(recipeId, json);

            // 2. Read custom fields from JSON
            // Read optional 'weather' field, default to ANY if missing
            String weatherStr = JSONUtils.getAsString(json, "weather", Weather.ANY.toString());
            Weather weather = Weather.getWeatherByString(weatherStr);

            // Read optional 'moon_phase' field, default to ANY if missing
            String moonPhaseStr = JSONUtils.getAsString(json, "moon_phase", MoonPhase.ANY.toString());
            MoonPhase moonPhase = MoonPhase.getMoonPhaseByString(moonPhaseStr);

            // Read optional 'dimension' field, default to ANY if missing
            String dimensionStr = JSONUtils.getAsString(json, "dimension", DimensionType.ANY.toString());
            DimensionType dimension = DimensionType.getDimensionTypeByString(dimensionStr);

            // Read Time (NEW JSON READ)
            // Long.MIN_VALUE/MAX_VALUE indicates no constraint
            long minTime = json.has("min_time") ? JSONUtils.getAsLong(json, "min_time") : Long.MIN_VALUE;
            long maxTime = json.has("max_time") ? JSONUtils.getAsLong(json, "max_time") : Long.MAX_VALUE;

            // 3. Reconstruct as the custom recipe
            return new EnvironmentalShapelessRecipe(
                    vanillaRecipe.getId(),
                    vanillaRecipe.getGroup(),
                    vanillaRecipe.getResultItem(),
                    vanillaRecipe.getIngredients(),
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
        public void toNetwork(PacketBuffer buffer, EnvironmentalShapelessRecipe recipe) {
            // 1. Write all vanilla fields to the buffer
            this.vanillaSerializer.toNetwork(buffer, recipe);

            // 2. Write your custom fields to the buffer
            buffer.writeEnum(recipe.requiredWeather);
            buffer.writeEnum(recipe.requiredMoonPhase);
            buffer.writeEnum(recipe.requiredDimensionType);
            buffer.writeLong(recipe.minTime); // NEW WRITE TO BUFFER
            buffer.writeLong(recipe.maxTime); // NEW WRITE TO BUFFER
        }

        /**
         * Reads the recipe data from the network buffer (PacketBuffer).
         */
        @Override
        public EnvironmentalShapelessRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            // 1. Read vanilla fields
            ShapelessRecipe vanillaRecipe = vanillaSerializer.fromNetwork(recipeId, buffer);

            // 2. Read custom fields
            Weather weather = buffer.readEnum(Weather.class);
            MoonPhase moonPhase = buffer.readEnum(MoonPhase.class);
            DimensionType dimension = buffer.readEnum(DimensionType.class);
            long minTime = buffer.readLong(); // NEW READ FROM BUFFER
            long maxTime = buffer.readLong(); // NEW READ FROM BUFFER

            // 3. Reconstruct as the custom recipe
            return new EnvironmentalShapelessRecipe(
                    vanillaRecipe.getId(),
                    vanillaRecipe.getGroup(),
                    vanillaRecipe.getResultItem(),
                    vanillaRecipe.getIngredients(),
                    weather,
                    moonPhase,
                    dimension,
                    minTime, // PASS NEW FIELD
                    maxTime // PASS NEW FIELD
            );
        }
    }
}
