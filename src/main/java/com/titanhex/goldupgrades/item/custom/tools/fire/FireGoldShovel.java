package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.components.TreasureToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
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
import java.util.Objects;

public class FireGoldShovel extends ShovelItem implements ILevelableItem, IIgnitableTool, ILightInfluencedItem, IDimensionInfluencedItem, IWeatherInfluencedItem, IDayInfluencedItem
{
    int burnTicks;
    int durabilityUse;
    TreasureToolComponent treasureHandler;

    public FireGoldShovel(IItemTier tier, float atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
        treasureHandler = new TreasureToolComponent();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, Entity holdingEntity, int uInt, boolean uBoolean) {
        int currentBrightness = getLightLevel(stack, world, holdingEntity.blockPosition());

        int oldBrightness = ILightInfluencedItem.getLightLevel(stack);

        if (oldBrightness != currentBrightness)
            ILightInfluencedItem.setLightLevel(stack, currentBrightness);

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

    private float calculateBonusDestroySpeed(ItemStack stack) {
        int lightLevel = ILightInfluencedItem.getLightLevel(stack);

        return (lightLevel > 7 ? 0.15F : 0.00F) + (float) IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack)/100;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float bonus = calculateBonusDestroySpeed(stack);

        if (baseSpeed > 1.0F) {
            float speedMultiplier = 1.0F + bonus;

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        treasureHandler.appendHoverText(stack, tooltip, "§eDigging sand in the desert yields treasure during clear weather.");
        IIgnitableTool.appendHoverText(stack, worldIn, tooltip, flagIn);
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

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
        treasureHandler.tryDropTreasure(
                world, blockPos, blockState, miningEntity, usedStack,
                Biomes.DESERT.location(),
                BlockTags.SAND,
                new ResourceLocation("goldupgrades", "gameplay/treasure/desert_digging"),
                12
        );
        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    /**
     * Attempts to ignite a block, similar to a Flint and Steel.
     * This logic is typically called from the Item's onItemUse method.
     *
     * @param player The player performing the action.
     * @param world  The world the action is taking place in.
     * @param firePos    The BlockPos of the block being targeted.
     * @return ActionResultType.SUCCESS if fire was placed, ActionResultType.PASS otherwise.
     */
    @Override
    public ActionResultType igniteBlock(PlayerEntity player, World world, BlockPos firePos) {

        if (world.isEmptyBlock(firePos) || Blocks.FIRE.getBlock().defaultBlockState().canSurvive(world, firePos)) {
            world.playSound(player, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);

            world.setBlock(firePos, Blocks.FIRE.getBlock().defaultBlockState(), burnTicks);

            if (player != null) {
                player.getItemInHand(player.getUsedItemHand()).hurtAndBreak(durabilityUse, player, (p) -> {
                    p.broadcastBreakEvent(player.getUsedItemHand());
                });
            }

            return ActionResultType.sidedSuccess(world.isClientSide); // ActionResultType.success(world.isClientSide)
        }

        return ActionResultType.PASS;
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
        Block clickedBlock = world.getBlockState(clickedPos).getBlock();

        return IIgnitableTool.handleUseOn(context, (itemStack) -> {
            if (clickedBlock == Blocks.SAND) {
                world.setBlock(clickedPos, Blocks.GLASS.getBlock().defaultBlockState(), 11);
                setDamage(stack, getDamage(stack) + 2);
                player.giveExperiencePoints(1);
                world.playSound(null, clickedPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1.2F);
            }
        });
    }

    @Override
    public DimensionType primaryDimension() {
        return DimensionType.NETHER;
    }
}
