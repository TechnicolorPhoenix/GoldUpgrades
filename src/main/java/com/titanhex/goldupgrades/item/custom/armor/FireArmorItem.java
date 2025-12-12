package com.titanhex.goldupgrades.item.custom.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.custom.inter.IDayInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IDimensionInfluencedItem;
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
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FireArmorItem extends ArmorItem implements IWeatherInfluencedItem, IDimensionInfluencedItem, IDayInfluencedItem {
    protected float recoverAmount;
    protected float damageBonus;
    protected int perTickRecoverSpeed;

    private static final String NBT_ARMOR_TIMER_KEY = "ArmorTimer";

    private static final UUID[] SUN_DAMAGE_MODIFIER = new UUID[]{
            UUID.randomUUID(), // BOOTS (Existing)
            UUID.randomUUID(), // LEGGINGS
            UUID.randomUUID(), // CHESTPLATE
            UUID.randomUUID()  // HELMET
    };

    public FireArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, float recoverAmount, int perTickRecoverSpeed, float damageBonus, Properties builderIn) {
        super(materialIn, slot, builderIn);
        this.recoverAmount = recoverAmount;
        this.perTickRecoverSpeed = perTickRecoverSpeed;
        this.damageBonus = damageBonus;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));

        if (equipmentSlot == this.slot) {
            boolean inNether = getDimension(stack) == DimensionType.NETHER;
            boolean inClear = getWeather(stack) == Weather.CLEAR;

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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        boolean inNether = getDimension(stack) == DimensionType.NETHER;
        boolean isDay = isDay(stack);
        boolean inClear = getWeather(stack) == Weather.CLEAR;
        boolean isDamageBonusActive = inClear || inNether;

        if (isDamageBonusActive) {
            tooltip.add(new StringTextComponent("§aActive: +" + this.damageBonus + " Attack Damage"));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Damage Bonus (Requires Clear Skies or Nether)"));
        }

        if (isDay) {
            tooltip.add(new StringTextComponent("§aActive: +" + this.recoverAmount + " Health per " + (this.perTickRecoverSpeed / 20) + " seconds.)"));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Regen Bonus (Requires Day)"));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int unknownInt, boolean unknownConditional) {
        super.inventoryTick(stack, world, holdingEntity, unknownInt, unknownConditional);

        if (world.isClientSide) return;

        int slotIndex = this.slot.getIndex();

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(this.slot) == stack;

        DimensionType currentDimension = DimensionType.getCurrentDimension(world);
        Weather currentWeather = Weather.getCurrentWeather(world);

        DimensionType oldDimension = getDimension(stack);
        Weather oldWeather = getWeather(stack);

        boolean environmentChanged = false;

        ModifiableAttributeInstance toughnessInstance = livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS);

        if (isEquipped && toughnessInstance != null)
            if (toughnessInstance.getModifier(SUN_DAMAGE_MODIFIER[slotIndex]) != null)
                environmentChanged = oldDimension != currentDimension || oldWeather != currentWeather;

        if (currentWeather != oldWeather || currentDimension != oldDimension) {
            this.setWeather(stack, currentWeather);
            this.setDimension(stack, currentDimension);
        }

        if (environmentChanged && holdingEntity instanceof LivingEntity) {

            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(this.slot, stack);
            toughnessInstance.removeModifier(SUN_DAMAGE_MODIFIER[this.slot.getIndex()]);

            for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                if (instance != null) {
                    if (entry.getValue().getId().equals(SUN_DAMAGE_MODIFIER[this.slot.getIndex()])) {
                        instance.addTransientModifier(entry.getValue());
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

        CompoundNBT nbt = stack.getOrCreateTag();
        int timer = nbt.getInt(NBT_ARMOR_TIMER_KEY);

        if (timer <= 0) {
            if (isDay(stack)) {
                player.heal(this.recoverAmount);
            }

            nbt.putInt(NBT_ARMOR_TIMER_KEY, this.perTickRecoverSpeed);
        } else {
            nbt.putInt(NBT_ARMOR_TIMER_KEY, timer - 1);
        }    }
}
