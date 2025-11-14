package com.titanhex.goldupgrades.item.custom;

import com.titanhex.goldupgrades.item.custom.inter.IJumpBoostArmor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;

public class CustomJumpArmor extends ArmorItem implements IJumpBoostArmor {
    double jumpBoost;

    public CustomJumpArmor(IArmorMaterial materialIn, EquipmentSlotType slot, float jumpBoost, Properties builderIn) {
        super(materialIn, slot, builderIn);
        this.jumpBoost = jumpBoost;
    }

    @Override
    public double getJumpBoostModifier() {
        return this.jumpBoost;
    }
}
