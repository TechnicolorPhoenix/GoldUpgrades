package com.titanhex.goldupgrades.item.custom.tools.obsidian;

import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.item.components.ObsidianToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObsidianGoldHoe extends HoeItem implements IMoonPhaseInfluencedItem, IDayInfluencedItem, ILightInfluencedItem, ILevelableItem, IElementalHoe
{

    ObsidianToolComponent obsidianToolHandler;

    public ObsidianGoldHoe(IItemTier tier, int atkDamage, float atkSpeed, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.obsidianToolHandler = new ObsidianToolComponent();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int inventorySlot, boolean isSelected) {
        if (world.isClientSide) {
            super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);
            return;
        }

        int currentBrightness = world.getRawBrightness(holdingEntity.blockPosition(), 0);
        int oldBrightness = ILightInfluencedItem.getLightLevel(stack);

        changeMoonPhase(stack, world);
        changeDay(stack, world);

        if (oldBrightness > 0 != currentBrightness > 0)
            ILightInfluencedItem.setLightLevel(stack, currentBrightness);

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.OFFHAND) == stack;

        if (isEquipped)
            holdingElementalHoe(stack, livingEntity, Effects.LUCK, () -> isNight(stack));

        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        return obsidianToolHandler.getDestroySpeed(stack, baseSpeed);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return super.getItemEnchantability(stack) + obsidianToolHandler.getItemEnchantability(stack);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        boolean appliedDamage = super.hurtEnemy(stack, target, attacker);
        if (!appliedDamage) return false;

        obsidianToolHandler.hurtEnemy(stack, target, Effects.MOVEMENT_SLOWDOWN);

        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        return super.getAttributeModifiers(equipmentSlot, stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        double slowChance = obsidianToolHandler.getEffectChance(stack);

        tooltip.add(new StringTextComponent((slowChance == 0 ? "§c" : "§a" ) + "Slow Chance: " + slowChance + "%"));
        obsidianToolHandler.appendHoverText(worldIn, stack, tooltip);

        IElementalHoe.appendHoverText(stack, tooltip, "§eHold for Luck, use for Night Vision");
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        return obsidianToolHandler.canHarvestBlock(stack, state, super.canHarvestBlock(stack, state));
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int elementalHoeLevel = IElementalHoe.getElementalHoeEnchantmentLevel(stack);

        if (hand == Hand.OFF_HAND)
            return IElementalHoe.use(stack, player, Effects.NIGHT_VISION, 30*20*2+(elementalHoeLevel*20*20)-(20*20));

        return super.use(world, player, hand);
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