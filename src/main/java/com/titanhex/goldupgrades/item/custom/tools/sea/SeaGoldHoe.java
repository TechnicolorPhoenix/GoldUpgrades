package com.titanhex.goldupgrades.item.custom.tools.sea;

import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.components.SeaToolComponent;
import com.titanhex.goldupgrades.item.components.TreasureToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.IElementalHoe;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWaterInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectHoe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class SeaGoldHoe extends EffectHoe implements IWeatherInfluencedItem, IWaterInfluencedItem, ILevelableItem, IElementalHoe
{

    private static final Random RANDOM = new Random();

    SeaToolComponent seaToolHandler;
    TreasureToolComponent treasureHandler;
    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier.
     *
     * @param tier                  The tier enum for the item type.
     * @param attackDamage         The base attack damage.
     * @param attackSpeed          The attack speed modifier.
     * @param effectAmplifications A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration       The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost       The number of durability points to subtract on each use.
     * @param properties           Item properties.
     */
    public SeaGoldHoe(IItemTier tier, int attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed, effectAmplifications, effectDuration, durabilityCost, properties);
        this.treasureHandler = new TreasureToolComponent(ParticleTypes.SPLASH, SoundEvents.PLAYER_SPLASH);
        this.seaToolHandler = new SeaToolComponent(durabilityCost);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, holdingEntity, itemSlot, isSelected);
        if (world.isClientSide) return;

        boolean submergedChanged = changeSubmerged(stack, holdingEntity);
        boolean isInRainChanged = changeIsInRain(stack, holdingEntity);
        boolean weatherChanged = changeWeather(stack, world);

        if (isInRainChanged || submergedChanged) {
            world.playSound(null, holdingEntity.blockPosition(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        if (!(holdingEntity instanceof LivingEntity))
            return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;

        int elementalHoeLevel = IElementalHoe.getElementalHoeEnchantmentLevel(stack);
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.OFFHAND) == stack;

        if (isEquipped && elementalHoeLevel > 0) {
            holdingElementalHoe(stack, livingEntity,
                    (getIsSubmerged(holdingEntity) ? Effects.DOLPHINS_GRACE : Effects.MOVEMENT_SPEED),
                    () -> isRaining(stack)
            );
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        int elementalHoeEnchantmentLevel = IElementalHoe.getElementalHoeEnchantmentLevel(stack);
        int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);

        boolean inRain = this.getIsInRain(stack);
        boolean submerged = this.getIsSubmerged(stack);
        boolean weatherIsRain = isRaining(stack, worldIn);

        IElementalHoe.appendHoverText(stack, tooltip, "§eHold for Dolphin's Grace, use for Water Breathing");

        treasureHandler.appendHoverText(stack, tooltip, "§Cut kelp in the Ocean for treasure while raining.");
        seaToolHandler.appendHoverText(stack, tooltip);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
        if (!isRaining(usedStack)) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);

        treasureHandler.tryDropTreasure(world, blockPos, blockState, miningEntity, usedStack,
                () -> blockState.is(Blocks.SEAGRASS),
                new ResourceLocation("goldupgrades", "gameplay/treasure/ocean_cutting"),
                2);

        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return super.damageItem(stack, seaToolHandler.damageItem(stack, amount), entity, onBroken);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        boolean weatherIsRain = isRaining(stack);
        int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);
        float bonusSpeed = seaToolHandler.getDestroySpeed(stack);

        if (baseSpeed > 1.0F) {

            float speedMultiplier = 1.0F + bonusSpeed;

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(@NotNull World world, PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ActionResult<ItemStack> result = super.use(world, player, hand);

        if (hand == Hand.OFF_HAND)
            result = IElementalHoe.use(stack, player, Effects.WATER_BREATHING, 30*20);

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
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        return seaToolHandler.useOn(this, context, (tool) -> {
            if (state.getBlock() instanceof IGrowable && world instanceof ServerWorld) {
                IGrowable growable = (IGrowable) state.getBlock();

                if (growable.isValidBonemealTarget(world, pos, state, false)) {
                    growable.performBonemeal((ServerWorld) world, world.getRandom(), pos, state);

                    world.playSound(null, pos, SoundEvents.HOE_TILL, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    if (player != null) {
                        stack.hurtAndBreak(super.baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                        player.giveExperiencePoints(1);
                    }

                    return ActionResultType.CONSUME;
                }
            }

            if (state.getBlock() == Blocks.FARMLAND) {
                if (state.hasProperty(BlockStateProperties.MOISTURE)) {
                    int currentMoisture = state.getValue(BlockStateProperties.MOISTURE);

                    if (currentMoisture < 7) {
                        world.setBlock(pos, state.setValue(BlockStateProperties.MOISTURE, 7), 2);

                        world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                        if (player != null) {
                            stack.hurtAndBreak(super.baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                            player.giveExperiencePoints(1);
                        }

                        return ActionResultType.CONSUME;
                    }
                }
            }
            return ActionResultType.PASS;
        });
    }
}
