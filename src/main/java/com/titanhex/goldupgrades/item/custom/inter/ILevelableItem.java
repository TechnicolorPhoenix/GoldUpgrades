package com.titanhex.goldupgrades.item.custom.inter;

import com.titanhex.goldupgrades.item.ModArmorMaterial;
import com.titanhex.goldupgrades.item.ModItemTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;

public interface ILevelableItem {

    static int getItemLevel(Item item){
        if (item instanceof TieredItem) {
            TieredItem tieredItem = (TieredItem) item;
            return tieredItem.getTier().getLevel();
        } else if (item instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) item;
            if (armor.getMaterial() instanceof ModArmorMaterial) {
                ModArmorMaterial modMaterial = (ModArmorMaterial) ((ArmorItem) item).getMaterial();
                return modMaterial.getLevel();
            }
        }
        return -1;
    }
    default int getItemLevel(){
        if (this instanceof  Item)
            return getItemLevel((Item) this);

        return -1;
    }

    default int getTotalSetLevel(LivingEntity livingEntity) {
        int totalLevel = 0;
        Class<?> containingClass = this.getClass();
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
                ItemStack stack = livingEntity.getItemBySlot(slot);
                if (stack.getItem() instanceof ILevelableItem && stack.getItem().getClass() == containingClass) {
                    ILevelableItem armorItem = (ILevelableItem) stack.getItem();
                    totalLevel += armorItem.getItemLevel();
                }
            }
        }
        return totalLevel;
    }
}
