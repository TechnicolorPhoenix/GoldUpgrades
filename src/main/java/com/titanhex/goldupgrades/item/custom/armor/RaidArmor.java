package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.UUID;

public class RaidArmor extends DyeableArmorItem {

    private static final UUID[] ARMOR_MODIFIERS_UUID = new UUID[]{
            UUID.fromString("845DB3C2-CD78-40FB-8A8C-6BC55D1F6E1B"), // Boots
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), // Leggings
            UUID.fromString("9F3D476D-C118-4544-8365-648469C6A53A"), // Chestplate
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380C1EBA")  // Helmet
    };

    // Constant UUIDs for conditional attributes to ensure they are added/removed correctly
    private static final UUID CONDITIONAL_OMEN_UUID = UUID.fromString("629A6A12-B67D-4C9D-A3D8-04D540A61B5F");
    private static final UUID CONDITIONAL_RAID_UUID = UUID.fromString("B2488E07-6B18-410F-9883-936495A9678A");

    public static final float damageIncreasePerPiece = 0.2F;
    public static final float damageReductionPerPiece = 0.1F;

    public int level;

    Map<Effect, Integer> omenEffects;
    Multimap<Attribute, Double> omenAttributes;
    Map<Effect, Integer> raidEffects;
    Multimap<Attribute, Double> raidAttributes;

    public RaidArmor(IArmorMaterial materialIn,
                     EquipmentSlotType slot,
                     Multimap<Attribute, Double> omenAttributes,
                     Map<Effect, Integer> omenEffects,
                     Multimap<Attribute, Double> raidAttributes,
                     Map<Effect, Integer> raidEffects,
                     int level,
                     Properties builderIn) {
        super(materialIn, slot, builderIn);
        this.omenEffects = omenEffects;
        this.omenAttributes = omenAttributes;
        this.raidEffects = raidEffects;
        this.raidAttributes = raidAttributes;
        this.level = level;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {

        // --- 1. Efficiency Check: Only run the heavy logic every 20 ticks (1 second) ---
        if (player.tickCount % 20 != 0) {
            return;
        }

        // --- 2. Server-Side Check (Mandatory for Raids/Effects) ---
        if (world.isClientSide || !(world instanceof ServerWorld)) {
            return;
        }

        // --- 3. Count the number of RaidArmor pieces worn by the player ---
        int armorCount = 0;
        // Iterate over only the armor slots (HEAD, CHEST, LEGS, FEET) using 1.16.5 checks
        for (EquipmentSlotType equipmentSlot : EquipmentSlotType.values()) {
            if (equipmentSlot == EquipmentSlotType.HEAD || equipmentSlot == EquipmentSlotType.CHEST || equipmentSlot == EquipmentSlotType.LEGS || equipmentSlot == EquipmentSlotType.FEET) {
                if (player.getItemBySlot(equipmentSlot).getItem() instanceof RaidArmor) {
                    armorCount++;
                }
            }
        }

        ServerWorld serverWorld = (ServerWorld) player.level;
        BlockPos playerPos = player.blockPosition();

        // --- 4. Define the state for effects and attributes ---
        Raid currentRaid = serverWorld.getRaidAt(playerPos);
        boolean isRaiding = currentRaid != null && currentRaid.isActive();
        boolean hasBadOmen = player.hasEffect(Effects.BAD_OMEN);

        if (isRaiding) {
            // A raid is active in the area where the player is standing.
            // You can get more details about the raid state:
            // currentRaid.isBetweenWaves();
            // currentRaid.isLossOrVictory();

            for (Map.Entry<Effect, Integer> entry : raidEffects.entrySet()) {
                Effect effect = entry.getKey();

                // Duration 21 is just over 1 second, ensuring it gets refreshed every tick.
                player.addEffect(new EffectInstance(
                        effect,
                        21,       // Duration (20 ticks = 1 second)
                        armorCount, // The stacked effect level
                        false,    // isAmbient
                        false     // showParticles
                ));
            }
        }

        if (hasBadOmen) {
            for (Map.Entry<Effect, Integer> entry : omenEffects.entrySet()) {
                Effect effect = entry.getKey();

                // Duration 21 is just over 1 second, ensuring it gets refreshed every tick.
                player.addEffect(new EffectInstance(
                        effect,
                        21,       // Duration (20 ticks = 1 second)
                        armorCount, // The stacked effect level
                        false,    // isAmbient
                        false     // showParticles
                ));
            }
        }

        // --- 6. Conditional Attribute Application (Ensuring one-time application) ---

        // RAID ATTRIBUTES (Active during raid)
        if (isRaiding) {
            addConditionalAttributes(player, this.raidAttributes, CONDITIONAL_RAID_UUID, "Raid Armor Raid Bonus");
        } else {
            removeConditionalAttributes(player, this.raidAttributes, CONDITIONAL_RAID_UUID);
        }

        // OMEN ATTRIBUTES (Active with Bad Omen)
        if (hasBadOmen) {
            addConditionalAttributes(player, this.omenAttributes, CONDITIONAL_OMEN_UUID, "Raid Armor Omen Bonus");
        } else {
            removeConditionalAttributes(player, this.omenAttributes, CONDITIONAL_OMEN_UUID);
        }
    }



    /**
     * Helper to apply attribute modifiers if they are not already present.
     */
    private void addConditionalAttributes(LivingEntity entity, Multimap<Attribute, Double> attributes, UUID baseUUID, String namePrefix) {
        int i = 0;
        for (Map.Entry<Attribute, Double> entry : attributes.entries()) {
            Attribute attribute = entry.getKey();
            double amount = entry.getValue();

            // Create a unique UUID for this attribute/modifier
            UUID attributeUUID = new UUID(baseUUID.getMostSignificantBits(), baseUUID.getLeastSignificantBits() + i);

            // Check if the modifier is already present before attempting to add it
            if (entity.getAttribute(attribute) != null && entity.getAttribute(attribute).getModifier(attributeUUID) == null) {
                AttributeModifier customModifier = new AttributeModifier(
                        attributeUUID,
                        namePrefix,
                        amount,
                        AttributeModifier.Operation.ADDITION
                );
                entity.getAttribute(attribute).addTransientModifier(customModifier);
            }
            i++;
        }
    }

    /**
     * Helper to remove attribute modifiers if they are present.
     */
    private void removeConditionalAttributes(LivingEntity entity, Multimap<Attribute, Double> attributes, UUID baseUUID) {
        int i = 0;
        for (Map.Entry<Attribute, Double> entry : attributes.entries()) {
            Attribute attribute = entry.getKey();

            // Create a unique UUID for this attribute/modifier
            UUID attributeUUID = new UUID(baseUUID.getMostSignificantBits(), baseUUID.getLeastSignificantBits() + i);

            // Check if the modifier is present before attempting to remove it
            if (entity.getAttribute(attribute) != null && entity.getAttribute(attribute).getModifier(attributeUUID) != null) {
                entity.getAttribute(attribute).removeModifier(attributeUUID);
            }
            i++;
        }
    }
}
