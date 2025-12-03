package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.IgnitableTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FireGoldPickaxe extends PickaxeItem implements IgnitableTool
{
    int burnTicks;
    int durabilityUse;
    protected Weather weather = Weather.CLEAR;
    protected DimensionType dimension = DimensionType.OVERWORLD;
    protected int lightLevel = 0;

    public FireGoldPickaxe(IItemTier tier, int atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int uInt, boolean uBoolean) {
        int currentBrightness = Math.max(world.getBrightness(LightType.BLOCK, holdingEntity.blockPosition()), world.getBrightness(LightType.SKY, holdingEntity.blockPosition()));

        if (this.lightLevel != currentBrightness) {
            this.lightLevel = currentBrightness;
            stack.setTag(stack.getTag());
        }

        if (world.isClientSide)
            return;

        Weather newWeather = Weather.getCurrentWeather(world);
        DimensionType newDimension = DimensionType.getCurrentDimension(world);

        if (this.weather != newWeather) {
            this.weather = newWeather;
            stack.setTag(stack.getTag());
        }
        if (this.dimension != newDimension) {
            this.dimension = newDimension;
            stack.setTag(stack.getTag());
        }

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    // --- Tooltip Display (Reads state from NBT) ---
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        tooltip.add(new StringTextComponent("§eHarvest Speed +" + this.lightLevel + "%."));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float bonusSpeed = this.lightLevel * 0.01F;

        if (baseSpeed > 1.0F) {

            float speedMultiplier = 1.0F + bonusSpeed;

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    @Override
    public void igniteEntity(LivingEntity target) {
        target.setSecondsOnFire(2);
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

            return ActionResultType.sidedSuccess(world.isClientSide); // ActionResultType.success(world.isClientSide)
        }

        return ActionResultType.PASS;
    }

    /**
     * Handles the entity hit event (Left Click on Entity).
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 1. Call the interface method to apply the custom effect
        igniteEntity(target);

        // 2. Damage the tool item (separate from combat damage)
        stack.hurtAndBreak(1, attacker, (e) -> e.broadcastBreakEvent(attacker.getUsedItemHand()));

        // Return true to indicate the attack was successful
        return true;
    }

    /**
     * Handles the block use event (Right Click).
     */
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        Direction face = context.getClickedFace();
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);

        if (!world.isClientSide && player != null) {
            Block dropBlock = null;

            if (clickedState.getBlock() == Blocks.COBBLESTONE) {
                // Cobblestone converts to Stone
                dropBlock = Blocks.STONE;
            } else if (clickedState.getBlock() == Blocks.STONE) {
                // Stone converts to Smooth Stone
                dropBlock = Blocks.SMOOTH_STONE;
            } else if (clickedState.getBlock() == Blocks.STONE_BRICKS) {
                // Stone converts to Smooth Stone
                dropBlock = Blocks.CRACKED_STONE_BRICKS;
            } else if (clickedState.getBlock() == Blocks.NETHERITE_BLOCK) {
                // Stone converts to Smooth Stone
                dropBlock = Blocks.NETHER_BRICKS;
            }

            if (dropBlock != null) {
                world.removeBlock(clickedPos, false);

                Block.popResource(world, clickedPos, new ItemStack(dropBlock));

                player.giveExperiencePoints(1);

                world.playSound(null, clickedPos, SoundEvents.STONE_BREAK, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);

                stack.hurtAndBreak(this.durabilityUse*2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
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
            player.giveExperiencePoints(1);

            return ActionResultType.SUCCESS;
        } else if (world.getBlockState(facePos).getBlock() == Blocks.FIRE) {
            world.setBlock(facePos, Blocks.AIR.getBlock().defaultBlockState(), 11);
            setDamage(stack, getDamage(stack) + 2);
            player.giveExperiencePoints(1);

            world.playSound(null, clickedPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1.2F);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
