package com.titanhex.goldupgrades.data.recipes;

import com.google.gson.JsonObject;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.MoonPhase;
import com.titanhex.goldupgrades.data.Weather;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class EnvironmentalShapedRecipe extends ShapedRecipe {

    // Custom fields to hold the required environmental conditions
    private final Weather requiredWeather;
    private final MoonPhase requiredMoonPhase;
    private final DimensionType requiredDimensionType;
    private final long minTime; // NEW FIELD
    private final long maxTime; // NEW FIELD

    public EnvironmentalShapedRecipe(
            ResourceLocation id, String group, int recipeWidth, int recipeHeight,
            net.minecraft.util.NonNullList<net.minecraft.item.crafting.Ingredient> ingredients,
            ItemStack recipeOutput, Weather requiredWeather, MoonPhase requiredMoonPhase,
            DimensionType requiredDimensionType, long minTime, long maxTime) { // UPDATED CONSTRUCTOR

        super(id, group, recipeWidth, recipeHeight, ingredients, recipeOutput);
        this.requiredWeather = requiredWeather;
        this.requiredMoonPhase = requiredMoonPhase;
        this.requiredDimensionType = requiredDimensionType;
        this.minTime = minTime; // INITIALIZE NEW FIELD
        this.maxTime = maxTime; // INITIALIZE NEW FIELD
    }

    /**
     * The core matching logic, including the shape check (from super) and the custom environmental checks.
     */
    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        // 1. Check if the vanilla shaped recipe matches the items in the inventory
        if (!super.matches(inv, worldIn)) {
            return false;
        }

        // 2. Custom Environmental Checks

        // Check Weather
        Weather currentWeather = Weather.getCurrentWeather(worldIn);
        boolean weatherMatches = this.requiredWeather == Weather.ANY || this.requiredWeather == currentWeather;

        // Check Moon Phase
        MoonPhase currentMoonPhase = MoonPhase.getCurrentMoonPhase(worldIn);
        boolean moonPhaseMatches = this.requiredMoonPhase == MoonPhase.ANY || this.requiredMoonPhase == currentMoonPhase;

        // Check Dimension Type
        DimensionType currentDimension = DimensionType.getCurrentDimension(worldIn);
        boolean dimensionMatches = this.requiredDimensionType == DimensionType.ANY || this.requiredDimensionType == currentDimension;

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

        return weatherMatches && moonPhaseMatches && dimensionMatches && timeMatches;
    }

    // --- Serializer Class ---

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EnvironmentalShapedRecipe> {
        private static final ShapedRecipe.Serializer vanillaSerializer = new ShapedRecipe.Serializer();

        /**
         * Reads the recipe data from a JSON object (loading the recipe from a file).
         */
        @Override
        public EnvironmentalShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json) { // <-- CORRECT: fromJson
            // 1. Read vanilla fields
            ShapedRecipe vanillaRecipe = vanillaSerializer.fromJson(recipeId, json); // <-- CORRECT: fromJson

            // 2. Read custom fields from JSON
            String weatherName = JSONUtils.getAsString(json, "weather", Weather.ANY.name());
            Weather weather = Weather.getWeatherByString(weatherName);

            String moonPhaseName = JSONUtils.getAsString(json, "moon_phase", MoonPhase.ANY.name());
            MoonPhase moonPhase = MoonPhase.getMoonPhaseByString(moonPhaseName);

            String dimensionName = JSONUtils.getAsString(json, "dimension", DimensionType.ANY.name());
            DimensionType dimension = DimensionType.getDimensionTypeByString(dimensionName);

            // Read Time (NEW JSON READ)
            // Long.MIN_VALUE/MAX_VALUE indicates no constraint
            long minTime = json.has("min_time") ? JSONUtils.getAsLong(json, "min_time") : Long.MIN_VALUE;
            long maxTime = json.has("max_time") ? JSONUtils.getAsLong(json, "max_time") : Long.MAX_VALUE;

            // 3. Reconstruct as the custom recipe
            return new EnvironmentalShapedRecipe(
                    vanillaRecipe.getId(),
                    vanillaRecipe.getGroup(),
                    vanillaRecipe.getRecipeWidth(),
                    vanillaRecipe.getRecipeHeight(),
                    vanillaRecipe.getIngredients(),
                    vanillaRecipe.getResultItem(),
                    weather,
                    moonPhase,
                    dimension,
                    minTime, // PASS NEW FIELD
                    maxTime  // PASS NEW FIELD
            );
        }

        /**
         * Writes the recipe data to the network buffer (PacketBuffer).
         */
        @Override
        public void toNetwork(PacketBuffer buffer, EnvironmentalShapedRecipe recipe) { // <-- CORRECT: toNetwork
            // 1. Write vanilla fields
            vanillaSerializer.toNetwork(buffer, recipe); // <-- CORRECT: toNetwork

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
        public EnvironmentalShapedRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) { // <-- CORRECT: fromNetwork
            // 1. Read vanilla fields
            ShapedRecipe vanillaRecipe = vanillaSerializer.fromNetwork(recipeId, buffer); // <-- CORRECT: fromNetwork

            // 2. Read custom fields
            Weather weather = buffer.readEnum(Weather.class);
            MoonPhase moonPhase = buffer.readEnum(MoonPhase.class);
            DimensionType dimension = buffer.readEnum(DimensionType.class);
            long minTime = buffer.readLong();
            long maxTime = buffer.readLong();

            // 3. Reconstruct as the custom recipe
            return new EnvironmentalShapedRecipe(
                    vanillaRecipe.getId(),
                    vanillaRecipe.getGroup(),
                    vanillaRecipe.getWidth(), // <-- CORRECT: getWidth
                    vanillaRecipe.getHeight(), // <-- CORRECT: getHeight
                    vanillaRecipe.getIngredients(),
                    vanillaRecipe.getResultItem(), // <-- CORRECT: getResultItem
                    weather,
                    moonPhase,
                    dimension,
                    minTime,
                    maxTime
            );
        }
    }
}
