package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.item.IgnitableTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.World;

public class FireGoldPickaxe extends PickaxeItem implements IgnitableTool
{
    int burnTicks;
    int durabilityUse;

    public FireGoldPickaxe(IItemTier tier, int atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
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

        // --- NEW LOGIC: Stone Conversion (Pickaxe functionality) ---
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

                // Drop the converted block
                Block.popResource(world, clickedPos, new ItemStack(dropBlock));

                // Give experience
                player.giveExperiencePoints(1);

                // Play a sizzling sound (or use a different sound if preferred)
                world.playSound(null, clickedPos, SoundEvents.STONE_BREAK, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);

                // Damage the tool
                stack.hurtAndBreak(this.durabilityUse*2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                return ActionResultType.SUCCESS;
            }
        }

        // Target position is one block out from the clicked face
        BlockPos firePos = clickedPos.relative(face);

        if (world.isClientSide) {
            // In 1.16.5, return success on client side so the action plays out correctly
            return ActionResultType.SUCCESS;
        }

        // Check if the target block is replaceable with fire
        // In 1.16.5, you check if the block is air/replaceable OR if the fire block can be placed there
        if (world.isEmptyBlock(firePos) || Blocks.FIRE.getBlock().defaultBlockState().canSurvive(world, firePos)) {

            igniteBlock(player, world, firePos);

            // Return success on the server side
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
