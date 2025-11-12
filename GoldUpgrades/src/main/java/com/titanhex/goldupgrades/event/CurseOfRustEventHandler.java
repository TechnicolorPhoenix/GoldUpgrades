package com.titanhex.goldupgrades.event;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.effect.CurseOfRustEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = GoldUpgrades.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CurseOfRustEventHandler {


    // Base cooldowns (in ticks)
    private static final int COOLDOWN_STAGE_1_2 = 600; // 30 seconds
    private static final int COOLDOWN_STAGE_3_PLUS = 300; // 15 seconds

    private static final String NBT_TAG_ROOT = "rust_curse_data";
    private static final String NBT_KEY_COOLDOWN = "RustCurseCooldown";

    // NOTE: In a real mod, replace this with your ModEffects.CURSE_OF_RUST.get() reference.
    private static final CurseOfRustEffect CURSE_OF_RUST_EFFECT =
            (CurseOfRustEffect) ForgeRegistries.POTIONS.getValue(ResourceLocation.tryParse("goldupgrades:curse_of_rust"));

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Only run on the server side and at the end of the tick phase
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) {
            return;
        }

        PlayerEntity player = event.player;

        // 1. Check if the player has the curse effect and get the instance
        EffectInstance curseInstance = player.getEffect(CURSE_OF_RUST_EFFECT);

        if (curseInstance != null) {

            // Determine the current amplifier (Stage 1 = 0, Stage 2 = 1, Stage 3 = 2, etc.)
            int amplifier = curseInstance.getAmplifier();

            // Set required cooldown based on amplification level
            int requiredCooldown = amplifier >= 2 ? COOLDOWN_STAGE_3_PLUS : COOLDOWN_STAGE_1_2;

            // Get or create persistent NBT for the player to track the cooldown
            CompoundNBT modNBT = player.getPersistentData().getCompound(NBT_TAG_ROOT);
            int cooldown = modNBT.getInt(NBT_KEY_COOLDOWN);

            cooldown++; // Increment the tick counter

            if (cooldown >= requiredCooldown) {

                // --- Apply Durability Damage ---

                // Get the player's worn armor items (helmet, chest, legs, boots)
                NonNullList<ItemStack> armorItems = player.inventory.armor;

                for (ItemStack stack : armorItems) {
                    // Check if the item is not empty and is damageable (i.e., has durability)
                    if (!stack.isEmpty() && stack.isDamageableItem()) {

                        boolean canBreak = amplifier >= 1; // Stage 2+ allows breakage

                        if (canBreak) {
                            // Stage 2+ (Amplifier 1+): Damage normally, allowing the item to break
                            stack.hurt(1, player.getRandom(), null);
                        } else {
                            // Stage 1 (Amplifier 0): Damage is applied, but item must not break.
                            // We check if the item has at least 2 durability points left before damaging.
                            // The item breaks when damageValue is equal to or exceeds maxDamage.
                            // If damageValue < maxDamage - 1, the item has at least 2 hits left.
                            if (stack.getDamageValue() < stack.getMaxDamage() - 1) {
                                stack.hurt(1, player.getRandom(), null);
                            }
                        }
                    }
                }

                // Reset cooldown
                cooldown = 0;
            }

            // Save the updated cooldown back to NBT
            modNBT.putInt(NBT_KEY_COOLDOWN, cooldown);
            player.getPersistentData().put(NBT_TAG_ROOT, modNBT);
        }
    }
}
