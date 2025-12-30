package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.item.components.DynamicAttributeComponent;
import com.titanhex.goldupgrades.item.interfaces.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class FireArmorItem extends ArmorItem implements IWeatherInfluencedItem, IDimensionInfluencedItem, IDayInfluencedItem, IArmorCooldown, ILevelableItem {
    protected float recoverAmount;
    protected float toughnessBonus;
    protected int perTickRecoverSpeed;

    private final DynamicAttributeComponent dynamicAttributeHelper;

    private static final UUID[] SUN_DAMAGE_MODIFIER = new UUID[]{
            UUID.randomUUID(), // BOOTS (Existing)
            UUID.randomUUID(), // LEGGINGS
            UUID.randomUUID(), // CHESTPLATE
            UUID.randomUUID()  // HELMET
    };

    public FireArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, float recoverAmount, int perTickRecoverSpeed, float toughnessBonus, Properties builderIn) {
        super(materialIn, slot, builderIn);
        this.recoverAmount = recoverAmount;
        this.perTickRecoverSpeed = perTickRecoverSpeed;
        this.toughnessBonus = toughnessBonus;
        this.dynamicAttributeHelper = new DynamicAttributeComponent(
                SUN_DAMAGE_MODIFIER[slot.getIndex()], slot,
                Attributes.ARMOR_TOUGHNESS, "Armor Modifier");
    }

    public double getBonusToughness(ItemStack stack, @Nullable World world) {
        boolean inNether = inValidDimension(stack, world);
        boolean inClear = isClear(stack, world);

        if (inClear || inNether)
            return this.toughnessBonus + (double) IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack)/2;

        return 0;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));
        double bonusToughness = getBonusToughness(stack, null);
        double baseToughness = (-0.5) + (getItemLevel() * 0.5D );

        dynamicAttributeHelper.getAttributeModifiers(
                equipmentSlot, builder,
                () -> bonusToughness > 0,
                () -> bonusToughness
            );

        return builder.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        double bonusDamage = getBonusToughness(stack, worldIn);
        boolean isDay = isDay(stack);

        if (bonusDamage == 0) {
            tooltip.add(new StringTextComponent("§cInactive: Toughness Bonus (Requires Clear Skies or Nether)"));
        }

        if (isDay) {
            tooltip.add(new StringTextComponent("§aActive: +" + this.recoverAmount + " Health per " + (this.perTickRecoverSpeed / 20) + " seconds.)"));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Regen Bonus (Requires Day)"));
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, holdingEntity, itemSlot, isSelected);

        if (world.isClientSide) return;

        boolean dimensionChanged = changeDimension(stack, world);
        boolean weatherChanged = changeWeather(stack, world);

        boolean environmentChanged = false;

        if (!(holdingEntity instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;

        boolean isEquipped = ItemStack.matches(livingEntity.getItemBySlot(this.slot), stack);

        ModifiableAttributeInstance toughnessInstance = livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS);

        if (isEquipped && toughnessInstance != null)
            if (toughnessInstance.getModifier(dynamicAttributeHelper.dynamicUUID) != null)
                environmentChanged = dimensionChanged || weatherChanged;

        if (environmentChanged && holdingEntity instanceof LivingEntity) {
            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(this.slot, stack);

            dynamicAttributeHelper.updateAttributes(livingEntity, newModifiers, toughnessInstance);
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);

        if (world.isClientSide)
            return;

        int timer = getArmorCooldown(stack);
        if (timer <= 0) {
            if (isDay(stack)) {
                player.heal(this.recoverAmount);
            }
            setArmorCooldown(stack, this.perTickRecoverSpeed);
        } else {
            reduceArmorCooldown(stack);
        }
    }

    @Override
    public DimensionType primaryDimension() {
        return DimensionType.NETHER;
    }
}
