package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.item.custom.armor.RaidArmor;
import net.minecraft.entity.AgeableEntity;
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
    private static final EffectInstance absorption = new EffectInstance(Effects.ABSORPTION,
            600,
            0,
            true,
            true
    );
    private static final EffectInstance regen = new EffectInstance(Effects.ABSORPTION,
            100,
            1,
            true,
            true
    );

    /**
     * Helper method to count the number of RaidArmor pieces worn by a LivingEntity.
     */
    private static int getRaidArmorCount(LivingEntity entity) {
        int armorCount = 0;
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            // 1.16.5 compatible check for armor slots
            if (slot == EquipmentSlotType.HEAD || slot == EquipmentSlotType.CHEST || slot == EquipmentSlotType.LEGS || slot == EquipmentSlotType.FEET) {
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
        if (event.getEntityLiving() instanceof LivingEntity) {
            LivingEntity villagerEntity = (LivingEntity) event.getEntityLiving();

            int armorPieceCount = getRaidArmorCount(villagerEntity);

            if (armorPieceCount > 0) {

                EffectInstance villagerHealthBoost = new EffectInstance(Effects.HEALTH_BOOST,
                        21,
                        armorPieceCount,
                        false,
                        false
                );

                villagerEntity.addEffect(villagerHealthBoost);
            }
            if (armorPieceCount > 1) {

                EffectInstance villagerMovespeed = new EffectInstance(Effects.MOVEMENT_SPEED,
                        21,
                        armorPieceCount,
                        false,
                        false
                );

                villagerEntity.addEffect(villagerMovespeed);
            }
            if (armorPieceCount > 2) {

                EffectInstance villagerDamageResistance = new EffectInstance(Effects.DAMAGE_RESISTANCE,
                        21,
                        armorPieceCount,
                        false,
                        false
                );

                villagerEntity.addEffect(villagerDamageResistance);
            }
            if (armorPieceCount > 3) {
                EffectInstance villagerAbsorption = new EffectInstance(Effects.ABSORPTION,
                        21,
                        0,
                        false,
                        false
                );

                villagerEntity.addEffect(villagerAbsorption);
            }
        }
    }
    /**
     * Event subscriber for when a LivingEntity takes damage.
     * This handles both damage reduction (if the victim is wearing the armor)
     * and damage increase (if the attacker is wearing the armor) against Illagers.
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        // Only run on the server side where damage calculations are authoritative
        if (event.getEntityLiving().level.isClientSide) return;

        LivingEntity victim = event.getEntityLiving();

        // --- 1. DAMAGE REDUCTION (Victim wearing armor) ---
        int victimArmorCount = getRaidArmorCount(victim);

        if (victimArmorCount > 0) {
            // Check if the source of damage is an Illager (e.g., Vindicator, Pillager, Evoker)
            if (event.getSource().getEntity() instanceof AbstractIllagerEntity) {
                // Reduces damage taken by 8% per armor piece (max 32% for full set)
                float reductionPerPiece = RaidArmor.damageReductionPerPiece;
                float damageReductionPercentage = reductionPerPiece * victimArmorCount;

                // Calculate new damage: damage * (1 - reduction_percentage)
                float newDamage = event.getAmount() * (1.0f - damageReductionPercentage);
                event.setAmount(newDamage);
            }
        }

        // --- 2. DAMAGE INCREASE (Attacker wearing armor) ---
        // Get the entity that is dealing the damage, assuming it's a LivingEntity
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;

        if (attacker != null) {
            int attackerArmorCount = getRaidArmorCount(attacker);

            if (attackerArmorCount > 0) {
                // Check if the victim is an Illager
                if (victim instanceof AbstractIllagerEntity) {
                    // Increases damage dealt by 10% per armor piece (max 40% for full set)
                    float increasePerPiece = RaidArmor.damageIncreasePerPiece;
                    float damageIncreasePercentage = increasePerPiece * attackerArmorCount;

                    // Calculate new damage: damage * (1 + increase_percentage)
                    float newDamage = event.getAmount() * (1.0f + damageIncreasePercentage);
                    event.setAmount(newDamage);
                }
            }
        }

        if (event.getEntityLiving().getHealth() - event.getAmount() <= 0.0F) {
            // 2. Check if the entity is wearing your Raid Armor
            if (victimArmorCount == 4) {

                // 3. Prevent the damage (and thus the death)
                event.setCanceled(true);

                victim.addEffect(regen);
                victim.addEffect(absorption);

                for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                    // 1.16.5 compatible check for armor slots
                    if (slot == EquipmentSlotType.HEAD || slot == EquipmentSlotType.CHEST || slot == EquipmentSlotType.LEGS || slot == EquipmentSlotType.FEET) {
                        if (victim.getItemBySlot(slot).getItem() instanceof RaidArmor) {
                            victim.getItemBySlot(slot).getStack().hurtAndBreak(550, victim, (e) -> e.broadcastBreakEvent(slot));
                        }
                    }
                }
            }
        }
    }
}
