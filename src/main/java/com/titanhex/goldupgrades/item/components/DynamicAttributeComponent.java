package com.titanhex.goldupgrades.item.components;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.titanhex.goldupgrades.item.custom.inter.ILevelableItem.getItemLevel;

public class DynamicAttributeComponent {

    public UUID dynamicUUID = UUID.randomUUID();
    public Attribute attribute = Attributes.ATTACK_DAMAGE;
    private String attributeName = "Weapon modifier";


    public DynamicAttributeComponent() {}
    public DynamicAttributeComponent(UUID dynamicUUID) {
        this.dynamicUUID = dynamicUUID;
    }
    public DynamicAttributeComponent(Attribute attribute, String attributeName, UUID dynamicUUID) {
        this.attribute = attribute;
        this.dynamicUUID = dynamicUUID;
        this.attributeName = attributeName;
    }

    public ImmutableMultimap.Builder<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack, ImmutableMultimap.Builder<Attribute, AttributeModifier> builder, Supplier<Boolean> environmentTester, Supplier<Float> valueNumber, AttributeModifier.Operation operation) {
        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
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

    public ImmutableMultimap.Builder<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack, ImmutableMultimap.Builder<Attribute, AttributeModifier> builder, Supplier<Boolean> environmentTester, Supplier<Float> valueNumber) {
        return getAttributeModifiers(
                equipmentSlot, stack, builder, environmentTester, valueNumber,
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
