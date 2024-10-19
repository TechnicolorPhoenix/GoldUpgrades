package net.titanhex.thex.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class GlowdustBlock extends Block {
    public GlowdustBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos blockPos, PlayerEntity playerIn, Hand handIn, BlockRayTraceResult result) {
        if (!world.isRemote()){
            if (handIn == Hand.MAIN_HAND){
                playerIn.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 60));
            }
            if (handIn == Hand.OFF_HAND){
                playerIn.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 15));
            }

        }
        return super.onBlockActivated(state, world, blockPos, playerIn, handIn, result);
    }

    @Override
    public void onBlockClicked(BlockState state, World world, BlockPos blockPos, PlayerEntity playerIn) {
        if (!world.isRemote()){
            playerIn.setFire(1);
        }
        super.onBlockClicked(state, world, blockPos, playerIn);
    }

    @Override
    public void onEntityWalk(World world, BlockPos blockPos, Entity entityIn) {
        super.onEntityWalk(world, blockPos, entityIn);
        entityIn.setGlowing(true);
    }
}
