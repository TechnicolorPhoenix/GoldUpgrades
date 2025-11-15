package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;

public class RaidArmor extends DyeableArmorItem {

    public static final float damageIncreasePerPiece = 0.1f;
    public static final float damageReductionPerPiece = 0.1f;

    private static final UUID[] ARMOR_MODIFIERS_UUID = new UUID[]{
            UUID.fromString("845DB3C2-CD78-40FB-8A8C-6BC55D1F6E1B"), // Boots
            UUID.fromString("D8499B04-0E66-4726-AB29-3731E40BC747"), // Leggings
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), // Chestplate
            UUID.fromString("2AD3B246-ABBC-4958-86D3-E4534AE4202A")  // Helmet
    };

    private final Multimap<Attribute, Double> omenAttributes;
    private final Map<Effect, Integer> omenEffects;
    private final Multimap<Attribute, Double> raidAttributes;
    private final Map<Effect, Integer> raidEffects;
    private final int setBonusSize;

    public RaidArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Multimap<Attribute, Double> omenAttributes, Map<Effect, Integer> omenEffects, Multimap<Attribute, Double> raidAttributes, Map<Effect, Integer> raidEffects, int setBonusSize, Properties builder) {
        super(materialIn, slot, builder);
        this.omenAttributes = omenAttributes;
        this.omenEffects = omenEffects;
        this.raidAttributes = raidAttributes;
        this.raidEffects = raidEffects;
        this.setBonusSize = setBonusSize;
    }

    // FIX: Instead of calling super for unmatched slots, return an empty map.
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType slotIn) {
        if (slotIn == this.slot) {
            // Standard attribute map creation (Defense, Toughness)
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            UUID uuid = ARMOR_MODIFIERS_UUID[slotIn.getIndex()]; // Use the correct UUID for the slot

            // This is the default defense attribute from ArmorItem, required for vanilla tooltip
            builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid,
                    "Armor toughness", this.material.getToughness(), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ARMOR, new AttributeModifier(uuid,
                    "Armor modifier", this.material.getDefenseForSlot(slotIn), AttributeModifier.Operation.ADDITION));

            // Add custom attributes here if needed, but for the base armor stats, this is sufficient.

            return builder.build();
        }

        // FIX: If the game asks for modifiers for a different slot, return an empty map
        // to prevent the tooltip from showing attributes for every slot.
        return ImmutableMultimap.of();
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClientSide) {
            int raidArmorCount = getRaidArmorCount(player);
            boolean isWearingFullSet = raidArmorCount == setBonusSize;

            if (isWearingFullSet) {
                // Full Set (RAID) Bonus: Apply RAID effects and attributes
                addConditionalAttributes(player, raidAttributes, ARMOR_MODIFIERS_UUID[this.slot.getIndex()], "raid_armor_bonus");
                addConditionalEffects(player, raidEffects);

                // Check for Raid Omen and apply Omen-related bonuses
                if (player.hasEffect(Effects.BAD_OMEN)) {
                    addConditionalAttributes(player, omenAttributes, ARMOR_MODIFIERS_UUID[this.slot.getIndex()], "raid_omen_bonus");
                    addConditionalEffects(player, omenEffects);
                } else {
                    // Remove Omen attributes/effects if Bad Omen is gone
                    removeConditionalAttributes(player, omenAttributes, ARMOR_MODIFIERS_UUID[this.slot.getIndex()]);
                    removeConditionalEffects(player, omenEffects);
                }
            } else {
                // Partial or No Set: Remove all attributes and effects
                removeConditionalAttributes(player, raidAttributes, ARMOR_MODIFIERS_UUID[this.slot.getIndex()]);
                removeConditionalAttributes(player, omenAttributes, ARMOR_MODIFIERS_UUID[this.slot.getIndex()]);
                removeConditionalEffects(player, raidEffects);
                removeConditionalEffects(player, omenEffects);
            }
        }
    }


    // --- Helper Methods ---

    /**
     * Helper method to count the number of RaidArmor pieces worn by a LivingEntity.
     */
    private int getRaidArmorCount(LivingEntity entity) {
        int count = 0;
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
                if (entity.getItemBySlot(slot).getItem() instanceof RaidArmor) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Helper to add potion effects if they are not already present.
     */
    private void addConditionalEffects(LivingEntity entity, Map<Effect, Integer> effects) {
        for (Map.Entry<Effect, Integer> entry : effects.entrySet()) {
            Effect effect = entry.getKey();
            int amplifier = entry.getValue();

            // Use entity.getEffect(effect) safely
            EffectInstance existingEffect = entity.getEffect(effect);
            if (existingEffect == null || existingEffect.getAmplifier() < amplifier) {
                entity.addEffect(new EffectInstance(effect, 205, amplifier, true, true)); // 10 seconds + margin
            }
        }
    }

    /**
     * Helper to remove potion effects if they are present and do not originate from an external source.
     */
    private void removeConditionalEffects(LivingEntity entity, Map<Effect, Integer> effects) {
        // Effects are managed implicitly by stopping re-application in onArmorTick,
        // allowing the duration (205 ticks) to expire naturally.
    }

    /**
     * Helper to add attribute modifiers if they are not already present.
     * REFACTOR: Safely retrieves the Attribute instance ONCE to prevent NullPointerException
     * if the entity does not possess the attribute (which causes the crash).
     */
    private void addConditionalAttributes(LivingEntity entity, Multimap<Attribute, Double> attributes, UUID baseUUID, String namePrefix) {
        int i = 0;
        for (Map.Entry<Attribute, Double> entry : attributes.entries()) {
            Attribute attributeKey = entry.getKey();
            Double amount = entry.getValue();

            // Create a unique UUID for this attribute/modifier
            UUID attributeUUID = new UUID(baseUUID.getMostSignificantBits(), baseUUID.getLeastSignificantBits() + i);

            // *** FIX: Retrieve the attribute instance once and check for null safely ***
            ModifiableAttributeInstance instance = entity.getAttribute(attributeKey);

            if (instance != null) {
                // Check if the modifier is already present before attempting to add it
                if (instance.getModifier(attributeUUID) == null) {
                    AttributeModifier customModifier = new AttributeModifier(
                            attributeUUID,
                            namePrefix,
                            amount,
                            AttributeModifier.Operation.ADDITION
                    );
                    // Use the safe, non-null instance
                    instance.addTransientModifier(customModifier);
                }
            }
            i++;
        }
    }

    /**
     * Helper to remove attribute modifiers if they are present.
     * REFACTOR: Safely retrieves the Attribute instance ONCE to prevent NullPointerException
     * if the entity does not possess the attribute (which causes the crash).
     */
    private void removeConditionalAttributes(LivingEntity entity, Multimap<Attribute, Double> attributes, UUID baseUUID) {
        int i = 0;
        for (Map.Entry<Attribute, Double> entry : attributes.entries()) {
            Attribute attributeKey = entry.getKey();

            // Create a unique UUID for this attribute/modifier
            UUID attributeUUID = new UUID(baseUUID.getMostSignificantBits(), baseUUID.getLeastSignificantBits() + i);

            // *** FIX: Retrieve the attribute instance once and check for null safely ***
            ModifiableAttributeInstance instance = entity.getAttribute(attributeKey);

            if (instance != null) {
                // Check if the modifier is present before attempting to remove it
                if (instance.getModifier(attributeUUID) != null) {
                    // Use the safe, non-null instance
                    instance.removeModifier(attributeUUID);
                }
            }
            i++;
        }
    }
}
