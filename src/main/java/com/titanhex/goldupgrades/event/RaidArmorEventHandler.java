package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.item.custom.armor.RaidArmor;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;


@Mod.EventBusSubscriber(modid = "goldupgrades")
public class RaidArmorEventHandler {
    // Removed static final EffectInstance declarations. They will be instantiated safely inside the event method.

    /**
     * Helper method to count the number of RaidArmor pieces worn by a LivingEntity.
     */
    private static int getRaidArmorCount(LivingEntity entity) {
        int armorCount = 0;
        // Check if the entity is null before proceeding
        if (entity == null) {
            return 0;
        }

        // Iterate over standard armor slots
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
                if (entity.getItemBySlot(slot).getItem() instanceof RaidArmor) {
                    armorCount++;
                }
            }
        }
        return armorCount;
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().tickCount % 20 != 0) {
            return;
        }

        if (event.getEntityLiving() instanceof CreatureEntity && event.getEntityLiving() instanceof IAngerable) {
            CreatureEntity creatureEntity = (CreatureEntity) event.getEntityLiving();

            int armorPieceCount = getRaidArmorCount(creatureEntity);

            if (armorPieceCount > 0) {

                EffectInstance villagerHealthBoost = new EffectInstance(Effects.HEALTH_BOOST,
                        30*20*60,
                        armorPieceCount/2,
                        false,
                        false
                );

                creatureEntity.addEffect(villagerHealthBoost);
            }
            if (armorPieceCount > 1) {

                EffectInstance villagerMovespeed = new EffectInstance(Effects.MOVEMENT_SPEED,
                        21,
                        armorPieceCount-2,
                        false,
                        false
                );

                creatureEntity.addEffect(villagerMovespeed);
            }
            if (armorPieceCount > 2) {

                EffectInstance villagerDamageResistance = new EffectInstance(Effects.DAMAGE_RESISTANCE,
                        21,
                        armorPieceCount-2,
                        false,
                        false
                );

                creatureEntity.addEffect(villagerDamageResistance);
            }
            if (armorPieceCount > 3) {
                EffectInstance villagerAbsorption = new EffectInstance(Effects.ABSORPTION,
                        21,
                        0,
                        false,
                        false
                );

                creatureEntity.addEffect(villagerAbsorption);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamageEvent(LivingDamageEvent event) {
        // Prevent event logic on the client side for this server-side event
        if (event.getEntity().level.isClientSide) {
            return;
        }

        LivingEntity victim = event.getEntityLiving();
        LivingEntity attacker = (event.getSource().getEntity() instanceof LivingEntity)
                ? (LivingEntity) event.getSource().getEntity()
                : null;

        int victimArmorCount = getRaidArmorCount(victim);
        int attackerArmorCount = getRaidArmorCount(attacker);

        // --- 1. Raid Armor Damage Increase Effect ---

        if (attacker != null && attackerArmorCount > 0) {
            // Check if the attacker is damaging a target that should receive bonus damage (Illagers, Villagers)
            if (victim instanceof AbstractIllagerEntity || victim instanceof AgeableEntity) {

                // Damage increase is 10% per armor piece (max 40% for full set)
                float increasePerPiece = RaidArmor.damageIncreasePerPiece;
                float damageIncreasePercentage = increasePerPiece * attackerArmorCount;

                // Calculate new damage: damage * (1 + increase_percentage)
                float newDamage = event.getAmount() * (1.0f + damageIncreasePercentage);
                event.setAmount(newDamage);
            }
        }

        if ((attacker instanceof AbstractIllagerEntity || attacker instanceof AgeableEntity) && victimArmorCount > 0) {

            // Damage increase is 10% per armor piece (max 40% for full set)
            float increasePerPiece = RaidArmor.damageReductionPerPiece;
            float damageDecreasePercentage = increasePerPiece * victimArmorCount;

            // Calculate new damage: damage * (1 + increase_percentage)
            float newDamage = event.getAmount() * (1.0f - damageDecreasePercentage);
            event.setAmount(newDamage);
        }

        // --- 2. Raid Armor Death Prevention Effect (4-piece set bonus) ---

        // ONLY proceed if the damage is lethal (or would be lethal)
        if (victim.getHealth() - event.getAmount() <= 0.0F) {
            // Check if the entity is wearing the full Raid Armor set
            if (victimArmorCount == 4) {

                // Define effects inside the method for safe registry access
                final EffectInstance absorption = new EffectInstance(Effects.ABSORPTION,
                        600, // 30 seconds
                        0,
                        true,
                        true
                );
                final EffectInstance regen = new EffectInstance(Effects.REGENERATION,
                        100, // 5 seconds
                        1, // Regeneration II
                        true,
                        true
                );

                // 3. Prevent the damage (and thus the death)
                event.setCanceled(true);

                victim.addEffect(regen);
                victim.addEffect(absorption);

                for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                    // 1.16.5 compatible check for armor slots
                    if (slot == EquipmentSlotType.HEAD || slot == EquipmentSlotType.CHEST || slot == EquipmentSlotType.LEGS || slot == EquipmentSlotType.FEET) {
                        if (victim.getItemBySlot(slot).getItem() instanceof RaidArmor) {
                            victim.getItemBySlot(slot).hurtAndBreak(550, victim, (e) -> e.broadcastBreakEvent(slot));
                        }
                    }
                }
            }
        }
    }
}