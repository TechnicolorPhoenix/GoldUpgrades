package com.titanhex.goldupgrades.item.custom.tools.sea;

import com.titanhex.goldupgrades.item.custom.tools.effect.EffectPickaxe;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectShovel;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;

public class SeaGoldShovel extends EffectShovel {

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
    public SeaGoldShovel(IItemTier tier, float attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed, effectAmplifications, effectDuration, durabilityCost, properties);
        this.durabilityCost = durabilityCost;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {

        if (super.use(world, player, hand) == ActionResult.success(player.getItemInHand(hand)) && !world.isClientSide) {

            ServerWorld serverWorld = (ServerWorld) world;

            // Spawn Enchanted Hit Particles around the player
            double x = player.getX();
            double y = player.getY() + player.getBbHeight() / 2.0D;
            double z = player.getZ();

            serverWorld.sendParticles(
                    ParticleTypes.BUBBLE, // The particle type
                    x, y, z,                     // Position (center of the player)
                    30,                          // Count (number of particles to spawn)
                    0.5D, 0.5D, 0.5D,             // X, Y, Z displacement variance (spread)
                    0.05D                         // Speed
            );

            return ActionResult.success(player.getItemInHand(hand));
        };

        return ActionResult.pass(player.getItemInHand(hand));
    }

    /**
     * Handles the item use event (Right Click) with custom logic for water/ice
     * conversion, falling back to the parent class's aura application.
     */
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        // Client side check: return success if we know a conversion will happen
        if (world.isClientSide) {
            if (state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.ICE) {
                return ActionResultType.SUCCESS;
            }
        }

        // --- Server-side Logic ---
        if (!world.isClientSide) {

            // 1. Check for Water -> Ice conversion (Priority 1)
            if (state.getBlock() == Blocks.WATER) {

                // Set the block state to Ice
                world.setBlock(pos, Blocks.ICE.defaultBlockState(), 11);

                // Play a freezing sound
                world.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.5F);

                // Damage the tool by a small amount (1 point for block conversion)
                if (player != null) {
                    stack.hurtAndBreak(durabilityCost/2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                }

                return ActionResultType.SUCCESS;
            } else if (state.getBlock() == Blocks.ICE) {

                // Set the block state to Packed Ice
                world.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 11);

                // Play a sound to indicate the hardening/packing
                world.playSound(null, pos, SoundEvents.STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);

                // Damage the tool by a small amount (1 point for block conversion)
                if (player != null) {
                    stack.hurtAndBreak(durabilityCost/2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                }

                return ActionResultType.SUCCESS;
            } else if (state.getBlock() == Blocks.PACKED_ICE) {

                // Set the block state to Packed Ice
                world.setBlock(pos, Blocks.BLUE_ICE.defaultBlockState(), 11);

                // Play a sound to indicate the hardening/packing
                world.playSound(null, pos, SoundEvents.STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);

                // Damage the tool by a small amount (1 point for block conversion)
                if (player != null) {
                    stack.hurtAndBreak(durabilityCost, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                }

                return ActionResultType.SUCCESS;
            } else if (state.getBlock() == Blocks.STONE) {

                // Set the block state to Packed Ice
                world.setBlock(pos, Blocks.ICE.defaultBlockState(), 11);

                // Play a sound to indicate the hardening/packing
                world.playSound(null, pos, SoundEvents.STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);

                // Damage the tool by a small amount (1 point for block conversion)
                if (player != null) {
                    stack.hurtAndBreak(durabilityCost/2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                }

                return ActionResultType.SUCCESS;
            }

            // 3. Check for Crops (Bonemeal effect) - Priority 3
            // IGrowable is the interface for blocks that can be grown with bonemeal (e.g., CropsBlock)
            if (state.getBlock() instanceof IGrowable && world instanceof ServerWorld) {
                IGrowable growable = (IGrowable) state.getBlock();

                // Check if the crop can even be grown further
                if (growable.isValidBonemealTarget(world, pos, state, world.isClientSide)) {
                    // Apply the bonemeal effect by calling the grow method
                    growable.performBonemeal((ServerWorld) world, world.getRandom(), pos, state);

                    // Play bonemeal sound/particle effect
                    world.playSound(null, pos, SoundEvents.HOE_TILL, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    // Damage the tool
                    if (player != null) {
                        stack.hurtAndBreak(durabilityCost/2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                        player.giveExperiencePoints(1);
                    }
                    return ActionResultType.SUCCESS;
                }
            }

        }
        return ActionResultType.PASS;
    }
}
