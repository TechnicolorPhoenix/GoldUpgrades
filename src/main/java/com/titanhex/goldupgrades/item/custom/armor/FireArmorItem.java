package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
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
import net.minecraft.nbt.CompoundNBT; // <-- REQUIRED NEW IMPORT
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

public class FireArmorItem extends ArmorItem {
    protected float recoverAmount;
    protected float damageBonus;
    protected int perTickRecoverSpeed;

    private static final String NBT_NETHER = "ArmorInNether";
    private static final String NBT_IN_CLEAR = "ArmorInClearWeather";

    private static final UUID[] SUN_DAMAGE_MODIFIER = new UUID[]{
            UUID.fromString("6d8b6c38-1456-37c0-9b62-421f421f421d"), // BOOTS (Existing)
            UUID.fromString("b9a5f1e8-6e7e-40d0-8b6a-9a0f0d2b7c6c"), // LEGGINGS
            UUID.fromString("730a9e1d-4f5d-4f3b-8c1a-7e0f8b1c4d2e"), // CHESTPLATE
            UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d")  // HELMET
    };

    public FireArmorItem(IArmorMaterial materialIn, float recoverAmount, int perTickRecoverSpeed, EquipmentSlotType slot, float damageBonus, Properties builderIn) {
        super(materialIn, slot, builderIn);
        this.recoverAmount = recoverAmount;
        this.perTickRecoverSpeed = perTickRecoverSpeed;
        this.damageBonus = damageBonus;
    }

    private boolean getNether(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_NETHER);
    }
    private void setNether(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_NETHER, value);
    }

    private boolean getInClear(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IN_CLEAR);
    }
    private void setInClear(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IN_CLEAR, value);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));

        if (equipmentSlot == this.slot) {
            boolean inNether = this.getNether(stack);
            boolean inClear = this.getInClear(stack);

            // Check the synchronized NBT state
            if (inClear || inNether) {
                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        SUN_DAMAGE_MODIFIER[this.slot.getIndex()],
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
        boolean isClearWeather = getInClear(stack);
        boolean isNetherActive = getNether(stack);
        boolean isDamageBonusActive = isClearWeather || isNetherActive;

        if (isDamageBonusActive) {
            tooltip.add(new StringTextComponent("§aActive: Damage Bonus (+" + this.damageBonus + " Attack Damage)"));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Damage Bonus (Requires Clear Skies or Nether)"));
        }

        if (isClearWeather) {
            tooltip.add(new StringTextComponent("§aActive: Regen Bonus (+" + this.recoverAmount + " Health per " + (this.perTickRecoverSpeed / 20) + " seconds.)"));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Regen Bonus (Requires Clear Skies)"));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int unknownInt, boolean unknownConditional)
    {
        super.inventoryTick(stack, world, holdingEntity, unknownInt, unknownConditional);

        if (world.isClientSide) return;

        if (!(holdingEntity instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(this.slot) == stack;

        Weather newWeather = Weather.getCurrentWeather(world);
        DimensionType newDimension = DimensionType.getCurrentDimension(world);

        boolean isNetherNow = DimensionType.NETHER == DimensionType.getCurrentDimension(world);
        boolean isInClearNow = Weather.CLEAR == Weather.getCurrentWeather(world);

        boolean oldNether = this.getNether(stack);
        boolean oldInClear = this.getInClear(stack);

        boolean shouldRefresh = false;

        if (isEquipped) {
            if (isInClearNow != oldInClear) {
                this.setInClear(stack, isInClearNow);
                shouldRefresh = true;
            }
            if (isNetherNow != oldNether) {
                this.setNether(stack, isNetherNow);
                shouldRefresh = true;
            }

        } else {
            // Crucial: If unequipped, force the NBT state to INACTIVE.
            // This ensures the attribute modifiers map will be empty for dynamic bonuses.
            if (oldInClear || oldNether) {
                setInClear(stack, false);
                setNether(stack, false);
                // When an item is unequipped, the game engine usually handles attribute removal,
                // but forcing the state to false is a good guard.
                shouldRefresh = true;
            }
        }

        if (shouldRefresh && holdingEntity instanceof LivingEntity) {

            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(this.slot, stack);

            ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackInstance != null) {
                attackInstance.removeModifier(SUN_DAMAGE_MODIFIER[this.slot.getIndex()]);
            }

            if (isEquipped) {
                for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                    ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                    if (instance != null) {
                        if (entry.getValue().getId().equals(SUN_DAMAGE_MODIFIER[this.slot.getIndex()]))
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

        if (world.isClientSide)
            return;

        if (player.tickCount % perTickRecoverSpeed == 0 && getInClear(stack))
            player.heal(recoverAmount);
    }
}
