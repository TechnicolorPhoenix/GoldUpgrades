package com.titanhex.goldupgrades.item.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraft.potion.Effect;
// Required for iterating over the player's armor inventory
import net.minecraft.util.NonNullList;
// Required for accumulating the effects
import java.util.HashMap;
import java.util.Map;

public class CustomEffectArmor extends ArmorItem {

    private final Map<Effect, Integer> effects;

    public CustomEffectArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Map<Effect, Integer> effects, Properties builderIn) {
        super(materialIn, slot, builderIn);
        // This map stores the base effects and amplifiers granted by this single piece of armor.
        this.effects = ImmutableMap.copyOf(effects);
    }

    /**
     * A helper method to aggregate all effects and their total amplifiers
     * from all worn CustomEffectArmor pieces on the player.
     * * The core stacking logic happens here by using the Map.merge function.
     */
    private static Map<Effect, Integer> getWornEffects(PlayerEntity player) {
        // Use a HashMap to store and sum the amplifiers for each unique effect.
        Map<Effect, Integer> combinedEffects = new HashMap<>();

        // Get all items in the armor slots (HEAD, CHEST, LEGS, FEET)
        NonNullList<ItemStack> armorInventory = player.inventory.armor;

        for (ItemStack armorStack : armorInventory) {
            // 1. Check if the item in the slot is an instance of our custom armor
            if (armorStack.getItem() instanceof CustomEffectArmor) {
                CustomEffectArmor customArmor = (CustomEffectArmor) armorStack.getItem();

                // 2. Iterate over the base effects of the current armor piece
                for (Map.Entry<Effect, Integer> entry : customArmor.effects.entrySet()) {
                    Effect effect = entry.getKey();
                    int amplifier = entry.getValue();

                    // 3. Accumulate the amplifier. If the effect is already present,
                    // add the new amplifier to the existing one using Integer::sum.
                    // This is how the stacking (bumping up the level) occurs.
                    combinedEffects.merge(effect, amplifier, Integer::sum);
                }
            }
        }

        return combinedEffects;
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        // We only want the computationally heavier aggregation logic to run once per tick
        // on the server side. We ensure this by only running the check when the HEAD piece
        // is the item currently calling onArmorTick.
        if (!world.isClientSide && this.slot == EquipmentSlotType.HEAD && !player.isCrouching()) {

            Map<Effect, Integer> effectsToApply = getWornEffects(player);

            if (!effectsToApply.isEmpty()) {
                for (Map.Entry<Effect, Integer> entry : effectsToApply.entrySet()) {
                    Effect effect = entry.getKey();
                    // This is the combined amplifier from all worn pieces.
                    int totalAmplifier = entry.getValue();

                    // Duration 21 is just over 1 second, ensuring it gets refreshed every tick.
                    player.addEffect(new EffectInstance(
                            effect,
                            21,       // Duration (20 ticks = 1 second)
                            totalAmplifier, // The stacked effect level
                            false,    // isAmbient
                            false     // showParticles
                    ));
                }
            }
        }
    }
}
