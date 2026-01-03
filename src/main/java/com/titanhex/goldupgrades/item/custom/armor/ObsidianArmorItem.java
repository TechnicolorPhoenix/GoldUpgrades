package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.item.components.DynamicAttributeComponent;
import com.titanhex.goldupgrades.item.components.ObsidianToolComponent;
import com.titanhex.goldupgrades.item.custom.CustomAttributeArmor;
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

public class ObsidianArmorItem extends CustomAttributeArmor implements ILevelableItem, ILightInfluencedItem, IMoonPhaseInfluencedItem, IDayInfluencedItem, IArmorCooldown {

    private final DynamicAttributeComponent dynamicAttributeHelper;
    private final ObsidianToolComponent obsidianHelper;

    public ObsidianArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Multimap<Attribute, Double> attributeBonuses, Properties builderIn) {
        super(materialIn, slot, attributeBonuses, builderIn);
        dynamicAttributeHelper = new DynamicAttributeComponent(
            UUID.randomUUID(), slot, Attributes.ARMOR_TOUGHNESS,
            "ArmorModifier" + this.slot.getName()
        );
        this.obsidianHelper = new ObsidianToolComponent();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int inventorySlot, boolean isSelected) {
        if (world.isClientSide)
            return;

        calibrateLightLevel(stack, world, holdingEntity);
        changeMoonPhase(stack, world);

        boolean shouldRefresh = changeDay(stack, world);

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(this.slot) == stack;

        ModifiableAttributeInstance toughnessAttribute = livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS);

        if (isEquipped && shouldRefresh && holdingEntity instanceof LivingEntity && toughnessAttribute != null) {
            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(this.slot, stack);

            dynamicAttributeHelper.updateAttributes(livingEntity, newModifiers, toughnessAttribute);
        }

        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return super.getItemEnchantability(stack) + obsidianHelper.getItemEnchantability(stack);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));
        int lightLevel = ILightInfluencedItem.getLightLevel(stack);
        int itemLevel = getItemLevel();
        float toughnessBonus = (float) (15 - lightLevel) / 5;

        dynamicAttributeHelper.getAttributeModifiers(
                equipmentSlot, builder,
                () -> true,
                () -> (-0.5 + itemLevel) + toughnessBonus
        );

        return builder.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        obsidianHelper.appendMoonPhaseHoverText(worldIn, stack, tooltip);

        if (isNight(stack, worldIn))
            tooltip.add(new StringTextComponent("§aActive: Absorption Regeneration."));
        else
            tooltip.add(new StringTextComponent("§cInactive: Absorption Regeneration."));
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);

        if (world.isClientSide)
            return;

        int timer = getArmorCooldown(stack);
        int totalSetLevel = getTotalSetLevel(player);
        int itemLevel = getItemLevel();

        if (timer <= 0) {
            if (isNight(stack, world)) {
                float toAdd = 0.1F*itemLevel;
                float currentAbsorptionAmount = player.getAbsorptionAmount();
                final int MAX_CAP = (totalSetLevel+1)/2 * 2 + getMoonPhaseValue(stack, world) - 1;

                if (currentAbsorptionAmount < MAX_CAP) {
                    player.setAbsorptionAmount(Math.min(MAX_CAP, currentAbsorptionAmount + toAdd));
                }
            }

            setArmorCooldown(stack, (20*40) - totalSetLevel*20);
        } else
            setArmorCooldown(stack, timer - 1);
    }

    @Override
    public boolean isDayInfluenced() {
        return false;
    }
}
