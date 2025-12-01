package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.custom.CustomAttributeArmor;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FireArmorItem extends CustomAttributeArmor {
    protected float recoverAmount = 0.1F;
    protected int perTickRecoverSpeed = 20;

    public FireArmorItem(IArmorMaterial materialIn, float recoverAmount, int perTickRecoverSpeed, EquipmentSlotType slot, Multimap<Attribute, Double> attributeBonuses, Properties builderIn) {
        super(materialIn, slot, attributeBonuses, builderIn);
        this.recoverAmount = recoverAmount;
        this.perTickRecoverSpeed = perTickRecoverSpeed;
    }
    public FireArmorItem(IArmorMaterial materialIn, int perTickRecoverSpeed, EquipmentSlotType slot, Multimap<Attribute, Double> attributeBonuses, Properties builderIn) {
        super(materialIn, slot, attributeBonuses, builderIn);
        this.perTickRecoverSpeed = perTickRecoverSpeed;
    }
    public FireArmorItem(IArmorMaterial materialIn, float recoverAmount, EquipmentSlotType slot, Multimap<Attribute, Double> attributeBonuses, Properties builderIn) {
        super(materialIn, slot, attributeBonuses, builderIn);
        this.recoverAmount = recoverAmount;
    }
    public FireArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Multimap<Attribute, Double> attributeBonuses, Properties builderIn) {
        super(materialIn, slot, attributeBonuses, builderIn);
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);
        if (world.isClientSide)
            return;
        if (player.tickCount % perTickRecoverSpeed == 0 && (Weather.getCurrentWeather(world) == Weather.CLEAR || DimensionType.getCurrentDimension(world) == DimensionType.NETHER))
            player.heal(recoverAmount);
    }
}
