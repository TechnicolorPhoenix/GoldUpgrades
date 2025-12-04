package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.custom.CustomAttributeArmor;
import com.titanhex.goldupgrades.item.custom.CustomAttributeEffectArmor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScubaEffectAttributeArmor extends CustomAttributeEffectArmor {
    int drainFactor;
    int cooldown = 0;
    private static final String ARMOR_WEATHER = "ArmorLastWeather";
    private static final String NBT_SUBMERGED = "ArmorSubmerged";
    private static final String NBT_IN_RAIN = "ArmorInRain";

    private static final UUID[] WATER_ARMOR_MODIFIER_UUID = new UUID[]{
            UUID.fromString("6d8b6c38-1456-37c0-9b62-421f421f421d"), // BOOTS (Existing)
            UUID.fromString("b9a5f1e8-6e7e-40d0-8b6a-9a0f0d2b7c6c"), // LEGGINGS
            UUID.fromString("730a9e1d-4f5d-4f3b-8c1a-7e0f8b1c4d2e"), // CHESTPLATE
            UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d")  // HELMET
    };

    private static final UUID[] RAIN_SPEED_MODIFIER_UUID = new UUID[]{
            UUID.fromString("6d7b5d12-6804-45e0-9e62-421f421f421f"), // BOOTS (Existing)
            UUID.fromString("87e5c942-8356-4c4f-96a9-4672f7d9c02d"), // LEGGINGS
            UUID.fromString("8c12a02b-9e4a-4e2a-8c5e-8e5f24213d21"), // CHESTPLATE
            UUID.fromString("1f4a9b5f-50d7-4c3e-a7f4-3d7c5b1b4a2e")  // HELMET
    };

    public ScubaEffectAttributeArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Map<Effect, Integer> effects, Multimap<Attribute, Double> attributeBonuses, int drainFactor, Properties builderIn) {
        super(materialIn, slot, effects, attributeBonuses, builderIn);
        this.drainFactor = drainFactor;
    }

    private boolean getSubmerged(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_SUBMERGED);
    }
    private void setSubmerged(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_SUBMERGED, value);
    }

    private boolean getInRain(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IN_RAIN);
    }
    private void setInRain(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IN_RAIN, value);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));

        // Guard 1: Only modify attributes if this item is in its correct slot.
        if (equipmentSlot == this.slot) {
            boolean submerged = this.getSubmerged(stack); // Read from NBT
            boolean inRain = this.getInRain(stack);       // Read from NBT

            if (inRain || submerged)
                builder.put(Attributes.ARMOR, new AttributeModifier(
                        WATER_ARMOR_MODIFIER_UUID[this.slot.getIndex()],
                        "Armor Water Modifier " + this.slot.getName(),
                        1F,
                        AttributeModifier.Operation.ADDITION
                ));
            if (inRain)
                builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(
                        RAIN_SPEED_MODIFIER_UUID[this.slot.getIndex()],
                        "Armor Speed Modifier" + this.slot.getName(),
                        0.05,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
        }

        return builder.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        boolean inRain = this.getInRain(stack);
        boolean submerged = this.getSubmerged(stack);

        if (inRain) {
            tooltip.add(new StringTextComponent("§aActive: Speed bonus +5%."));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Rain required for Speed bonus."));
        }

        if (inRain || submerged) {
            tooltip.add(new StringTextComponent("§aActive: Armor bonus + 1."));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Rain required for Armor bonus."));
        }

        tooltip.add(new StringTextComponent("§eAbsorption hearts when weather becomes rainy."));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int unknownInt, boolean unknownConditional) {
        if (world.isClientSide) return;

        if (!(holdingEntity instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(this.slot) == stack;

        boolean isSubmergedNow = holdingEntity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
        boolean isInRainOrWaterNow = holdingEntity.isInWaterOrRain();
        boolean isInRainNow = isInRainOrWaterNow && !isSubmergedNow;

        boolean oldSubmerged = this.getSubmerged(stack);
        boolean oldInRain = this.getInRain(stack);

        boolean shouldRefresh = false;

        if (isEquipped) {
            // If equipped, update the item's NBT state to reflect the current world state
            if (isInRainNow != oldInRain || isSubmergedNow != oldSubmerged) {
                setInRain(stack, isInRainNow);
                setSubmerged(stack, isSubmergedNow);
                shouldRefresh = true;

                // Sound is only played if transitioning to an active state
                if (isInRainNow || isSubmergedNow) {
                    world.playSound(null, holdingEntity.blockPosition(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }

            // --- Absorption Logic (Needs its own NBT tracking if using the class field 'weather') ---
            // For simplicity, let's keep the weather tracking on the ItemStack's NBT too
            Weather currentWeather = Weather.getCurrentWeather(world);
            if (stack.getOrCreateTag().contains(ARMOR_WEATHER)) {
                String lastWeatherStr = stack.getOrCreateTag().getString(ARMOR_WEATHER);
                Weather lastWeather = Weather.valueOf(lastWeatherStr);

                if (currentWeather != lastWeather) {
                    if (currentWeather == Weather.RAIN) {
                        livingEntity.setAbsorptionAmount(6F);
                    }
                    stack.getOrCreateTag().putString(ARMOR_WEATHER, currentWeather.name());
                }
            } else {
                stack.getOrCreateTag().putString(ARMOR_WEATHER, currentWeather.name());
            }
        } else {
            // Crucial: If unequipped, force the NBT state to INACTIVE.
            // This ensures the attribute modifiers map will be empty for dynamic bonuses.
            if (oldInRain || oldSubmerged) {
                setInRain(stack, false);
                setSubmerged(stack, false);
                // When an item is unequipped, the game engine usually handles attribute removal,
                // but forcing the state to false is a good guard.
                shouldRefresh = true;
            }
        }

        // --- Attribute Refresh Logic (ONLY RUN IF EQUIPPED STATE CHANGED) ---
        // This is the core attribute fix, modified from the previous suggestion.
        if (shouldRefresh && holdingEntity instanceof LivingEntity) {

            // 1. Get the Attribute Map for modification (using the new NBT state)
            // We get the modifiers based on the *new* state now stored in the ItemStack NBT
            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(this.slot, stack);

            // 2. Remove all potential dynamic modifiers (always remove before applying)
            ModifiableAttributeInstance armorInstance = livingEntity.getAttribute(Attributes.ARMOR);
            if (armorInstance != null) {
                armorInstance.removeModifier(WATER_ARMOR_MODIFIER_UUID[this.slot.getIndex()]);
            }
            ModifiableAttributeInstance speedInstance = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedInstance != null) {
                speedInstance.removeModifier(RAIN_SPEED_MODIFIER_UUID[this.slot.getIndex()]);
            }

            // 3. Re-apply the attributes based on the new NBT state (if the item is equipped)
            if (isEquipped) {
                for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                    ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                    if (instance != null) {
                        // Check if it's one of the dynamic modifiers to re-add
                        if (entry.getValue().getId().equals(RAIN_SPEED_MODIFIER_UUID[this.slot.getIndex()]) ||
                                entry.getValue().getId().equals(WATER_ARMOR_MODIFIER_UUID[this.slot.getIndex()]))
                        {
                            instance.addTransientModifier(entry.getValue());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);

        if (world.isClientSide) return;

        if (cooldown > 0) {
            this.cooldown = Math.max(0, this.cooldown - 1);
            return;
        }

        if (getSubmerged(stack)) {
            int currentOxygen = player.getAirSupply();
            int maxOxygen = player.getMaxAirSupply();
            int oxygenDifference = maxOxygen - currentOxygen;

            int toRestore = Math.min(oxygenDifference, 30);

            if (!player.isSprinting() && currentOxygen <= 60 && this.cooldown == 0) {
                stack.hurtAndBreak(toRestore / this.drainFactor, player, (p) -> p.broadcastBreakEvent(stack.getEquipmentSlot()));
                player.setAirSupply(currentOxygen + toRestore);
                this.cooldown = 120;
            }
        }
    }
}
