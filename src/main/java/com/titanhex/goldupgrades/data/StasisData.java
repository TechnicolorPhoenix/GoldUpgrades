package com.titanhex.goldupgrades.data;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.helper.DamageSourceHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity; // Import needed for displayClientMessage cast
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.event.entity.living.LivingDamageEvent; // Required for DamageSource access

public class StasisData {
    public static final String NBT_ROOT = GoldUpgrades.MOD_ID + "_stasis_data";
    public static final String NBT_KEY_DAMAGE = "AccumulatedDamage";
    public static final String NBT_KEY_HEALING = "AccumulatedHealing";
    public static final String NBT_KEY_LAST_SOURCE = "LastDamageSource"; // Stores serialized DamageSource

    // --- Data Accessors ---

    public static float getDamage(LivingEntity entity) {
        return entity.getPersistentData().getCompound(NBT_ROOT).getFloat(NBT_KEY_DAMAGE);
    }

    public static float getHealing(LivingEntity entity) {
        return entity.getPersistentData().getCompound(NBT_ROOT).getFloat(NBT_KEY_HEALING);
    }

    // --- Accumulation Methods ---

    public static void accumulateDamage(LivingEntity entity, float damage, DamageSource source) {
        CompoundNBT nbt = entity.getPersistentData().getCompound(NBT_ROOT);
        float currentDamage = nbt.getFloat(NBT_KEY_DAMAGE);
        nbt.putFloat(NBT_KEY_DAMAGE, currentDamage + damage);

        // Save the current damage source
        nbt.putString(NBT_KEY_LAST_SOURCE, DamageSourceHelper.serializeSource(source));
        entity.getPersistentData().put(NBT_ROOT, nbt);
    }

    public static void accumulateHealing(LivingEntity entity, float healing) {
        CompoundNBT nbt = entity.getPersistentData().getCompound(NBT_ROOT);
        float currentHealing = nbt.getFloat(NBT_KEY_HEALING);
        nbt.putFloat(NBT_KEY_HEALING, currentHealing + healing);
        entity.getPersistentData().put(NBT_ROOT, nbt);
    }

    // --- Final Application Method ---

    public static void applyAccumulatedResults(LivingEntity entity) {
        if (EffectiveSide.get() == LogicalSide.CLIENT) return;

        CompoundNBT nbt = entity.getPersistentData().getCompound(NBT_ROOT);
        float damage = nbt.getFloat(NBT_KEY_DAMAGE);
        float healing = nbt.getFloat(NBT_KEY_HEALING);
        String sourceString = nbt.getString(NBT_KEY_LAST_SOURCE);

        float finalResult = damage - healing;

        // Reset the NBT data immediately
        entity.getPersistentData().remove(NBT_ROOT);

        if (finalResult > 0) {
            // Apply net damage
            DamageSource finalSource = DamageSourceHelper.deserializeSource(sourceString);

            // Fallback for an un-serialized or unknown source
            if (finalSource == null) {
                finalSource = DamageSource.GENERIC;
            }

            // Apply the damage using the last source (or a fallback)
            entity.hurt(finalSource, finalResult);

            // Notify player (Corrected messaging)
            if (entity instanceof PlayerEntity) {
                ((PlayerEntity)entity).displayClientMessage(new StringTextComponent("§cStasis Ended: Took " + String.format("%.1f", finalResult) + " damage!"), false);
            }
        } else if (finalResult < 0) {
            // Apply net healing
            entity.heal(Math.abs(finalResult));

            // Notify player (Corrected messaging)
            if (entity instanceof PlayerEntity) {
                ((PlayerEntity)entity).displayClientMessage(new StringTextComponent("§aStasis Ended: Healed " + String.format("%.1f", Math.abs(finalResult)) + " health!"), false);
            }
        } else {
            // No net change
            if (entity instanceof PlayerEntity) {
                ((PlayerEntity)entity).displayClientMessage(new StringTextComponent("§bStasis Ended: Net result was zero."), false);
            }
        }
    }
}