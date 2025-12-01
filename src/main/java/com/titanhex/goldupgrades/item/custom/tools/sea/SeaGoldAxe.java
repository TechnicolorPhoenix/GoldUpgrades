package com.titanhex.goldupgrades.item.custom.tools.sea;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectAxe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.potion.Effect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Map;

public class SeaGoldAxe extends EffectAxe {

    int durabilityCost;
    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier of the pickaxe.
     *
     * @param tier
     * @param attackDamage         The base attack damage of the tool.
     * @param attackSpeed          The attack speed modifier of the tool.
     * @param effectAmplifications A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration       The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost       The number of durability points to subtract on each use.
     * @param properties           Item properties.
     */
    public SeaGoldAxe(IItemTier tier, float attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed, effectAmplifications, effectDuration, durabilityCost, properties);
        this.durabilityCost = durabilityCost;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        RayTraceResult hitResult = world.clip(
                new RayTraceContext(
                        player.getEyePosition(1.0F),
                        player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)),
                        RayTraceContext.BlockMode.OUTLINE,
                        RayTraceContext.FluidMode.ANY, // Check ANY block/fluid in range
                        player
                )
        );

        if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = ((BlockRayTraceResult) hitResult).getBlockPos();
            BlockState hitState = world.getBlockState(hitPos);

            if (hitState.getBlock() == Blocks.WATER ||
                    hitState.getBlock() == Blocks.ICE ||
                    hitState.getBlock() == Blocks.PACKED_ICE ) {

                return ActionResult.pass(stack);
            }
        }

        return super.use(world, player, hand);
    }

    /**
     * Handles the item use event (Right Click) with custom logic for water/ice
     * conversion, falling back to the parent class's aura application.
     */
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        // The BlockPos of the block that was hit, which might be the water block itself.
        BlockPos hitPos = context.getClickedPos();

        // --- Server-side Logic ---
        if (!world.isClientSide) {

            // Get the RayTraceResult from the context, which holds the precise block hit.
            RayTraceResult hitResult = world.clip(
                    new RayTraceContext(
                            player.getEyePosition(1.0F),             // Start position (player's eyes)
                            player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)), // End position (5 blocks away)
                            RayTraceContext.BlockMode.OUTLINE,       // Only hit blocks with an outline
                            RayTraceContext.FluidMode.SOURCE_ONLY,           // Crucially, hit ANY fluid block
                            player
                    )
            );

            BlockState state = world.getBlockState(hitPos);

            // Check if the ray trace result is a BlockRayTraceResult (meaning it hit a block)
            if (state.getBlock() == Blocks.ICE) {

                // Set the block state to Packed Ice
                world.setBlock(hitPos, Blocks.PACKED_ICE.defaultBlockState(), 11);

                // Play a sound to indicate the hardening/packing
                world.playSound(null, hitPos, SoundEvents.STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);

                // Damage the tool by a small amount (1 point for block conversion)
                if (player != null) {
                    stack.hurtAndBreak(durabilityCost/2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                }

                return ActionResultType.SUCCESS;
//            } else if (state.getBlock() == Blocks.PACKED_ICE) {
//
//                // Set the block state to Packed Ice
//                world.setBlock(hitPos, Blocks.BLUE_ICE.defaultBlockState(), 11);
//
//                // Play a sound to indicate the hardening/packing
//                world.playSound(null, hitPos, SoundEvents.STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);
//
//                // Damage the tool by a small amount (1 point for block conversion)
//                if (player != null) {
//                    stack.hurtAndBreak(durabilityCost, player, (p) -> p.broadcastBreakEvent(context.getHand()));
//                }
//
//                return ActionResultType.SUCCESS;
            } else if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockHit = (BlockRayTraceResult) hitResult;
                BlockPos rayHitPos = blockHit.getBlockPos();
                BlockState rayHitState = world.getBlockState(rayHitPos);

                // 1. Check for Water -> Ice conversion (Priority 1)
                if (rayHitState.getBlock() == Blocks.WATER) {
                    // Set the block state to Ice
                    world.setBlock(rayHitPos, Blocks.ICE.defaultBlockState(), 11);

                    // Play a freezing sound
                    world.playSound(null, rayHitPos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.5F);

                    // Damage the tool by a small amount (1 point for block conversion)
                    if (player != null) {
                        stack.hurtAndBreak(durabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                    }

                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }
}
