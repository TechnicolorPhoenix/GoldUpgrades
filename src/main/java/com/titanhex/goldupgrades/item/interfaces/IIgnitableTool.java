package com.titanhex.goldupgrades.item.interfaces;

import com.titanhex.goldupgrades.GoldUpgradesConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface defining the required ignition capabilities for a tool.
 * This can be implemented by any Item class (e.g., a Sword or a Custom Tool).
 */
public interface IIgnitableTool {

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    void igniteEntity(LivingEntity target, ItemStack stack);
    int durabilityUse = 0;

    @FunctionalInterface
    interface IgnitableToolAction{
        ActionResultType apply(ItemStack stack);
    }

    static float calculateBonusDestroySpeed(ItemStack stack) {
        int lightLevel = ILightInfluencedItem.getLightLevel(stack);

        return (lightLevel > 7 ? 0.15F : 0.00F) + (float) IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack)/100;
    }

    static void appendHoverText(@NotNull ItemStack stack, @NotNull List<ITextComponent> tooltip) {
        int lightLevel = ILightInfluencedItem.getLightLevel(stack);
        int bonus = (int) (calculateBonusDestroySpeed(stack) * 100);

        if (lightLevel > 7)
            tooltip.add(new StringTextComponent("§9+" + bonus + "% Harvest Speed ."));
    }

    static ActionResultType useOn(ItemUseContext context, @Nullable IgnitableToolAction specializedAction) {
        World world = context.getLevel();
        if (world.isClientSide) return ActionResultType.PASS;

        PlayerEntity player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);
        ItemStack stack = context.getItemInHand();

        // A. Convert Logs to Charcoal (Common to all)
        if (clickedState.is(BlockTags.LOGS)) {
            world.removeBlock(clickedPos, false);
            Block.popResource(world, clickedPos, new ItemStack(Items.CHARCOAL));
            if (player != null) {
                player.giveExperiencePoints(1);
                stack.hurtAndBreak(durabilityUse * 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
            }
            world.playSound(null, clickedPos, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 0.8F, 1.2F);
            return ActionResultType.SUCCESS;
        }

        // C. Extinguish Fire & Place Torches (Common)
        Direction face = context.getClickedFace();
        BlockPos facePos = clickedPos.relative(face);
        Block clickedBlock = clickedState.getBlock();

        Block faceBlock = world.getBlockState(facePos).getBlock();

        // Extinguish Fire
        if (clickedBlock == Blocks.FIRE) {
            world.setBlock(clickedPos, Blocks.AIR.defaultBlockState(), 11);
            stack.setDamageValue(stack.getDamageValue() - 2); // Heal item
            if (player != null) player.giveExperiencePoints(1);
            world.playSound(null, clickedPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1.2F);
            return ActionResultType.SUCCESS;
        }

        if (clickedBlock == Blocks.TORCH || faceBlock == Blocks.TORCH) {
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

            if (player != null) {
                stack.hurtAndBreak(GoldUpgradesConfig.TORCH_DURABILITY_COST.get(), player, (e) -> e.broadcastBreakEvent(context.getHand()));
                player.giveExperiencePoints(1);
            }

            return ActionResultType.SUCCESS;
        }

        if (specializedAction != null) {
            return specializedAction.apply(stack);
        }

        return ActionResultType.PASS;
    }
}
