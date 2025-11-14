package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.item.custom.CustomAttributeArmor;
import com.titanhex.goldupgrades.item.custom.CustomAttributeEffectArmor;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;

public class ScubaEffectAttributeArmor extends CustomAttributeEffectArmor {
    int drainFactor;
    int cooldown = 0;

    private static final UUID RAIN_SPEED_MODIFIER_UUID =
            UUID.fromString("6d7b5d12-6804-45e0-9e62-421f421f421f");

    public ScubaEffectAttributeArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Map<Effect, Integer> effects, Multimap<Attribute, Double> attributeBonuses, int drainFactor, Properties builderIn) {
        super(materialIn, slot, effects, attributeBonuses, builderIn);
        this.drainFactor = drainFactor;
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);

        if (player.hasEffect(Effects.BAD_OMEN)) {

        }

        if (cooldown > 0) {
            this.cooldown = Math.max(0, this.cooldown - 1);
            return;
        }

        int currentOxygen = player.getAirSupply();
        int maxOxygen = player.getMaxAirSupply();
        int oxygenDifference = maxOxygen - currentOxygen;

        int toRestore = Math.min(oxygenDifference, 30);
// 2. Get the player's Movement Speed attribute
        ModifiableAttributeInstance speedAttribute =
                player.getAttribute(Attributes.MOVEMENT_SPEED);

        if (speedAttribute != null) {
            AttributeModifier modifier = speedAttribute.getModifier(RAIN_SPEED_MODIFIER_UUID);

            if (player.isInWaterOrRain() && !player.isEyeInFluid(net.minecraft.tags.FluidTags.WATER)) {
                if (modifier == null) {
                    AttributeModifier newModifier = new AttributeModifier(
                            RAIN_SPEED_MODIFIER_UUID,
                            "Rain Speed Bonus", // A descriptive name
                            0.1D,
                            AttributeModifier.Operation.MULTIPLY_TOTAL // Use MULTIPLY_TOTAL for percentage bonuses
                    );

                    // This applies the modifier and handles synchronization
                    speedAttribute.addTransientModifier(newModifier);

                    // Play sound once when the bonus is activated
                    world.playSound(null, player.blockPosition(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.cooldown = 20;
                }
            } else if (modifier != null) {
                speedAttribute.removeModifier(RAIN_SPEED_MODIFIER_UUID);
            }
        }

        if (!player.isSprinting() && currentOxygen <= 60 && this.cooldown == 0) {
            stack.hurtAndBreak(toRestore / this.drainFactor, player, (p) -> p.broadcastBreakEvent(stack.getEquipmentSlot()));
            player.setAirSupply(currentOxygen + toRestore);
            this.cooldown = 120;
        }
    }
}
