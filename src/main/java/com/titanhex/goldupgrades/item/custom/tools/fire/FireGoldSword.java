package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.components.DynamicAttributeComponent;
import com.titanhex.goldupgrades.item.interfaces.*;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FireGoldSword extends SwordItem implements ILevelableItem, IIgnitableTool, ILightInfluencedItem, IDimensionInfluencedItem, IWeatherInfluencedItem, IDayInfluencedItem
{
    int burnTicks;
    int durabilityUse;
    DynamicAttributeComponent dynamicAttributeHandler;

    public FireGoldSword(IItemTier tier, int atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
        this.dynamicAttributeHandler = new DynamicAttributeComponent();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int inventorySlot, boolean isSelected) {
        calibrateLightLevel(stack, world, holdingEntity);

        if (world.isClientSide)
            return;

        changeDay(stack, world);
        boolean changeWeather = changeWeather(stack, world);
        boolean changeDimension = changeDimension(stack, world);

        LivingEntity livingEntity = (LivingEntity) holdingEntity;

        ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

        boolean shouldRefresh = false;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND) == stack;

        if (isEquipped && attackInstance != null)
            if (attackInstance.getModifier(dynamicAttributeHandler.dynamicUUID) != null)
                shouldRefresh = changeDimension || changeWeather;

        if (shouldRefresh && holdingEntity instanceof LivingEntity) {
            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);
            dynamicAttributeHandler.updateAttributes(livingEntity, newModifiers, attackInstance);
        }

        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        if (baseSpeed > 1.0F) {
            float bonusSpeed = IIgnitableTool.calculateBonusDestroySpeed(stack);

            float speedMultiplier = 1.0F + bonusSpeed;

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));
        boolean isDay = isDay(stack);

        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            if (getWeather(stack) == Weather.CLEAR || inValidDimension(stack)) {
                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        dynamicAttributeHandler.dynamicUUID,
                        "Weapon modifier",
                        (isDay ? 2 : 1) + (isClear(stack) ? IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack) : 0),
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }

        return builder.build();
   }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (!inValidDimension(stack, worldIn) && getWeather(stack) != Weather.CLEAR)
            tooltip.add(new StringTextComponent("§cInactive: Damage Bonus (Requires Clear Skies or Nether)"));


        IIgnitableTool.appendHoverText(stack, tooltip);
    }

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    @Override
    public void igniteEntity(LivingEntity target, ItemStack stack) {
        target.setSecondsOnFire(burnTicks);
    }

    /**
     * Handles the entity hit event (Left Click on Entity).
     */
    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        // 1. Call the interface method to apply the custom effect
        igniteEntity(target, stack);

        // Return true to indicate the attack was successful
        return true;
    }

    /**
     * Handles the block use event (Right Click).
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide) {
            return super.useOn(context);
        }
        PlayerEntity player = context.getPlayer();
        if (player == null)
            return super.useOn(context);

        return IIgnitableTool.useOn(context, null);
    }

    @Override
    public DimensionType primaryDimension() {
        return DimensionType.NETHER;
    }
}
