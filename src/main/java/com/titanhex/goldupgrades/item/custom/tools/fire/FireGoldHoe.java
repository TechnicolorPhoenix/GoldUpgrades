package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.item.IgnitableTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class FireGoldHoe extends HoeItem implements IgnitableTool
{
    int burnTicks;
    int durabilityUse;

    public FireGoldHoe(IItemTier tier, int atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
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
        BlockState blockstate = world.getBlockState(pos);

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
        igniteEntity(target);

        // 2. Damage the tool item (separate from combat damage)
        stack.hurtAndBreak(1, attacker, (e) -> e.broadcastBreakEvent(attacker.getUsedItemHand()));

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


        // --- LOGIC: Cook Potato Plant (Hoe functionality) ---
        // Check if the block is a potato plant.
        if (clickedState.getBlock() == Blocks.POTATOES) {
            // Cast the block to CropsBlock and use the canonical isMaxAge check.
            CropsBlock potatoBlock = (CropsBlock) clickedState.getBlock();

            if (potatoBlock.isMaxAge(clickedState)) {
                if (!world.isClientSide) {
                    // Remove the mature potato plant
                    world.removeBlock(clickedPos, false);

                    Random rand = world.getRandom();
                    int drops = rand.nextInt(4) + 1; // 1 to 4 cooked potatoes

                    // Drop 1-4 Cooked Potatoes
                    Block.popResource(world, clickedPos, new ItemStack(Items.BAKED_POTATO, drops));

                    // Give experience
                    if (player != null) {
                        player.giveExperiencePoints(1);
                        // Play a soft sizzling sound
                        world.playSound(null, clickedPos, SoundEvents.FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, 1.5F);
                    }

                    // Damage the tool
                    stack.hurtAndBreak(durabilityUse*2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

                }
                return ActionResultType.SUCCESS;
            }
        }

        // Target position is one block out from the clicked face
        BlockPos firePos = clickedPos.relative(face);

        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        // Check if the target block is replaceable with fire
        if (world.isEmptyBlock(firePos) || Blocks.FIRE.getBlock().defaultBlockState().canSurvive(world, firePos)) {

            igniteBlock(player, world, firePos);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
