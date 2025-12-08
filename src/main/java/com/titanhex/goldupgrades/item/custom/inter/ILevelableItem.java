package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.item.ModArmorMaterial;
import com.titanhex.goldupgrades.item.ModItemTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;

public interface ILevelableItem {
    default int getItemLevel(){
        if (this instanceof TieredItem) {
            TieredItem tieredItem = (TieredItem) this;
            return tieredItem.getTier().getLevel();
        } else if (this instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) this;
            if (armor.getMaterial() instanceof ModArmorMaterial) {
                ModArmorMaterial modMaterial = (ModArmorMaterial) ((ArmorItem) this).getMaterial();
                return modMaterial.getLevel();
            }
        }
        return -1;
    }

    static int getTotalSetLevel(LivingEntity livingEntity) {
        int totalLevel = 0;
        // EquipmentSlotType.values() will give us HEAD, CHEST, LEGS, FEET
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
                ItemStack stack = livingEntity.getItemBySlot(slot);
                if (stack.getItem() instanceof ILevelableItem) {
                    ILevelableItem armorItem = (ILevelableItem) stack.getItem();
                    // armorItem.itemLevel is available via the getItemLevel() method
                    totalLevel += armorItem.getItemLevel();
                }
            }
        }
        return totalLevel;
    }
}
