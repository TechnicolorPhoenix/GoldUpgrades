package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.GoldUpgrades;
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

        for (ItemStack armorPiece : livingEntity.getArmorSlots()) {
            if (armorPiece.getItem() instanceof IJumpBoostArmor) {
                IJumpBoostArmor armor = (IJumpBoostArmor) armorPiece.getItem();

                totalReduction += armor.getFallDamageReductionFraction();
            }
        }

        totalReduction = Math.min(1.0f, totalReduction);

        if (totalReduction > 0.0f) {
            float newDamageMultiplier = (event.getDamageMultiplier() - totalReduction);
            GoldUpgrades.LOGGER.debug("FALL REDUCTION: {}, FALL DISTANCE: {}, NEW REDUCTION: {}, NEW DISTANCE: {}", totalReduction, event.getDistance(), newDamageMultiplier, event.getDistance() * newDamageMultiplier);

            event.setDistance(event.getDistance() * newDamageMultiplier);
        }
    }
}
