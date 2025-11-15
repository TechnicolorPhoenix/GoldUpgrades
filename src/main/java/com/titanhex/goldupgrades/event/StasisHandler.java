package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.data.StasisData;
import com.titanhex.goldupgrades.effect.ModEffects;
import com.titanhex.goldupgrades.effect.StasisEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoldUpgrades.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StasisHandler {

    // Highest priority ensures we intercept the event before other mods (like armor or resistance) change the damage amount.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();

        // 1. Check if the entity has the Stasis effect
        if (entity.hasEffect(ModEffects.STASIS.get())) {

            EffectInstance stasisEffectInstance = entity.getEffect(ModEffects.STASIS.get());

            float damage = event.getAmount() * (1.00F - 0.1F * stasisEffectInstance.getAmplifier());
            DamageSource source = event.getSource();

            // 2. Accumulate the damage and the source
            StasisData.accumulateDamage(entity, damage, source);

            // 3. Negate the current damage event (crucial!)
            event.setCanceled(true);

            if (!entity.level.isClientSide) {
                String sourceName = source.getMsgId();
                if (source.getEntity() != null) {
                    sourceName = source.getEntity().getDisplayName().getString();
                }
                ((PlayerEntity)entity).displayClientMessage(
                        new StringTextComponent("§6STASIS: Damage (" + String.format("%.1f", damage) + " from " + sourceName + ") absorbed."),
                        true // Display on action bar
                );
            }
        }
    }

    // Intercept healing events too
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntityLiving();

        // 1. Check if the entity has the Stasis effect
        if (entity.hasEffect(ModEffects.STASIS.get())) {

            EffectInstance stasisEffectInstance = entity.getEffect(ModEffects.STASIS.get());

            float healing = event.getAmount() * (1.00F + 0.1F * stasisEffectInstance.getAmplifier());

            // 2. Accumulate the healing
            StasisData.accumulateHealing(entity, healing);

            // 3. Negate the current healing event (crucial!)
            event.setCanceled(true);

            if (entity instanceof PlayerEntity) {
                ((PlayerEntity)entity).displayClientMessage(
                        new StringTextComponent("§6STASIS: Healing (" + String.format("%.1f", healing) + ") absorbed."),
                        true // Display on action bar
                );
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();

        // Only run on the logical server side
        if (entity.level.isClientSide) {
            return;
        }

        // Optimization: Only check the status once every 5 ticks to save performance.
        if (entity.tickCount % 5 != 0) {
            return;
        }

        CompoundNBT nbt = entity.getPersistentData().getCompound(StasisData.NBT_ROOT);

        // Check 1: Does the entity have Stasis data stored?
        if (nbt.contains(StasisData.NBT_KEY_DAMAGE)) {

            // Check 2: Does the entity NO LONGER have the Stasis effect?
            // This condition means the effect was forcibly removed (e.g., milk/command)
            // OR the duration expired.
            if (!entity.hasEffect(ModEffects.STASIS.get())) {

                // CRITICAL: Apply the final damage/healing and clean up the NBT.
                StasisData.applyAccumulatedResults(entity);
            }
            // If they have the data AND the effect, do nothing, stasis is still active.
        }
    }
}