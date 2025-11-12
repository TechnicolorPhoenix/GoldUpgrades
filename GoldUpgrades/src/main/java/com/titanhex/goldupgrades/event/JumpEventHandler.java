package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.item.custom.inter.IJumpBoostArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class JumpEventHandler {

    @SubscribeEvent
    public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        // Check if the entity is a player
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            double totalJumpBoost = 0.0;

            for (ItemStack armorPiece : player.getArmorSlots()) {
                // Check if the item stack is your custom armor piece
                if (armorPiece.getItem() instanceof IJumpBoostArmor) {
                    totalJumpBoost += ((IJumpBoostArmor) armorPiece.getItem()).getJumpBoostModifier(); // Example boost value
                }
            }

            if (totalJumpBoost > 0) {
                Vector3d motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x, motion.y + totalJumpBoost, motion.z);
            }
        }
    }
}