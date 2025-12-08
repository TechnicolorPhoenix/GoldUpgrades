package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.item.custom.inter.IJumpBoostArmor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FallDamageHandler {

    @SubscribeEvent
    public void FallDamageHandler(LivingFallEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();
        float totalReduction = 0.0f;
        int stormArmorCount = 0;

        // 2. Iterate over all armor slots
        for (ItemStack armorPiece : livingEntity.getArmorSlots()) {
            if (armorPiece.getItem() instanceof IJumpBoostArmor) {
                IJumpBoostArmor armor = (IJumpBoostArmor) armorPiece.getItem();

                // Accumulate the reduction fraction from each piece
                float pieceReduction = armor.getFallDamageReductionFraction();
                totalReduction += pieceReduction;

                if (pieceReduction > 0.0f) {
                    stormArmorCount++;
                }
            }
        }

        totalReduction = Math.min(1.0f, totalReduction);

        if (totalReduction > 0.0f) {
            float originalDamage = event.getDamageMultiplier();

            float newDamageMultiplier = originalDamage * (1.0f - totalReduction);

            event.setDamageMultiplier(newDamageMultiplier);
        }
    }
}
