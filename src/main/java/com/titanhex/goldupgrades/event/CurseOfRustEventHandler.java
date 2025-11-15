package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.effect.ModEffects;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumSet;

@Mod.EventBusSubscriber(modid = GoldUpgrades.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CurseOfRustEventHandler {

    // Define all possible slots where the curse can apply (Tools, Weapons, Armor)
    private static final EnumSet<EquipmentSlotType> CHECK_SLOTS = EnumSet.of(
            EquipmentSlotType.MAINHAND,
            EquipmentSlotType.OFFHAND,
            EquipmentSlotType.FEET,
            EquipmentSlotType.LEGS,
            EquipmentSlotType.CHEST,
            EquipmentSlotType.HEAD
    );

    // Base cooldowns (in ticks)
    private static final int COOLDOWN_STAGE_2 = 600;
    private static final int COOLDOWN_STAGE_3 = 300;

    // NBT Keys for the ENCHANTMENT (stored on the item ItemStack's NBT)
    private static final String NBT_TAG_ROOT = "rust_curse_data";
    private static final String NBT_KEY_COOLDOWN = "tick_cooldown";

    // <-- NEW: NBT Keys for the MOB EFFECT (stored on the Player Entity's NBT)
    private static final String NBT_EFFECT_ROOT = GoldUpgrades.MOD_ID + "_rust_effect_data";
    private static final String NBT_EFFECT_COOLDOWN = "effect_cooldown";

    /**
     * Helper to safely damage the item based on the curse's rules.
     * @param player The player wearing/holding the item.
     * @param stack The item stack to damage.
     * @param rustLevel The level of the Curse of Rust enchantment (1, 2, or 3).
     */
    static void damageItem(PlayerEntity player, ItemStack stack, int rustLevel) {
        if (stack.isEmpty() || !stack.isDamageableItem()) {

            return;
        }

        // Levels 2 and 3: Damage normally, allowing the item to break
        if (rustLevel >= 2) {
            stack.hurt(1, player.getRandom(), null);
        }
        // Level 1: Damage is applied, BUT we prevent the item from fully breaking (stays at 1 durability).
        else if (rustLevel == 1) {
            // Check if damage is about to destroy the item (damage + 1 >= maxDamage).
            // If the damage is about to break it, just set damage to maxDamage - 1 (1 durability left).
            if (stack.getDamageValue() + 1 >= stack.getMaxDamage()) {
                stack.setDamageValue(stack.getMaxDamage() - 1);
            } else {
                stack.hurt(1, player.getRandom(), null);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        // Only run on the server side and at the end of the tick phase
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) {
            return;
        }

        PlayerEntity player = event.player;

        // =========================================================================
        // 1. MOB EFFECT CHECK (Targets the player entity)
        // =========================================================================
        EffectInstance effectInstance = player.getEffect(ModEffects.CURSE_OF_RUST.get());

        if (effectInstance != null) {
            // Mob Effect Level is Amplifier + 1
            int effectRustLevel = effectInstance.getAmplifier() + 1;

            // Get the player's persistent NBT data
            CompoundNBT playerNbt = player.getPersistentData().getCompound(NBT_EFFECT_ROOT);

            player.getPersistentData().put(NBT_EFFECT_ROOT, playerNbt); // Ensure the root tag exists

            int cooldown = playerNbt.getInt(NBT_EFFECT_COOLDOWN);
            cooldown++;

            int requiredCooldown = effectRustLevel >= 3 ? COOLDOWN_STAGE_3 : COOLDOWN_STAGE_2;

            for (EquipmentSlotType slot : CHECK_SLOTS) {
                ItemStack stackToDamage = player.getItemBySlot(slot); // Affect Main Hand item

                if (cooldown % requiredCooldown/12 == 0) {
                    // Time to damage the item
                    damageItem(player, stackToDamage, effectRustLevel);
                }
            }
            // Save the updated cooldown back to Player NBT
            playerNbt.putInt(NBT_EFFECT_COOLDOWN, cooldown);
        }

        // =========================================================================
        // 2. ENCHANTMENT CHECK (Targets equipped item NBT)
        // =========================================================================

        // Iterate through all defined item slots for the curse enchantment
        for (EquipmentSlotType slot : CHECK_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);

            // Check if the item has the Curse of Rust enchantment
            int rustLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.CURSE_OF_RUST.get(), stack);

            if (rustLevel > 0) {
                // If the item has the enchantment, track the cooldown using the item's NBT
                CompoundNBT nbt = stack.getOrCreateTagElement(NBT_TAG_ROOT);
                int cooldown = nbt.getInt(NBT_KEY_COOLDOWN);

                // Determine the current cooldown based on the level
                int requiredCooldown = rustLevel >= 3 ? COOLDOWN_STAGE_3 : COOLDOWN_STAGE_2;

                cooldown++; // Increment the tick counter

                if (cooldown >= requiredCooldown) {
                    // Time to damage the item
                    damageItem(player, stack, rustLevel);

                    // Reset cooldown
                    cooldown = 0;
                }

                // Save the updated cooldown back to NBT
                nbt.putInt(NBT_KEY_COOLDOWN, cooldown);
            }
        }
    }
}
