package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.Util;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = GoldUpgrades.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DeathDelayHandler {

    private static final String NBT_ROOT = GoldUpgrades.MOD_ID + "_death_delay_data";
    private static final String NBT_KEY_DEATH_TIMER = "DeathTimer";
    private static final String NBT_KEY_DEATH_SOURCE = "DeathSource"; // Stores serialized DamageSource message ID
    private static final String NBT_KEY_DEATH_SOURCE_COMPONENT = "DeathSourceComponent"; // Stores the full death message

    /**
     * Priority HIGH: Intercept the damage before the armor/resistance calculations.
     * This handles both the initial fatal blow and subsequent hits during the countdown.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().level.isClientSide) return;

        LivingEntity entity = event.getEntityLiving();

        CompoundNBT persistentData = entity.getPersistentData();
        CompoundNBT nbt = persistentData.getCompound(NBT_ROOT);

        // --- FIX 1: Prevent damage if the death delay countdown is already active ---
        if (nbt.contains(NBT_KEY_DEATH_TIMER)) {
            // Timer is running. Cancel ALL further damage events until the countdown finishes.
            event.setCanceled(true);

            // Provide feedback to the player on the action bar
            if (entity instanceof PlayerEntity) {
                ((PlayerEntity)entity).displayClientMessage(
                        new StringTextComponent("§4DEATH IMMINENT: Damage is ignored while the countdown is active."),
                        true // Display on action bar
                );
            }
            return; // Stop processing this event
        }
        // --- End Fix 1 ---

        // Check if the entity has the initial Death Delay effect (only runs once per life)
        EffectInstance deathDelayInstance = entity.getEffect(ModEffects.DEATH_DELAY.get());

        if (deathDelayInstance != null) {
            float damage = event.getAmount();
            float finalHealth = entity.getHealth() - damage;

            // Check if this damage is fatal (or already dead)
            if (finalHealth <= 0) {

                // Get the maximum delay (2 seconds per amplifier level, converted to ticks)
                // Amplifier N -> (N+1) * 2 seconds * 20 Ticks/sec
                int amplifier = deathDelayInstance.getAmplifier();
                int deathDelaySeconds = 2 * (amplifier + 1);
                int deathDelayTicks = deathDelaySeconds * 20;

                // 1. Store the death data in persistent NBT
                // (nbt is already obtained above)
                nbt.putInt(NBT_KEY_DEATH_TIMER, deathDelayTicks);

                // Store the damage source for the final death message
                DamageSource source = event.getSource();
                nbt.putString(NBT_KEY_DEATH_SOURCE, source.getMsgId());

                // Create and store the exact death message (e.g., "Player was shot by Skeleton")
                ITextComponent deathMessage = source.getLocalizedDeathMessage(entity);
                nbt.putString(NBT_KEY_DEATH_SOURCE_COMPONENT, ITextComponent.Serializer.toJson(deathMessage));

                persistentData.put(NBT_ROOT, nbt); // Save the NBT back to persistentData

                // 2. Set health to 1 to prevent immediate death
                entity.setHealth(1.0f);

                // 3. Remove the Death Delay Effect (so it doesn't trigger again)
                entity.removeEffect(ModEffects.DEATH_DELAY.get());

                // 4. Cancel the current damage event (crucial!)
                event.setCanceled(true);

                // 5. Notify the player of their impending doom
                if (entity instanceof PlayerEntity) {
                    ((PlayerEntity)entity).displayClientMessage(
                            new StringTextComponent("§4DEATH IMMINENT: Death has been delayed by " + deathDelaySeconds + " seconds."),
                            false // Display in chat
                    );
                }
            }
        }
    }

    /**
     * Server tick event handles the countdown timer for the delayed death.
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) {
            return;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        for (PlayerEntity player : server.getPlayerList().getPlayers()) {

            CompoundNBT persistentData = player.getPersistentData();
            CompoundNBT nbt = persistentData.getCompound(NBT_ROOT);

            if (nbt.contains(NBT_KEY_DEATH_TIMER)) {

                int timer = nbt.getInt(NBT_KEY_DEATH_TIMER);
                timer--; // Countdown

                if (timer <= 0) {
                    // --- Death Time is Up! ---

                    // 1. Get the final death source/message
                    String messageJson = nbt.getString(NBT_KEY_DEATH_SOURCE_COMPONENT);
                    DamageSource finalSource = DamageSource.GENERIC;
                    if (nbt.contains(NBT_KEY_DEATH_SOURCE)) {
                        finalSource = new DamageSource(nbt.getString(NBT_KEY_DEATH_SOURCE));
                    }

                    // 2. Execute final death
                    player.setHealth(0.0f);
                    player.die(finalSource);

                    // This block confirms the player is in the process of dying/dead, which is robust.
                    if (player.isDeadOrDying()) {

                        // 3. Set the custom death message in the chat
                        if (!messageJson.isEmpty()) {
                            ITextComponent deathMessage = ITextComponent.Serializer.fromJson(messageJson);

                            server.getPlayerList().broadcastMessage(
                                    deathMessage,
                                    ChatType.SYSTEM,
                                    Util.NIL_UUID
                            );
                        }

                        persistentData.remove(NBT_ROOT);
                    }

                } else {
                    // --- Display Countdown Warning ---
                    nbt.putInt(NBT_KEY_DEATH_TIMER, timer);

                    if (timer % 20 == 0) { // Update message every second
                        int secondsLeft = timer / 20;
                        ((PlayerEntity)player).displayClientMessage(
                                new StringTextComponent("§4DEATH IMMINENT: " + secondsLeft + " seconds remaining."),
                                true
                        );
                    }

                    persistentData.put(NBT_ROOT, nbt);
                }
                // NO CODE HERE! The previous bug was the persistentData.put(...) call
                // being outside the 'if/else' but inside the main NBT check,
                // which re-added the NBT after it was removed.
            }
        }
    }
}