package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoldUpgrades.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CurseOfRustEventHandler {


    // Base cooldowns (in ticks)
    private static final int COOLDOWN_STAGE_2 = 600; // 30 seconds (Level 2)
    private static final int COOLDOWN_STAGE_3 = 300; // 15 seconds (Level 3+)

    private static final String NBT_TAG_ROOT = "rust_curse_data";

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

        // Level 1: No breaking (damage only if max durability is not reached)
        if (rustLevel == 1) {
            // Check if the item has at least 2 durability points left before damaging.
            // If damageValue < maxDamage - 1, the item has at least 2 hits left.
            if (stack.getDamageValue() < stack.getMaxDamage() - 1) {
                // Damage by 1, null is fine for the random/source arguments here
                stack.hurt(1, player.getRandom(), null);
            }
            // Levels 2 and 3: Damage normally, allowing the item to break
        } else if (rustLevel >= 2) {
            stack.hurt(1, player.getRandom(), null);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        // Only run on the server side and at the end of the tick phase
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) {
            return;
        }

        PlayerEntity player = event.player;

        // Iterate through ALL slots that the enchantment can be placed on
        for (EquipmentSlotType slot : ModEnchantments.ALL_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);

            // Check if the item in the slot has the Curse of Rust
            int rustLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.CURSE_OF_RUST.get(), stack);

            if (rustLevel > 0) {
                // If the item has the enchantment, track the cooldown using the item's NBT
                CompoundNBT nbt = stack.getOrCreateTagElement(NBT_TAG_ROOT);
                int cooldown = nbt.getInt(NBT_TAG_ROOT); // Cooldown is stored on the item's NBT

                // Determine the current cooldown based on the level
                int requiredCooldown = rustLevel >= 3 ? COOLDOWN_STAGE_3 : COOLDOWN_STAGE_2;

                // Level 1 uses the same cooldown as Level 2 (30 seconds), but damage is handled differently.
                if (rustLevel == 1) {
                    requiredCooldown = COOLDOWN_STAGE_2;
                }

                cooldown++; // Increment the tick counter

                if (cooldown >= requiredCooldown) {
                    // Time to damage the item
                    damageItem(player, stack, rustLevel);

                    // Reset cooldown
                    cooldown = 0;
                }

                // Save the updated cooldown back to NBT
                nbt.putInt(NBT_TAG_ROOT, cooldown);
                // stack.setTag(nbt); // Not strictly required as getOrCreateTagElement modifies it directly, but good for clarity
            }
        }
    }
}
