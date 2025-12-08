package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.MoonPhase;
import com.titanhex.goldupgrades.item.custom.CustomAttributeArmor;
import com.titanhex.goldupgrades.item.custom.inter.*;
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
import java.util.Map;
import java.util.UUID;

public class ObsidianArmorItem extends CustomAttributeArmor implements ILevelableItem, ILightInfluencedItem, IMoonPhaseInfluencedItem, IDayInfluencedItem {

    protected final int itemLevel;
    private final UUID[] LIGHT_LEVEL_TOUGHNESS_UUID = new UUID[]{
            UUID.randomUUID(), // BOOTS
            UUID.randomUUID(), // LEGGINGS
            UUID.randomUUID(), // CHESTPLATE
            UUID.randomUUID()  // HELMET
    };

    public ObsidianArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Multimap<Attribute, Double> attributeBonuses, int itemLevel, Properties builderIn) {
        super(materialIn, slot, attributeBonuses, builderIn);
        this.itemLevel = itemLevel;
    }

    @Override
    public int getItemLevel() {
        return this.itemLevel;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int uInt, boolean uBoolean) {
        if (world.isClientSide)
            return;

        int slotIndex = this.slot.getIndex();
        LivingEntity livingEntity = (LivingEntity) holdingEntity;

        boolean isEquipped = livingEntity.getItemBySlot(this.slot) == stack;

        int currentBrightness = world.getRawBrightness(holdingEntity.blockPosition(), 0);
        MoonPhase currentMoonPhase = MoonPhase.getCurrentMoonPhase(world);
        boolean currentIsDay = world.isDay() ;

        int oldBrightness = getLightLevel(stack);
        MoonPhase oldMoonPhase = this.getMoonPhase(stack);
        boolean oldIsDay = getIsDay(stack);

        boolean shouldRefresh = false;

        ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS);

        if (isEquipped && attackInstance != null)
            if (attackInstance.getModifier(LIGHT_LEVEL_TOUGHNESS_UUID[slotIndex]) != null)
                shouldRefresh = oldMoonPhase != currentMoonPhase;

        if (currentIsDay != oldIsDay || oldMoonPhase != currentMoonPhase || oldBrightness != currentBrightness) {
            setLightLevel(stack, currentBrightness);
            setMoonPhase(stack, currentMoonPhase);
            setIsDay(stack, currentIsDay);
        }

        if (shouldRefresh && holdingEntity instanceof LivingEntity) {

            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(this.slot, stack);

            attackInstance.removeModifier(LIGHT_LEVEL_TOUGHNESS_UUID[slotIndex]);

            for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                if (instance != null) {
                    if (entry.getValue().getId().equals(LIGHT_LEVEL_TOUGHNESS_UUID[slotIndex])) {
                        instance.addTransientModifier(entry.getValue());
                    }
                }
            }
        }

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return super.getItemEnchantability(stack) + getMoonPhaseValue(getMoonPhase(stack));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));
        int lightLevel = getLightLevel(stack);
        int toughnessBonus = (15 - lightLevel)/5;

        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(
                LIGHT_LEVEL_TOUGHNESS_UUID[this.slot.getIndex()],
                "Obsidian Armor Toughness Bonus",
                toughnessBonus,
                AttributeModifier.Operation.ADDITION
                )
        );

        return builder.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        MoonPhase moonPhase = getMoonPhase(stack);
        int phaseValue = getMoonPhaseValue(moonPhase);
        boolean isNight = !getIsDay(stack);
        int lightLevel = getLightLevel(stack);

        tooltip.add(new StringTextComponent("§9+" + phaseValue + " Enchantment Level"));
        tooltip.add(new StringTextComponent("§9+" + ((15 - lightLevel)/5) + " Armor Toughness" ));

        if (isNight)
            tooltip.add(new StringTextComponent("§eActive: Absorption Generation."));
        else
            tooltip.add(new StringTextComponent("§cInactive: Absorption Regeneration."));

    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);
        if (!world.isClientSide)
            return;
        int totalSetLevel = ILevelableItem.getTotalSetLevel(player);
        if (player.tickCount % ((20*60) - totalSetLevel*4) != 0)
                return;

        if (totalSetLevel > 0 && world.isNight()) {
            final float maxAbsorption = 0.1F*itemLevel;
            float currentAbsorptionAmount = player.getAbsorptionAmount();
            final int MAX_CAP = 20;
            float maxAllowedAbsorption = Math.min(
                    (float)totalSetLevel * maxAbsorption,
                    (float)MAX_CAP
            );

            if (currentAbsorptionAmount > maxAllowedAbsorption) {
                player.setAbsorptionAmount(maxAllowedAbsorption);
            }
        }
    }
}
