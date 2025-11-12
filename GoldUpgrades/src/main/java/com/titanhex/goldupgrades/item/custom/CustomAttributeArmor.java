package com.titanhex.goldupgrades.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import java.util.Map;
import java.util.UUID;

public class CustomAttributeArmor extends ArmorItem {

    private final Multimap<Attribute, Double> attributeBonuses;

    private static final UUID[] ARMOR_MODIFIERS_UUID = new UUID[]{
            UUID.fromString("845DB3C2-CD78-40FB-8A8C-6BC55D1F6E1B"), // Boots
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), // Leggings
            UUID.fromString("9F3D476D-C118-4544-8365-648469C6A53A"), // Chestplate
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380C1EBA")  // Helmet
    };

    public CustomAttributeArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Multimap<Attribute, Double> attributeBonuses, Properties builderIn) {
        super(materialIn, slot, builderIn.rarity(Rarity.COMMON));
        // Use an ImmutableMultimap to store the bonuses safely
        this.attributeBonuses = ImmutableMultimap.copyOf(attributeBonuses);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        // 1. Start with the default armor attributes (e.g., protection)
        Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(equipmentSlot, stack);

        // 2. Prepare a builder for the full list of modifiers
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(modifiers);

        // 3. Only add the custom modifiers if the item is in its intended slot
        if (equipmentSlot == this.slot) {
            // Get the base UUID for this specific armor slot
            UUID slotUUID = ARMOR_MODIFIERS_UUID[equipmentSlot.getIndex()];

            // 4. Iterate over the custom attribute bonuses
            // The attributeBonuses is a Multimap<Attribute, Double>, but we can treat
            // it like a map of entries for simple iteration.
            int i = 0; // Use an index to make the UUID unique per attribute on the item

            for (Map.Entry<Attribute, Double> entry : this.attributeBonuses.entries()) {
                Attribute attribute = entry.getKey();
                double amount = entry.getValue();

                // Create a unique UUID for this specific attribute/slot combination.
                // We shift the least significant bits based on the attribute's index
                // within our map to ensure each modifier is unique for the same item.
                UUID attributeUUID = new UUID(slotUUID.getMostSignificantBits(), slotUUID.getLeastSignificantBits() + i);

                AttributeModifier customModifier = new AttributeModifier(
                        attributeUUID,
                        // Name the modifier clearly, using the attribute's registry name
                        "Armor Attribute Bonus: " + attribute.getRegistryName().getPath(),
                        amount,
                        AttributeModifier.Operation.ADDITION // Assuming ADDITION is the desired operation
                );

                // Add the custom attribute modifier
                builder.put(attribute, customModifier);
                i++;
            }
        }

        return builder.build();
    }
}
