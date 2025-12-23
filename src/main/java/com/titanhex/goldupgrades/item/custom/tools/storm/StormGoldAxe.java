package com.titanhex.goldupgrades.item.custom.tools.storm;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.item.components.DynamicAttributeComponent;
import com.titanhex.goldupgrades.item.components.StormToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectAxe;
import com.titanhex.goldupgrades.item.tool.StormToolItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class StormGoldAxe extends EffectAxe implements ILevelableItem, IWeatherInfluencedItem
{

    DynamicAttributeComponent dynamicAttributeHandler;
    StormToolComponent stormToolHandler;

    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier.
     *
     * @param tier                  The base material.
     * @param attackDamage          The base attack damage.
     * @param attackSpeed           The attack speed modifier.
     * @param effectAmplifications  A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration        The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost        The number of durability points to subtract on each use.
     * @param properties            Item properties.
     */
    public StormGoldAxe(IItemTier tier, float attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed+1.333F, effectAmplifications, effectDuration, durabilityCost, properties);
        this.stormToolHandler = new StormToolComponent();
        this.dynamicAttributeHandler = new DynamicAttributeComponent(StormToolComponent.STORM_DAMAGE_MODIFIER, EquipmentSlotType.MAINHAND);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));
        boolean isThundering = isThundering(stack);

        dynamicAttributeHandler.getAttributeModifiers(
                equipmentSlot, builder,
                () -> isThundering,
                () -> stormToolHandler.getToolDamageBonus(
                        stack,
                        StormToolItems.STORM_POWER_GOLD_AXE.get()
                )
        );

        return builder.build();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int inventorySlot, boolean isSelected) {
        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);

        if (world.isClientSide)
            return;
        if (!(holdingEntity instanceof LivingEntity))
            return;

        boolean environmentalStateChanged = changeWeather(stack, world);

        if (!environmentalStateChanged) return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        stormToolHandler.updateInventory(stack, world, livingEntity, isSelected);

        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND) == stack;

        if (isEquipped) {
            ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

            if (attackInstance != null) {
                Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);
                dynamicAttributeHandler.updateAttributes(livingEntity, newModifiers, attackInstance);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        stormToolHandler.appendHoverText(stack, worldIn, tooltip);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity targetEntity, @NotNull LivingEntity attacker) {
        if (!super.hurtEnemy(stack, targetEntity, attacker)) return false;

        stormToolHandler.hurtEnemyWithLightning(stack, targetEntity, attacker);

        return true;
    }

    @Override
    public int getHarvestLevel(@NotNull ItemStack stack, @NotNull ToolType toolType, @Nullable PlayerEntity player, @Nullable BlockState state) {
        return stormToolHandler.getHarvestLevel(stack, super.getHarvestLevel(stack, toolType, player, state));
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        return baseSpeed * (1.0F + stormToolHandler.getDestroyBonus(stack));
    }
}
