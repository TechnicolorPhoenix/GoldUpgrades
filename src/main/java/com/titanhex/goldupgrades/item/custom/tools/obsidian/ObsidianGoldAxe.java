package com.titanhex.goldupgrades.item.custom.tools.obsidian;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.item.components.DynamicAttributeComponent;
import com.titanhex.goldupgrades.item.components.ObsidianToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.IDayInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.ILightInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IMoonPhaseInfluencedItem;
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
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObsidianGoldAxe extends AxeItem implements ILightInfluencedItem, IDayInfluencedItem, IMoonPhaseInfluencedItem, ILevelableItem
{

    ObsidianToolComponent obsidianToolHandler;
    DynamicAttributeComponent dynamicAttributeHandler;

    public ObsidianGoldAxe(IItemTier tier, float atkDamage, float atkSpeed, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        obsidianToolHandler = new ObsidianToolComponent();
        dynamicAttributeHandler = new DynamicAttributeComponent(ObsidianToolComponent.NIGHT_DAMAGE_UUID, EquipmentSlotType.MAINHAND);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        int enchantmentBonus = obsidianToolHandler.getItemEnchantability(stack);
        return super.getItemEnchantability(stack) + enchantmentBonus;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int inventorySlot, boolean isSelected) {
        if (world.isClientSide) {
            super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);
            return;
        }

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND) == stack;

        boolean moonPhaseChanged = changeMoonPhase(stack, world);
        boolean dayChanged = changeDay(stack, world);

        int currentBrightness = world.getRawBrightness(holdingEntity.blockPosition(), 0);
        int oldBrightness = ILightInfluencedItem.getLightLevel(stack);

        if (oldBrightness > 0 != currentBrightness > 0)
            ILightInfluencedItem.setLightLevel(stack, currentBrightness);

        boolean shouldRefresh = dayChanged || moonPhaseChanged;

        if (shouldRefresh && isEquipped && holdingEntity instanceof LivingEntity) {

            ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

            if (attackInstance != null) {
                Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);

                dynamicAttributeHandler.updateAttributes(livingEntity, newModifiers, attackInstance);
            }
        }

        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        return obsidianToolHandler.getDestroySpeed(stack, baseSpeed);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));
        float phaseValue = getMoonPhaseValue(stack);

        dynamicAttributeHandler.getAttributeModifiers(
                equipmentSlot, builder,
                () -> phaseValue > 0,
                () -> obsidianToolHandler.getObsidianDamage(stack)
        );

        return builder.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        obsidianToolHandler.appendHoverText(worldIn, stack, tooltip);
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        return obsidianToolHandler.canHarvestBlock(stack, state, super.canHarvestBlock(stack, state));
    }

    /**
     * Handles the block use event (Right Click) with custom Pickaxe and Fire functionality.
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide) return super.useOn(context);
        PlayerEntity player = context.getPlayer();
        if (player == null) return super.useOn(context);
        ItemStack stack = context.getItemInHand();

        return obsidianToolHandler.useOn(context, stack);
    }

    @Override
    public boolean isDayInfluenced() {
        return false;
    }
}