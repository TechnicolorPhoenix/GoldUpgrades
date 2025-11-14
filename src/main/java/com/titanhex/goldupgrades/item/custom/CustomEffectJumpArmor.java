package com.titanhex.goldupgrades.item.custom;

import com.titanhex.goldupgrades.item.custom.inter.IJumpBoostArmor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.potion.Effect;

import java.util.Map;

public class CustomEffectJumpArmor extends CustomEffectArmor implements IJumpBoostArmor {
    double jumpBoost;

    public CustomEffectJumpArmor(IArmorMaterial materialIn, EquipmentSlotType slot, float jumpBoost, Map<Effect, Integer> effects, Properties builderIn) {
        super(materialIn, slot, effects, builderIn);
        this.jumpBoost = jumpBoost;
    }


    @Override
    public double getJumpBoostModifier() {
        return this.jumpBoost;
    }
}
