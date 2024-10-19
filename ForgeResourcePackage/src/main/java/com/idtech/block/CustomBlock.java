package com.idtech.block;

import com.idtech.BaseMod;
import com.idtech.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class CustomBlock extends InfestedBlock {

    private static Properties properties = Properties.of(Material.STONE).strength(6);
    public static Block INSTANCE = new CustomBlock(BlockMod.WATER_BLOCK, properties).setRegistryName(BaseMod.MODID, "ore");

    public CustomBlock(Block p_54178_, Properties p_54179_) {
        super(p_54178_, p_54179_);
    }


    /*@Override
    public void onCaughtFire(BlockState state, Level world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        igniter.setSecondsOnFire(3);
        super.onCaughtFire(state, world, pos, face, igniter);
    }*/

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        Utils.createExplosion(world, pos, 50);
        return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        entity.setDeltaMovement(0, 5, 0);
        world.setBlock(pos, state, 5);
        Utils.setFireBlock(world, pos, Direction.EAST);
        Utils.findNeighborBlock(pos);
        super.stepOn(world, pos, state, entity);
    }
}
