package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.IgnitableTool;
import com.titanhex.goldupgrades.item.custom.inter.IDayInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IDimensionInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.ILightInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class FireGoldHoe extends HoeItem implements IgnitableTool, IDimensionInfluencedItem, IWeatherInfluencedItem, IDayInfluencedItem, ILightInfluencedItem
{
    int burnTicks;
    int durabilityUse;

    public FireGoldHoe(IItemTier tier, int atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int uInt, boolean uBoolean) {
        int currentBrightness = world.getRawBrightness(holdingEntity.blockPosition(), 0);

        int oldBrightness = getLightLevel(stack);

        if (oldBrightness != currentBrightness) {
            setLightLevel(stack, currentBrightness);
        }

        if (world.isClientSide)
            return;

        DimensionType oldDimension = getDimension(stack);
        Weather oldWeather = getWeather(stack);
        boolean oldIsDay = getIsDay(stack);

        DimensionType currentDimension = DimensionType.getCurrentDimension(world);
        Weather currentWeather = Weather.getCurrentWeather(world);
        boolean currentIsDay = world.isDay();

        if (oldWeather != currentWeather || oldDimension != currentDimension || currentIsDay != oldIsDay) {
            setWeather(stack, currentWeather);
            setDimension(stack, currentDimension);
            setIsDay(stack, currentIsDay);
        }

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float bonusSpeed = getLightLevel(stack) * 0.01F;

        if (baseSpeed > 1.0F) {
            float speedMultiplier = 1.0F + bonusSpeed;

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    // --- Tooltip Display (Reads state from NBT) ---
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        int bonusSpeed = getLightLevel(stack);

        if (bonusSpeed > 0)
        tooltip.add(new StringTextComponent("§9+" + bonusSpeed + "% Harvest Speed ."));
    }

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    @Override
    public void igniteEntity(LivingEntity target, ItemStack stack) {
        target.setSecondsOnFire(2 + (getIsDay(stack) ? 2 : 0));
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

            // Play sound effect
            world.playSound(player, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);

            // Set the block to fire
            world.setBlock(firePos, Blocks.FIRE.getBlock().defaultBlockState(), burnTicks);

            // Damage the tool item
            if (player != null) {
                player.getItemInHand(player.getUsedItemHand()).hurtAndBreak(durabilityUse, player, (p) -> {
                    p.broadcastBreakEvent(player.getUsedItemHand());
                });
            }

            return ActionResultType.sidedSuccess(world.isClientSide); // ActionResultType.success(world.isRemote)
        }

        return ActionResultType.PASS;
    }

    /**
     * Handles the entity hit event (Left Click on Entity).
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 1. Call the interface method to apply the custom effect
        igniteEntity(target, stack);

        // Return true to indicate the attack was successful
        return true;
    }

    /**
     * Handles the block use event (Right Click) with custom Hoe and Fire functionality.
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        PlayerEntity player = context.getPlayer();
        if (player == null)
            return super.useOn(context);

        Direction face = context.getClickedFace();
        ItemStack stack = context.getItemInHand(); // Get the ItemStack directly from the context
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);

        if (clickedState.is(BlockTags.LOGS)) {
            world.removeBlock(clickedPos, false);

            Block.popResource(world, clickedPos, new ItemStack(Items.CHARCOAL));

            player.giveExperiencePoints(1);
            world.playSound(null, clickedPos, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 0.8F, 1.2F);

            stack.hurtAndBreak(durabilityUse*2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

            return ActionResultType.SUCCESS;
        }

        BlockPos facePos = clickedPos.relative(face);

        if (world.getBlockState(facePos).getBlock() == Blocks.FIRE) {
            world.setBlock(facePos, Blocks.AIR.getBlock().defaultBlockState(), 11);
            setDamage(stack, getDamage(stack) + 2);
            player.giveExperiencePoints(1);

            world.playSound(null, clickedPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1.2F);

            return ActionResultType.SUCCESS;
        } else if (clickedState.getBlock() == Blocks.TORCH || world.getBlockState(facePos).getBlock() == Blocks.TORCH) {
            return ActionResultType.PASS;
        } else if (world.isEmptyBlock(facePos) || Blocks.FIRE.getBlock().defaultBlockState().canSurvive(world, facePos)) {
            if (context.getClickedFace() == Direction.UP) {
                world.setBlock(facePos, Blocks.TORCH.defaultBlockState(), 11);
            } else if (face.getAxis().isHorizontal()) {
                Block wallTorch = Blocks.WALL_TORCH;
                BlockState torchState = wallTorch.defaultBlockState().setValue(WallTorchBlock.FACING, face);
                world.setBlock(facePos, torchState, 1);
            } else
                return ActionResultType.PASS;

            stack.hurtAndBreak(5, player, (e) -> e.broadcastBreakEvent(context.getHand()));
            player.giveExperiencePoints(1);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
