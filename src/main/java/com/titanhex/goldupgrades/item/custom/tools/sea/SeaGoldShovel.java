package com.titanhex.goldupgrades.item.custom.tools.sea;

import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.components.SeaToolComponent;
import com.titanhex.goldupgrades.item.components.TreasureToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWaterInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectShovel;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
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
import java.util.function.Consumer;

public class SeaGoldShovel extends EffectShovel implements IWaterInfluencedItem, IWeatherInfluencedItem, ILevelableItem
{

    private final SeaToolComponent seaToolHandler;
    private final TreasureToolComponent treasureHandler;

    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier of the pickaxe.
     *
     * @param tier                  The tier for the stats of the tool.
     * @param attackDamage         The base attack damage.
     * @param attackSpeed          The attack speed modifier.
     * @param effectAmplifications A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration       The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost       The number of durability points to subtract on each use.
     * @param properties           Item properties.
     */
    public SeaGoldShovel(IItemTier tier, float attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed, effectAmplifications, effectDuration, durabilityCost, properties);
        this.seaToolHandler = new SeaToolComponent(durabilityCost);
        this.treasureHandler = new TreasureToolComponent(ParticleTypes.SPLASH, SoundEvents.PLAYER_SPLASH);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int unknownInt, boolean unknownConditional) {
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
        seaToolHandler.appendHoverText(this, stack, tooltip);
        treasureHandler.appendHoverText(stack, tooltip, "§eDigging up sand at the beach yields treasure in the rain.");
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return super.damageItem(stack, seaToolHandler.damageItem(this, stack, amount), entity, onBroken);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);

        treasureHandler.tryDropTreasure(world, blockPos, blockState, miningEntity, usedStack,
                Biomes.BEACH.location(), (state) -> state.is(Blocks.SAND),
                (stack) -> isRaining(stack, world),
                new ResourceLocation("goldupgrades", "gameplay/treasure/beach_digging"),
                11
                );

        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        if (baseSpeed > 1.0F) {

            float speedMultiplier = 1.0F + seaToolHandler.getDestroySpeed(this, stack);

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

        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = context.getItemInHand();

        return seaToolHandler.useOn(this, context, (unused) -> {
            if (state.getBlock() == Blocks.SAND || state.getBlock() == Blocks.DIRT) {
                world.setBlock(pos, Blocks.SNOW.defaultBlockState(), 11);

                world.playSound(null, pos, SoundEvents.SNOW_BREAK, SoundCategory.BLOCKS, 1.0F, 1.5F);

                stack.hurtAndBreak(super.baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

                return ActionResultType.SUCCESS;

            }
            return ActionResultType.PASS;
        });
    }
}
