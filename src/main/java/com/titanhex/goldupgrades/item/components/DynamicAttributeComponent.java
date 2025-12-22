package com.titanhex.goldupgrades.item.components;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.inventory.EquipmentSlotType;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class DynamicAttributeComponent {

    public UUID dynamicUUID = UUID.randomUUID();
    public Attribute attribute = Attributes.ATTACK_DAMAGE;
    private String attributeName = "Weapon modifier";
    private EquipmentSlotType slotType = EquipmentSlotType.MAINHAND;

    public DynamicAttributeComponent() {}
    public DynamicAttributeComponent(UUID dynamicUUID, EquipmentSlotType slotType) {
        this.dynamicUUID = dynamicUUID;
        this.slotType = slotType;
    }

    public DynamicAttributeComponent(UUID dynamicUUID, Attribute attribute, String attributeName, EquipmentSlotType slotType) {
        this.attribute = attribute;
        this.dynamicUUID = dynamicUUID;
        this.attributeName = attributeName;
        this.slotType = slotType;
    }

    public ImmutableMultimap.Builder<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ImmutableMultimap.Builder<Attribute, AttributeModifier> builder, Supplier<Boolean> environmentTester, Supplier<Double> valueNumber, AttributeModifier.Operation operation) {
        if (equipmentSlot == slotType) {
            if (environmentTester.get()) {
                return builder.put(attribute, new AttributeModifier(
                        dynamicUUID,
                        attributeName,
                        valueNumber.get(),
                        operation
                ));
            }
        }
        return builder;
    }

    public ImmutableMultimap.Builder<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ImmutableMultimap.Builder<Attribute, AttributeModifier> builder, Supplier<Boolean> environmentTester, Supplier<Double> valueNumber) {
        return getAttributeModifiers(
                equipmentSlot, builder, environmentTester, valueNumber,
                AttributeModifier.Operation.ADDITION
        );
    }

    public void updateAttributes(LivingEntity updatingEntity,
                                 Multimap<Attribute, AttributeModifier> newModifiers,
                                 ModifiableAttributeInstance modifiedAttribute)
    {
        modifiedAttribute.removeModifier(dynamicUUID);

        for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
            ModifiableAttributeInstance instance = updatingEntity.getAttribute(entry.getKey());
            if (instance != null) {
                if (entry.getValue().getId().equals(dynamicUUID)) {
                    instance.addTransientModifier(entry.getValue());
                }
            }
        }

    }
}
