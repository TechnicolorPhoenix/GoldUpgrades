package com.titanhex.goldupgrades.item.custom.inter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.client.util.ITooltipFlag;
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
import java.util.function.Consumer;

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
    /**
     * Attempts to ignite a block, similar to a Flint and Steel.
     * This logic is typically called from the Item's onItemUse method.
     *
     * @param player The player performing the action.
     * @param world The world the action is taking place in.
     * @param pos The BlockPos of the block being targeted.
     * @return ActionResultType.SUCCESS if fire was placed, ActionResultType.PASS otherwise.
     */
    ActionResultType igniteBlock(PlayerEntity player, World world, BlockPos pos);

    static float calculateBonusDestroySpeed(ItemStack stack) {
        int lightLevel = ILightInfluencedItem.getLightLevel(stack);

        return (lightLevel > 7 ? 0.15F : 0.00F) + (float) IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack)/100;
    }

    static void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        int lightLevel = ILightInfluencedItem.getLightLevel(stack);
        int bonus = (int) (calculateBonusDestroySpeed(stack) * 100);

        if (lightLevel > 7)
            tooltip.add(new StringTextComponent("§9+" + bonus + "% Harvest Speed ."));
    }

    static ActionResultType handleUseOn(ItemUseContext context, Consumer<ItemStack> specializedAction) {
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
            if (player != null) player.giveExperiencePoints(1);
            world.playSound(null, clickedPos, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 0.8F, 1.2F);
            stack.hurtAndBreak(durabilityUse * 2, player != null ? player : null, (p) -> p.broadcastBreakEvent(context.getHand()));
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

        if (specializedAction != null) {
            specializedAction.accept(stack);
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

            stack.hurtAndBreak(8, player, (e) -> e.broadcastBreakEvent(context.getHand()));
            player.giveExperiencePoints(1);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
