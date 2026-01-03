package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.GoldUpgradesConfig;
import com.titanhex.goldupgrades.item.components.DynamicAttributeComponent;
import com.titanhex.goldupgrades.item.custom.CustomAttributeEffectArmor;
import com.titanhex.goldupgrades.item.interfaces.IArmorCooldown;
import com.titanhex.goldupgrades.item.interfaces.ILevelableItem;
import com.titanhex.goldupgrades.item.interfaces.IWaterInfluencedItem;
import com.titanhex.goldupgrades.item.interfaces.IWeatherInfluencedItem;
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
    int drainFactor = GoldUpgradesConfig.SEA_ARMOR_DURABILITY_DRAIN_FACTOR.get();

    private final DynamicAttributeComponent waterArmorHelper;
    private final DynamicAttributeComponent rainSpeedHelper;
    private final Random RANDOM = new Random();

    public SeaArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Map<Effect, Integer> effects, Multimap<Attribute, Double> attributeBonuses, Properties builderIn) {
        super(materialIn, slot, effects, attributeBonuses, builderIn);
        this.waterArmorHelper = new DynamicAttributeComponent(
                UUID.randomUUID(), slot, Attributes.ARMOR,
                "WaterArmor" + this.slot.getName()
        );
        this.rainSpeedHelper = new DynamicAttributeComponent(
                UUID.randomUUID(), slot, Attributes.MOVEMENT_SPEED,
                "RainSpeed" + this.slot.getName()
        );
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));
        boolean submerged = getIsSubmerged(stack);
        boolean inRain = getIsInRain(stack);

        waterArmorHelper.getAttributeModifiers(
                equipmentSlot, builder,
                () -> inRain || submerged,
                () -> 1D
        );
        rainSpeedHelper.getAttributeModifiers(
                equipmentSlot, builder,
                () -> inRain,
                () -> 0.05D,
                AttributeModifier.Operation.MULTIPLY_BASE
        );

        return builder.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        boolean inRain = getIsInRain(stack);
        boolean submerged = getIsSubmerged(stack);

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
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, holdingEntity, itemSlot, isSelected);

        if (world.isClientSide) return;
        if (!(holdingEntity instanceof LivingEntity)) return;

        boolean submergedChanged = changeSubmerged(stack, holdingEntity);
        boolean inRainChanged = changeIsInRain(stack, holdingEntity);

        boolean environmentalStateChanged = submergedChanged || inRainChanged;

        boolean lastWeatherClear = isClear(stack);
        boolean weatherChanged = changeWeather(stack, world);

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(this.slot) == stack;

        if (weatherChanged) {
            if (isRaining(stack, world) && lastWeatherClear) {
                float weatherBoosterAmount = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);
                livingEntity.setAbsorptionAmount(
                        Math.min(
                                16F + weatherBoosterAmount,
                                livingEntity.getAbsorptionAmount() + weatherBoosterAmount + getItemLevel()
                        )
                );
            }
        }

        if (isEquipped && environmentalStateChanged) {

            ModifiableAttributeInstance armorInstance = livingEntity.getAttribute(Attributes.ARMOR);
            ModifiableAttributeInstance speedInstance = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);

            if (armorInstance != null && speedInstance != null) {
                Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(this.slot, stack);
                waterArmorHelper.updateAttributes(livingEntity, newModifiers, armorInstance);
                rainSpeedHelper.updateAttributes(livingEntity, newModifiers, speedInstance);
            }
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);

        if (world.isClientSide) return;

        if (getIsSubmerged(stack)) {
            int timer = getArmorCooldown(stack);

            if (timer > 0) {
                reduceArmorCooldown(stack);
                return;
            }

            int currentOxygen = player.getAirSupply();
            int maxOxygen = player.getMaxAirSupply();
            int oxygenDifference = maxOxygen - currentOxygen;

            int toRestore = Math.min(oxygenDifference, GoldUpgradesConfig.SEA_ARMOR_OXYGEN_RECOVERY.get());

            int oxygenThreshold = Math.min(GoldUpgradesConfig.SEA_ARMOR_RECOVER_KICKIN.get(), player.getMaxAirSupply());
            if (currentOxygen <= oxygenThreshold && timer == 0) {
                stack.hurtAndBreak(toRestore / this.drainFactor, player, (p) -> p.broadcastBreakEvent(Objects.requireNonNull(stack.getEquipmentSlot())));
                player.setAirSupply(currentOxygen + toRestore);
                setArmorCooldown(stack, GoldUpgradesConfig.SEA_ARMOR_RECOVER_COOLDOWN.get());
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
