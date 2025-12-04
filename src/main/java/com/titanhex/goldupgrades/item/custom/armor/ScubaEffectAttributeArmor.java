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
    protected boolean isSubmerged = false;
    protected boolean isInRain = false;
    protected Weather weather = Weather.CLEAR;
    protected DimensionType dimension = DimensionType.OVERWORLD;

    private static final UUID[] RAIN_SPEED_MODIFIER_UUID = new UUID[]{
            UUID.fromString("6d7b5d12-6804-45e0-9e62-421f421f421f"), // BOOTS (Existing)
            UUID.fromString("87e5c942-8356-4c4f-96a9-4672f7d9c02d"), // LEGGINGS
            UUID.fromString("8c12a02b-9e4a-4e2a-8c5e-8e5f24213d21"), // CHESTPLATE
            UUID.fromString("1f4a9b5f-50d7-4c3e-a7f4-3d7c5b1b4a2e")  // HELMET
    };
    private static final UUID[] WATER_ARMOR_MODIFIER_UUID = new UUID[]{
            UUID.fromString("6d8b6c38-1456-37c0-9b62-421f421f421d"), // BOOTS (Existing)
            UUID.fromString("b9a5f1e8-6e7e-40d0-8b6a-9a0f0d2b7c6c"), // LEGGINGS
            UUID.fromString("730a9e1d-4f5d-4f3b-8c1a-7e0f8b1c4d2e"), // CHESTPLATE
            UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d")  // HELMET
    };

    public ScubaEffectAttributeArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Map<Effect, Integer> effects, Multimap<Attribute, Double> attributeBonuses, int drainFactor, Properties builderIn) {
        super(materialIn, slot, effects, attributeBonuses, builderIn);
        this.drainFactor = drainFactor;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));

        if (equipmentSlot == this.slot) {
            // Check the synchronized NBT state
            if (this.isInRain || this.isSubmerged)
                builder.put(Attributes.ARMOR, new AttributeModifier(
                        WATER_ARMOR_MODIFIER_UUID[this.slot.getIndex()],
                        "Armor Water Modifier " + this.slot.getName(),
                        1F,
                        AttributeModifier.Operation.ADDITION
                ));
            if (this.isInRain)
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

        if (isInRain) {
            tooltip.add(new StringTextComponent("§aActive: Speed bonus +5%."));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Rain required for Speed bonus."));
        }

        if (isInRain || isSubmerged) {
            tooltip.add(new StringTextComponent("§aActive: Armor bonus + 1."));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Rain required for Armor bonus."));
        }

        tooltip.add(new StringTextComponent("§eAbsorption hearts when weather becomes rainy."));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int unknownInt, boolean unknownConditional) {
        if (world.isClientSide) return;

        Weather currentWeather = Weather.getCurrentWeather(world);
        boolean isInRainOrWater = holdingEntity.isInWaterOrRain();
        boolean isSubmerged = holdingEntity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
        boolean isInRain = isInRainOrWater && !isSubmerged;

        boolean shouldRefresh = false;

        if (this.weather != currentWeather)
        {
            this.weather = currentWeather;

            if (holdingEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) holdingEntity;
                ItemStack equippedStack = livingEntity.getItemBySlot(this.slot);

                boolean isEquipped = livingEntity.getItemBySlot(this.slot) == stack;
                if (!isEquipped) {

                }

                if (equippedStack == stack && currentWeather == Weather.RAIN)
                    livingEntity.setAbsorptionAmount(6F);
            }
            shouldRefresh = true;
        }
        if (isInRain != this.isInRain || isSubmerged != this.isSubmerged) {
            if (isInRainOrWater && (!this.isInRain && !this.isSubmerged)) {
                world.playSound(null, holdingEntity.blockPosition(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            if (isInRainOrWater) {
                this.isSubmerged = isSubmerged;
                this.isInRain = isInRain;
            } else {
                this.isSubmerged = false;
                this.isInRain = false;
            }
            shouldRefresh = true;
        }

        if (shouldRefresh) {
            if (shouldRefresh && holdingEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) holdingEntity;

                Multimap<Attribute, AttributeModifier> newModifiers = stack.getAttributeModifiers(this.slot);

                for (Attribute attribute : newModifiers.keySet()) {
                    ModifiableAttributeInstance instance = livingEntity.getAttribute(attribute);
                    if (instance != null) {
                        instance.removeModifier(RAIN_SPEED_MODIFIER_UUID[this.slot.getIndex()]);
                        instance.removeModifier(WATER_ARMOR_MODIFIER_UUID[this.slot.getIndex()]);
                    }
                }

                for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                    ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                    if (instance != null) {
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

        if (isSubmerged) {
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
