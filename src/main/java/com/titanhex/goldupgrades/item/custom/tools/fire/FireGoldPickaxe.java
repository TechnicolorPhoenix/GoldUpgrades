package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.components.TreasureToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FireGoldPickaxe extends PickaxeItem implements ILevelableItem, IIgnitableTool, IDimensionInfluencedItem, IDayInfluencedItem, IWeatherInfluencedItem, ILightInfluencedItem
{
    int burnTicks;
    int durabilityUse;
    TreasureToolComponent treasureHandler;

    public FireGoldPickaxe(IItemTier tier, int atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
        treasureHandler = new TreasureToolComponent();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, Entity holdingEntity, int uInt, boolean uBoolean) {
        int currentBrightness = getLightLevel(stack, world, holdingEntity.blockPosition());

        int oldBrightness = ILightInfluencedItem.getLightLevel(stack);

        if (oldBrightness != currentBrightness) {
            ILightInfluencedItem.setLightLevel(stack, currentBrightness);
        }

        if (world.isClientSide)
            return;

        DimensionType oldDimension = getDimension(stack);
        Weather oldWeather = getWeather(stack);
        boolean oldIsDay = isDay(stack);

        DimensionType currentDimension = DimensionType.getCurrentDimension(world);
        Weather currentWeather = Weather.getCurrentWeather(world);
        boolean currentIsDay = isDay(stack, world);

        if (oldWeather != currentWeather || oldDimension != currentDimension || currentIsDay != oldIsDay) {
            setWeather(stack, currentWeather);
            setDimension(stack, currentDimension);
            setIsDay(stack, currentIsDay);
        }

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
        treasureHandler.tryDropTreasure(
                world, blockPos, blockState, miningEntity, usedStack,
                Biomes.BADLANDS.location(),
                (state) -> state.is(BlockTags.BASE_STONE_OVERWORLD),
                this::isClear,
                new ResourceLocation("goldupgrades", "gameplay/treasure/badlands_mining"),
                13
        );
        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    private float calculateBonusDestroySpeed(ItemStack stack) {
        int lightLevel = ILightInfluencedItem.getLightLevel(stack);
        float weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);

        return (lightLevel > 7 ? 0.15F : 0.00F) + weatherBoosterLevel/100;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        if (baseSpeed > 1.0F) {
            float bonus = calculateBonusDestroySpeed(stack);
            float speedMultiplier = 1.0F + bonus;

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    // --- Tooltip Display (Reads state from NBT) ---
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        IIgnitableTool.appendHoverText(stack, tooltip);
        treasureHandler.appendHoverText(stack, tooltip, "§eMining stone in the badlands yields treasure during clear weather.");
    }

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    @Override
    public void igniteEntity(LivingEntity target, ItemStack stack) {
        target.setSecondsOnFire(2);
    }

    /**
     * Handles the entity hit event (Left Click on Entity).
     */
    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        igniteEntity(target, stack);

        stack.hurtAndBreak(1, attacker, (e) -> e.broadcastBreakEvent(attacker.getUsedItemHand()));

        return true;
    }

    /**
     * Handles the block use event (Right Click).
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();

        if (world.isClientSide)
            return super.useOn(context);

        PlayerEntity player = context.getPlayer();
        if (player == null)
            return super.useOn(context);

        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);

        return IIgnitableTool.useOn(context, (itemStack) -> {
            boolean success = false;
            if (clickedState.is(Blocks.COBBLESTONE)) {
                BlockState state = world.getRandom().nextInt(10) == 0 ? Blocks.INFESTED_STONE.defaultBlockState() : Blocks.STONE.defaultBlockState();
                world.setBlock(clickedPos, state, 11);
                success = true;
            } else if (clickedState.is(Blocks.STONE)) {
                world.setBlock(clickedPos, Blocks.SMOOTH_STONE.defaultBlockState(), 11);
                success = true;
            } else if (clickedState.is(Blocks.SMOOTH_STONE)) {
                BlockState state = world.getRandom().nextInt(10) == 0 ? Blocks.INFESTED_CHISELED_STONE_BRICKS.defaultBlockState() : Blocks.CHISELED_STONE_BRICKS.defaultBlockState();
                world.setBlock(clickedPos, state, 11);
                success = true;
            }

            if (success) {
                player.giveExperiencePoints(1);
                world.playSound(null, clickedPos, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 0.8F, 1.2F);

                stack.hurtAndBreak(durabilityUse * 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                return ActionResultType.CONSUME;
            }
            return ActionResultType.PASS;
        });
    }

    @Override
    public DimensionType primaryDimension() {
        return DimensionType.NETHER;
    }
}
