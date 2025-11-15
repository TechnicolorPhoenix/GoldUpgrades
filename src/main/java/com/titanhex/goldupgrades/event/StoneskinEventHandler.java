package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoldUpgrades.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StoneskinEventHandler {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntityLiving() instanceof LivingEntity) {
            LivingEntity target = event.getEntityLiving();

            if (target.hasEffect(ModEffects.STONESKIN.get())) {
                EffectInstance stoneskinEffectInstance = target.getEffect(ModEffects.STONESKIN.get());

                float originalDamage = event.getAmount();

                if (stoneskinEffectInstance != null) {
                    int stoneskinAmplifierLevel = stoneskinEffectInstance.getAmplifier();
                    float newDamage = originalDamage * (0.70F - stoneskinAmplifierLevel*0.05F);
                    event.setAmount(newDamage);

                    int currentDuration = stoneskinEffectInstance.getDuration();
                    int newDuration = currentDuration - (3 * 20);

                    if (newDuration <= 0) {
                        target.removeEffect(ModEffects.STONESKIN.get());
                    } else {
                        target.removeEffect(ModEffects.STONESKIN.get());

                        EffectInstance newInstance = new EffectInstance(
                                stoneskinEffectInstance.getEffect(),   // The effect itself
                                newDuration,            // The new, reduced duration
                                stoneskinEffectInstance.getAmplifier(),// Keep the same amplifier
                                stoneskinEffectInstance.isAmbient(),   // Keep ambient status
                                stoneskinEffectInstance.isVisible()    // Keep visibility status
                        );

                        target.addEffect(newInstance);
                    }
                }
            }
        }
    }
}
