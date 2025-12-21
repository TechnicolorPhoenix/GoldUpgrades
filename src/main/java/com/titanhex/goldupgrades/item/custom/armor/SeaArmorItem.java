package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.custom.CustomAttributeEffectArmor;
import com.titanhex.goldupgrades.item.custom.inter.IArmorCooldown;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWaterInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class SeaArmorItem extends CustomAttributeEffectArmor implements IWeatherInfluencedItem, IWaterInfluencedItem, ILevelableItem, IArmorCooldown {
    int drainFactor = 15;
    int itemLevel;

    private static final UUID[] WATER_ARMOR_MODIFIER_UUID = new UUID[]{
            UUID.randomUUID(), // BOOTS (Existing)
            UUID.randomUUID(), // LEGGINGS
            UUID.randomUUID(), // CHESTPLATE
            UUID.randomUUID()  // HELMET
    };

    private static final UUID[] RAIN_SPEED_MODIFIER_UUID = new UUID[]{
            UUID.randomUUID(), // BOOTS (Existing)
            UUID.randomUUID(), // LEGGINGS
            UUID.randomUUID(), // CHESTPLATE
            UUID.randomUUID()  // HELMET
    };
    private final Random RANDOM = new Random();

    public SeaArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Map<Effect, Integer> effects, Multimap<Attribute, Double> attributeBonuses, Properties builderIn) {
        super(materialIn, slot, effects, attributeBonuses, builderIn);
        this.itemLevel = getItemLevel();
    }

    private boolean getSubmerged(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IS_SUBMERGED);
    }
    private void setSubmerged(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IS_SUBMERGED, value);
    }

    private boolean getInRain(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IS_IN_RAIN);
    }
    private void setInRain(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IS_IN_RAIN, value);
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        boolean inRain = this.getInRain(stack);
        boolean submerged = this.getSubmerged(stack);

        if (inRain) {
            tooltip.add(new StringTextComponent("§aActive: Speed bonus +5%."));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Rain required for Speed bonus."));
        }

        if (!inRain && !submerged) {
            tooltip.add(new StringTextComponent("§cInactive: Rain required for Armor bonus."));
        }

        tooltip.add(new StringTextComponent("§eAbsorption hearts when weather becomes rainy."));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int unknownInt, boolean unknownConditional) {
        super.inventoryTick(stack, world, holdingEntity, unknownInt, unknownConditional);

        if (world.isClientSide) return;
        if (!(holdingEntity instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(this.slot) == stack;

        boolean currentSubmerged = holdingEntity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
        boolean inWaterOrRain = holdingEntity.isInWaterOrRain();
        boolean currentInRain = inWaterOrRain && !currentSubmerged;

        boolean oldSubmerged = this.getSubmerged(stack);
        boolean oldInRain = this.getInRain(stack);

        boolean environmentalStateChanged = oldInRain != currentInRain || currentSubmerged != oldSubmerged;

        if (environmentalStateChanged) {
            setInRain(stack, currentInRain);
            setSubmerged(stack, currentSubmerged);

            if (currentInRain || currentSubmerged) {
                world.playSound(null, holdingEntity.blockPosition(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }

        Weather currentWeather = Weather.getCurrentWeather(world);
        Weather lastWeather = getWeather(stack);

        if (currentWeather != lastWeather) {
            if (currentWeather == Weather.RAINING && lastWeather == Weather.CLEAR) {
                float weatherBoosterAmount = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);
                livingEntity.setAbsorptionAmount(
                        Math.min(
                                16F + weatherBoosterAmount,
                                livingEntity.getAbsorptionAmount() + weatherBoosterAmount + itemLevel
                        )
                );
            }
            setWeather(stack, currentWeather);
        }

        if (isEquipped && environmentalStateChanged) {

            ModifiableAttributeInstance armorInstance = livingEntity.getAttribute(Attributes.ARMOR);
            ModifiableAttributeInstance speedInstance = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);

            if (armorInstance != null && speedInstance != null) {
                Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(this.slot, stack);

                int slotIdx = this.slot.getIndex();
                armorInstance.removeModifier(WATER_ARMOR_MODIFIER_UUID[slotIdx]);
                speedInstance.removeModifier(RAIN_SPEED_MODIFIER_UUID[slotIdx]);

                for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                    ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                    if (instance != null) {
                        if (entry.getValue().getId().equals(RAIN_SPEED_MODIFIER_UUID[slotIdx]) ||
                                entry.getValue().getId().equals(WATER_ARMOR_MODIFIER_UUID[slotIdx])) {
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

        if (getSubmerged(stack)) {
            int timer = getArmorCooldown(stack);

            if (timer > 0) {
                reduceArmorCooldown(stack);
                return;
            }

            int currentOxygen = player.getAirSupply();
            int maxOxygen = player.getMaxAirSupply();
            int oxygenDifference = maxOxygen - currentOxygen;

            int toRestore = Math.min(oxygenDifference, 30);

            if (currentOxygen <= 60 && timer == 0) {
                stack.hurtAndBreak(toRestore / this.drainFactor, player, (p) -> p.broadcastBreakEvent(Objects.requireNonNull(stack.getEquipmentSlot())));
                player.setAirSupply(currentOxygen + toRestore);
                setArmorCooldown(stack, 120);
            }
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (getIsSubmerged(stack) && hasWaterDiverEnchantment(stack)){
            int lowestValue = RANDOM.nextInt(2);
            amount = Math.max(lowestValue, amount - getWaterDiverEnchantmentLevel(stack));
        }
        return super.damageItem(stack, amount, entity, onBroken);
    }
}
