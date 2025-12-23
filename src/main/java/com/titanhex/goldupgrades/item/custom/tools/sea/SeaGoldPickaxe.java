package com.titanhex.goldupgrades.item.custom.tools.sea;

import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.components.SeaToolComponent;
import com.titanhex.goldupgrades.item.components.TreasureToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWaterInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectPickaxe;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class SeaGoldPickaxe extends EffectPickaxe implements IWaterInfluencedItem, IWeatherInfluencedItem, ILevelableItem
{

    private static final Random RANDOM = new Random();
    private final SeaToolComponent seaToolHandler;
    private final TreasureToolComponent treasureHandler;

    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier.
     *
     * @param tier                  The tier enum for the armor type.
     * @param attackDamage         The base attack damage.
     * @param attackSpeed          The attack speed modifier.
     * @param effectAmplifications A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration       The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost       The number of durability points to subtract on each use.
     * @param properties           Item properties.
     */
    public SeaGoldPickaxe(IItemTier tier, int attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed, effectAmplifications, effectDuration, durabilityCost, properties);
        this.seaToolHandler = new SeaToolComponent(durabilityCost);
        this.treasureHandler = new TreasureToolComponent(ParticleTypes.SPLASH, SoundEvents.PLAYER_SPLASH);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int itemSlot, boolean isSelected) {
        if (world.isClientSide) return;

        boolean currentSubmerged = holdingEntity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
        boolean isInRainOrWaterNow = holdingEntity.isInWaterOrRain();
        boolean currentInRain = isInRainOrWaterNow && !currentSubmerged;
        Weather currentWeather = Weather.getCurrentWeather(world);

        boolean oldSubmerged = this.getIsSubmerged(stack);
        boolean oldInRain = this.getIsInRain(stack);
        Weather oldWeather = this.getWeather(stack);

        boolean environmentalStateChanged = currentInRain != oldInRain || currentSubmerged != oldSubmerged || oldWeather != currentWeather;

        if (environmentalStateChanged) {
            setIsInRain(stack, currentInRain);
            setIsSubmerged(stack, currentSubmerged);
            setWeather(stack, currentWeather);

            if (currentInRain || currentSubmerged) {
                world.playSound(null, holdingEntity.blockPosition(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        seaToolHandler.appendHoverText(stack, tooltip);
        treasureHandler.appendHoverText(stack, tooltip, "§eMine jungle stones for treasure when raining.");
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return super.damageItem(stack, seaToolHandler.damageItem(stack, amount), entity, onBroken);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        if (baseSpeed > 1.0F) {
            float speedMultiplier = 1.0F + seaToolHandler.getDestroySpeed(stack);

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(@NotNull World world, PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ActionResult<ItemStack> result = super.use(world, player, hand);
        return seaToolHandler.use(world, player, stack, result);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);

        treasureHandler.tryDropTreasure(world, blockPos, blockState, miningEntity, usedStack,
                () -> blockState.is(BlockTags.BASE_STONE_OVERWORLD),
                new ResourceLocation("goldupgrades", "gameplay/treasure/jungle_mining"),
                16);
        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    /**
     * Handles the item use event (Right Click) with custom logic for water/ice
     * conversion, falling back to the parent class's aura application.
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();

        if (world.isClientSide || player == null)
            return super.useOn(context);

        return seaToolHandler.useOn(this, context, null);
    }
}
