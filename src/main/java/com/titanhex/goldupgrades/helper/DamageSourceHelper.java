package com.titanhex.goldupgrades.helper;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class DamageSourceHelper {

    // This is a simplified serialization for the common cases needed here.
    public static String serializeSource(DamageSource source) {
        if (source.msgId.equals("generic")) {
            return "generic";
        }

        // Store the source message ID and the entity's UUID, if applicable
        if (source.getEntity() != null) {
            return source.msgId + ":" + source.getEntity().getUUID().toString();
        }
        return source.msgId;
    }

    // This is a simplified deserialization
    public static DamageSource deserializeSource(String serialized) {
        if (serialized == null || serialized.isEmpty() || serialized.equals("generic")) {
            return DamageSource.GENERIC;
        }

        String[] parts = serialized.split(":");
        String msgId = parts[0];

        if (parts.length > 1) {
            // Try to find the entity by UUID
            try {
                java.util.UUID uuid = java.util.UUID.fromString(parts[1]);

                // --- FIX: Iterating over worlds without using the problematic stream() method ---
                Entity foundEntity = null;
                for (ServerWorld world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
                    foundEntity = world.getEntity(uuid);
                    if (foundEntity != null) {
                        break;
                    }
                }
                Optional<Entity> entity = Optional.ofNullable(foundEntity);

                if (entity.isPresent()) {
                    if (msgId.contains("projectile")) {
                        // Simplified check for projectile damage
                        return new DamageSource(msgId).setProjectile();
                    }
                    // We return generic entity damage for simplicity here.
                    return DamageSource.mobAttack((LivingEntity)entity.get());
                }
            } catch (IllegalArgumentException e) {
                // Invalid UUID, fall through to basic source
            }
        }

        // Return a basic DamageSource based on the ID if entity lookup fails
        return new DamageSource(msgId);
    }
}