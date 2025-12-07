package com.titanhex.goldupgrades.item.custom.inter;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public interface ILevelableItem {
    int getItemLevel();

    static int getTotalSetLevel(LivingEntity livingEntity) {
        int totalLevel = 0;
        // EquipmentSlotType.values() will give us HEAD, CHEST, LEGS, FEET
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
                ItemStack stack = livingEntity.getItemBySlot(slot);
                if (stack.getItem() instanceof ILevelableItem) {
                    ILevelableItem armorItem = (ILevelableItem) stack.getItem();
                    // armorItem.armorLevel is available via the getItemLevel() method
                    totalLevel += armorItem.getItemLevel();
                }
            }
        }
        return totalLevel;
    }
}
