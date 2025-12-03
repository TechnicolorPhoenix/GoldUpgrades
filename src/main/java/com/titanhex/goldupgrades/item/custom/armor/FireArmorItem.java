package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT; // <-- REQUIRED NEW IMPORT
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class FireArmorItem extends ArmorItem {
    protected float recoverAmount = 0.1F;
    protected float damageBonus = 0F;
    protected int perTickRecoverSpeed = 20;

    private static final UUID SUN_DAMAGE_MODIFIER = UUID.fromString("6F21A77E-F0C6-44D1-A12A-14C2D8397E9C");

    private Weather weather = Weather.CLEAR;
    private DimensionType dimension = DimensionType.OVERWORLD;

    // --- Constructors (Kept original structure) ---
    public FireArmorItem(IArmorMaterial materialIn, float recoverAmount, int perTickRecoverSpeed, EquipmentSlotType slot, float damageBonus, Properties builderIn) {
        super(materialIn, slot, builderIn);
        this.recoverAmount = recoverAmount;
        this.perTickRecoverSpeed = perTickRecoverSpeed;
        this.damageBonus = damageBonus;
    }
    // All other constructors omitted for brevity...

    // --- Attribute Modifiers (Reads state from NBT) ---
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));

        if (equipmentSlot == this.slot) {
            // Check the synchronized NBT state
            if (weather == Weather.CLEAR || dimension == DimensionType.NETHER) {
                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        SUN_DAMAGE_MODIFIER,
                        "Armor modifier",
                        this.damageBonus,
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }

        return builder.build();
    }

    // --- Tooltip Display (Reads state from NBT) ---
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        // Read the synchronized NBT state for display
        boolean isRainActive = weather == Weather.RAIN;
        boolean isOverworldActive = dimension == DimensionType.OVERWORLD;
        boolean isDamageBonusActive = isRainActive || isOverworldActive;

        if (isDamageBonusActive) {
            tooltip.add(new StringTextComponent("§aActive: Damage Bonus (+" + this.damageBonus + " Attack Damage)"));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Damage Bonus (Requires Clear Skies or Nether)"));
        }

        if (isRainActive) {
            tooltip.add(new StringTextComponent("§aActive: Regen Bonus (+" + this.recoverAmount + " Health per " + (this.perTickRecoverSpeed / 20) + " seconds.)"));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Regen Bonus (Requires Clear Skies)"));
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);

        if (world.isClientSide)
            return;

        if (player.tickCount % perTickRecoverSpeed == 0 && (Weather.getCurrentWeather(world) == Weather.CLEAR))
            player.heal(recoverAmount);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int unknownInt, boolean unknownConditional)
    {
        super.inventoryTick(stack, world, holdingEntity, unknownInt, unknownConditional);

        world.getMaxLocalRawBrightness(holdingEntity.blockPosition());
        if (world.isClientSide)
            return;

        Weather newWeather = Weather.getCurrentWeather(world);
        DimensionType newDimension = DimensionType.getCurrentDimension(world);

        if (this.weather != newWeather) {
            this.weather = newWeather;
            stack.setTag(stack.getTag());
        }
        if (this.dimension != newDimension) {
            this.dimension = newDimension;
            stack.setTag(stack.getTag());
        }
    }
}
