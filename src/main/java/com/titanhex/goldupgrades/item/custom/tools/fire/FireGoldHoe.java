package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.IgnitableTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Dimension;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class FireGoldHoe extends HoeItem implements IgnitableTool
{
    int burnTicks;
    int durabilityUse;

    private static final String NBT_NETHER = "WeaponInNether";
    private static final String NBT_IN_CLEAR = "WeaponInClearWeather";
    private static final String NBT_LIGHT_LEVEL = "WeaponLightLevel";

    public FireGoldHoe(IItemTier tier, int atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
    }

    private boolean getNether(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_NETHER);
    }
    private void setNether(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_NETHER, value);
    }

    private boolean getInClear(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IN_CLEAR);
    }
    private void setInClear(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IN_CLEAR, value);
    }

    private int getLightLevel(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_LIGHT_LEVEL);
    }
    private void setLightLevel(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(NBT_LIGHT_LEVEL, value);
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

        boolean weatherIsClear = Weather.getCurrentWeather(world) == Weather.CLEAR;
        boolean dimensionIsNether = DimensionType.getCurrentDimension(world) == DimensionType.NETHER;

        boolean oldNether = this.getNether(stack);
        boolean oldInClear = this.getInClear(stack);

        if (oldInClear != weatherIsClear) {
            setInClear(stack, weatherIsClear);
            stack.setTag(stack.getTag());
        } else if (oldNether != dimensionIsNether) {
            setNether(stack, dimensionIsNether);
            stack.setTag(stack.getTag());
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

        tooltip.add(new StringTextComponent( (bonusSpeed > 0 ? "§9" : "§c" ) + "+" + bonusSpeed + "% Harvest Speed ."));
    }

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    @Override
    public void igniteEntity(LivingEntity target, ItemStack stack) {
        target.setSecondsOnFire(2 + (getInClear(stack) ? 2 : 0));
    }

    /**
     * Attempts to ignite a block, similar to a Flint and Steel.
     * This logic is typically called from the Item's onItemUse method.
     *
     * @param player The player performing the action.
     * @param world  The world the action is taking place in.
     * @param pos    The BlockPos of the block being targeted.
     * @return ActionResultType.SUCCESS if fire was placed, ActionResultType.PASS otherwise.
     */
    @Override
    public ActionResultType igniteBlock(PlayerEntity player, World world, BlockPos pos) {
        BlockPos firePos = pos;

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
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        Direction face = context.getClickedFace();
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);


        if (clickedState.getBlock() == Blocks.POTATOES) {
            CropsBlock potatoBlock = (CropsBlock) clickedState.getBlock();

            if (potatoBlock.isMaxAge(clickedState)) {
                if (!world.isClientSide) {
                    world.removeBlock(clickedPos, false);

                    Random rand = world.getRandom();
                    int drops = rand.nextInt(4) + 1; // 1 to 4 cooked potatoes

                    Block.popResource(world, clickedPos, new ItemStack(Items.BAKED_POTATO, drops));

                    if (player != null) {
                        player.giveExperiencePoints(1);
                        world.playSound(null, clickedPos, SoundEvents.FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, 1.5F);
                    }

                    stack.hurtAndBreak(durabilityUse*2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

                }
                return ActionResultType.SUCCESS;
            }
        }

        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        BlockPos facePos = clickedPos.relative(face);

        if (world.isEmptyBlock(facePos) || Blocks.FIRE.getBlock().defaultBlockState().canSurvive(world, facePos)) {
            world.setBlock(facePos, Blocks.TORCH.defaultBlockState(), 11);
            stack.hurtAndBreak(5, player, (e) -> e.broadcastBreakEvent(context.getHand()));
            player.giveExperiencePoints(1 + (getInClear(stack) | getNether(stack) ? 3 : 0));

            return ActionResultType.SUCCESS;
        } else if (world.getBlockState(facePos).getBlock() == Blocks.FIRE) {
            world.setBlock(facePos, Blocks.AIR.getBlock().defaultBlockState(), 11);
            setDamage(stack, getDamage(stack) + 2 + (getInClear(stack) | getNether(stack) ? 3 : 0));
            player.giveExperiencePoints(1 + (getInClear(stack) | getNether(stack) ? 3 : 0));

            world.playSound(null, clickedPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1.2F);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
