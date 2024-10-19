package com.idtech.entity;

import com.idtech.Utils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class BoomArrow extends AbstractArrow {

    protected BoomArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected ItemStack getPickupItem() {
        return null;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        Utils.createExplosion(level, result.getBlockPos(), 10);
        super.onHit(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Utils.createExplosion(level, result.getEntity().blockPosition(), 10);
        super.onHitEntity(result);
    }
}
