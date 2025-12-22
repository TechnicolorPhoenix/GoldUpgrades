package com.titanhex.goldupgrades.item.custom.tools.obsidian;

import com.titanhex.goldupgrades.item.components.ObsidianToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

public class ObsidianGoldPickaxe extends PickaxeItem implements ILevelableItem, IDayInfluencedItem, IMoonPhaseInfluencedItem, ILightInfluencedItem
{
    ObsidianToolComponent obsidianToolHandler;

    public ObsidianGoldPickaxe(IItemTier tier, int atkDamage, float atkSpeed, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.obsidianToolHandler = new ObsidianToolComponent();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int uInt, boolean uBoolean) {
        if (world.isClientSide) {
            super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
            return;
        }

        changeDay(stack, world);
        changeMoonPhase(stack, world);

        int currentBrightness = ILightInfluencedItem.getLightLevel(stack);
        int oldBrightness = getLightLevel(stack, world, holdingEntity.blockPosition());

        if (oldBrightness > 0 != currentBrightness > 0) {
            ILightInfluencedItem.setLightLevel(stack, currentBrightness);
        }

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float bonusSpeed = isDay(stack) ? 0 : 0.15F;

        if (ILightInfluencedItem.getLightLevel(stack) == 0) {
            baseSpeed = 1.1F;
        }

        if (baseSpeed > 1.0F) {
            float speedMultiplier = 1.0F + bonusSpeed;
            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return super.getItemEnchantability(stack) + getMoonPhaseValue(stack);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        boolean appliedDamage = super.hurtEnemy(stack, target, attacker);
        if (!appliedDamage) return false;

        obsidianToolHandler.hurtEnemy(stack, target, Effects.WEAKNESS);

        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        double weaknessChance = obsidianToolHandler.getEffectChance(stack);

        tooltip.add(new StringTextComponent((weaknessChance == 0 ? "§c" : "§a") + "Weakness Chance: " + weaknessChance + "%"));
        obsidianToolHandler.appendHoverText(worldIn, stack, tooltip);
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