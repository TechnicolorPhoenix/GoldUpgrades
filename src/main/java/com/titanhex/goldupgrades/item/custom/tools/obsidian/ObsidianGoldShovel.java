package com.titanhex.goldupgrades.item.custom.tools.obsidian;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ObsidianGoldShovel extends ShovelItem
{
    // Defines the amount of durability to restore when absorbing lava
    private int repairAmount;

    public ObsidianGoldShovel(IItemTier tier, float atkDamage, float atkSpeed, int repairAmount, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.repairAmount = repairAmount;
    }

    /**
     * Handles the block use event (Right Click) with custom Pickaxe and Fire functionality.
     */
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        Direction face = context.getClickedFace();
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);

        // --- NEW LOGIC: Lava Absorption (Repair/Reward functionality) ---
        // Check 1: Server-side, Player exists, and NOT Nether
        // Using the isRemote check ensures logic runs once, and player != null is a safety check.
        if (!world.isClientSide && player != null && world.dimension() != World.NETHER) {

            RayTraceResult hitResult = world.clip(
                    new RayTraceContext(
                            player.getEyePosition(1.0F),
                            player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)),
                            RayTraceContext.BlockMode.OUTLINE,
                            RayTraceContext.FluidMode.ANY, // Check ANY block/fluid in range
                            player
                    )
            );

            // Check 2: Block is Lava source block
            if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockHit = (BlockRayTraceResult) hitResult;
                BlockPos rayHitPos = blockHit.getBlockPos();
                BlockState rayHitState = world.getBlockState(rayHitPos);

                // 1. Check for Water -> Ice conversion (Priority 1)
                if (rayHitState.getBlock() == Blocks.LAVA) {

                    // Remove Lava by setting it to air (or block if desired, but air removes the source)
                    world.setBlock(rayHitPos, Blocks.AIR.defaultBlockState(), 3);

                    // Repair Tool by LAVA_REPAIR_AMOUNT
                    int currentDamage = stack.getDamageValue();
                    stack.setDamageValue(Math.max(0, currentDamage - repairAmount)); // Sets damage to a lower value, max 0.

                    // Give EXP
                    player.giveExperiencePoints(1);

                    // Play sound (Extinguish)
                    world.playSound(null, clickedPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }
}