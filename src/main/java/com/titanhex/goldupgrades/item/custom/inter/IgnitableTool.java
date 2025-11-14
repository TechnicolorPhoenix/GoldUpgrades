package com.titanhex.goldupgrades.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface defining the required ignition capabilities for a tool.
 * This can be implemented by any Item class (e.g., a Sword or a Custom Tool).
 */
public interface IgnitableTool {

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    void igniteEntity(LivingEntity target);

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
}
