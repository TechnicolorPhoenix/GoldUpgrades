package com.titanhex.goldupgrades.item.components;

import com.titanhex.goldupgrades.item.interfaces.ILevelableItem;
import com.titanhex.goldupgrades.item.interfaces.IWaterInfluencedItem;
import com.titanhex.goldupgrades.item.interfaces.IWeatherInfluencedItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SeaToolComponent {

    public final UUID SEA_DAMAGE_MODIFIER = UUID.randomUUID();
    private static final Random RANDOM = new Random();
    int baseDurabilityCost;
    public SeaToolComponent(int baseDurabilityCost) {
        this.baseDurabilityCost = baseDurabilityCost;
    }

    @FunctionalInterface
    public interface SeaToolAction{
        ActionResultType apply(ItemStack stack);
    }

    public float getSeaDamage(Item item, ItemStack stack){
        ILevelableItem levelableItem = (ILevelableItem) item;
        IWeatherInfluencedItem weatherItem = (IWeatherInfluencedItem) item;
        return levelableItem.getItemLevel() + (weatherItem.isRaining(stack) ? (float) IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack) /2: 0F);
    }

    public int damageItem(ItemStack stack, int amount) {
        IWaterInfluencedItem waterTool = (IWaterInfluencedItem) stack.getItem();
        if (waterTool.getIsSubmerged(stack) && waterTool.hasWaterDiverEnchantment(stack)){
            int lowestValue = RANDOM.nextInt(2);
            amount = Math.max(lowestValue, amount - waterTool.getWaterDiverEnchantmentLevel(stack));
        }
        return amount;
    }

    public void appendHoverText(ItemStack usedStack, List<ITextComponent> tooltip){
        Item tool = usedStack.getItem();
        IWaterInfluencedItem waterTool = (IWaterInfluencedItem) tool;
        boolean submerged = waterTool.getIsSubmerged(usedStack);
        IWeatherInfluencedItem weatherTool = (IWeatherInfluencedItem) tool;
        boolean inRain = weatherTool.isRaining(usedStack);
        int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(usedStack);
        if (submerged && inRain) {
            tooltip.add(new StringTextComponent("§a+" + (20 + weatherBoosterLevel*5) + "% Harvest Speed"));
        } else if (inRain || submerged) {
            tooltip.add(new StringTextComponent("§a+" + (15 + weatherBoosterLevel*5) + "% Harvest Speed"));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Water required for harvest bonus."));
        }
    }

    public float getDestroySpeed(@NotNull ItemStack stack) {
        Item tool = stack.getItem();
        IWeatherInfluencedItem weatherTool = (IWeatherInfluencedItem) tool;
        IWaterInfluencedItem waterTool = (IWaterInfluencedItem) tool;

        boolean isInRain = waterTool.getIsInRain(stack);
        boolean isRaining = weatherTool.isRaining(stack);
        boolean isSubmerged = waterTool.getIsSubmerged(stack);

        float bonusSpeed = isSubmerged ? isRaining ? 0.20F : 0.15F : isInRain ? 0.15F : 0F;

        if (isRaining)
            bonusSpeed += 0.05F * IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);

        return bonusSpeed;
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, ItemStack stack, ActionResult<ItemStack> result) {
        BlockRayTraceResult hitResult = world.clip(
                new RayTraceContext(
                        player.getEyePosition(1.0F),
                        player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)),
                        RayTraceContext.BlockMode.OUTLINE,
                        RayTraceContext.FluidMode.ANY, // Check ANY block/fluid in range
                        player
                )
        );

        if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = hitResult.getBlockPos();
            BlockState hitState = world.getBlockState(hitPos);

            if (hitState.getBlock() == Blocks.WATER ||
                    hitState.getBlock() == Blocks.ICE ||
                    hitState.getBlock() == Blocks.PACKED_ICE) {

                return ActionResult.pass(stack);
            }
        }

        if (result.getResult().consumesAction()) {
            if (!world.isClientSide) {
                ServerWorld serverWorld = (ServerWorld) world;

                // Spawn Falling Water Particles around the player
                double x = player.getX();
                double y = player.getY() + player.getBbHeight() / 2.0D;
                double z = player.getZ();

                serverWorld.sendParticles(
                        ParticleTypes.FALLING_WATER, // The particle type
                        x, y, z,                     // Position (center of the player)
                        30,                          // Count (number of particles to spawn)
                        0.5D, 0.5D, 0.5D,             // X, Y, Z displacement variance (spread)
                        0.05D                         // Speed
                );
            }
        }

        return result;
    }

    public ActionResultType useOn(Item mainItem, ItemUseContext context, @Nullable SeaToolAction specializedAction){
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos hitPos = context.getClickedPos();

        if (world.isClientSide || player == null)
            return ActionResultType.PASS;

        int toolLevel = ILevelableItem.getItemLevel(mainItem);

        BlockRayTraceResult hitResult = world.clip(
                new RayTraceContext(
                        player.getEyePosition(1.0F),
                        player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)),
                        RayTraceContext.BlockMode.OUTLINE,
                        RayTraceContext.FluidMode.SOURCE_ONLY,
                        player
                )
        );

        BlockState state = world.getBlockState(hitPos);

        if (state.getBlock() == Blocks.SNOW) {
            world.setBlock(hitPos, Blocks.ICE.defaultBlockState(), 11);

            world.playSound(null, hitPos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.5F);

            stack.hurtAndBreak(baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

            return ActionResultType.SUCCESS;
        } else if (state.getBlock() == Blocks.ICE && toolLevel > 1) {
            world.setBlock(hitPos, Blocks.PACKED_ICE.defaultBlockState(), 11);

            world.playSound(null, hitPos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 0.8F);

            stack.hurtAndBreak(baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

            return ActionResultType.SUCCESS;
        } else if (state.getBlock() == Blocks.PACKED_ICE && toolLevel > 2) {
            world.setBlock(hitPos, Blocks.BLUE_ICE.defaultBlockState(), 11);

            world.playSound(null, hitPos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 0.8F);

            stack.hurtAndBreak(baseDurabilityCost, player, (p) -> p.broadcastBreakEvent(context.getHand()));

            return ActionResultType.SUCCESS;
        } else if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos rayHitPos = hitResult.getBlockPos();
            BlockState rayHitState = world.getBlockState(rayHitPos);

            if (rayHitState.getBlock() == Blocks.WATER) {
                world.setBlock(rayHitPos, Blocks.ICE.defaultBlockState(), 11);

                world.playSound(null, rayHitPos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.5F);

                stack.hurtAndBreak(baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

                return ActionResultType.SUCCESS;
            }
        }

        if (specializedAction != null) {
            return specializedAction.apply(stack);
        }

        return ActionResultType.PASS;
    }
}
