package com.idtech.item;

import com.idtech.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class HexBow extends CrossbowItem {
    public HexBow(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isCharged(stack)) {
            BlockPos pos = Utils.getBlockAtCursor(player, 50, true).offset(new Vec3i(0, 1, 0));
            if (pos != null){
                Utils.createExplosion(level, pos, 3f);
            }
            setCharged(stack, false);
            return InteractionResultHolder.success(stack);
        } else if (!player.getProjectile(stack).isEmpty()) {
            if (!isCharged(stack)) {
                player.startUsingItem(hand);
            }

            return InteractionResultHolder.consume(stack);
        } else {
            return InteractionResultHolder.fail(stack);
        }
    }
}
