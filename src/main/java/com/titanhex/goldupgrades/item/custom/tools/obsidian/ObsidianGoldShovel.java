package com.titanhex.goldupgrades.item.custom.tools.obsidian;

import com.titanhex.goldupgrades.item.components.ObsidianToolComponent;
import com.titanhex.goldupgrades.item.interfaces.IDayInfluencedItem;
import com.titanhex.goldupgrades.item.interfaces.ILevelableItem;
import com.titanhex.goldupgrades.item.interfaces.ILightInfluencedItem;
import com.titanhex.goldupgrades.item.interfaces.IMoonPhaseInfluencedItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObsidianGoldShovel extends ShovelItem implements ILevelableItem, IDayInfluencedItem, IMoonPhaseInfluencedItem, ILightInfluencedItem
{
    ObsidianToolComponent obsidianToolHandler;
    public ObsidianGoldShovel(IItemTier tier, float atkDamage, float atkSpeed, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.obsidianToolHandler = new ObsidianToolComponent();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int inventorySlot, boolean isSelected) {
        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);

        if (world.isClientSide) {
            return;
        }

        changeDay(stack, world);
        changeMoonPhase(stack, world);

        int currentBrightness = getLightLevel(stack, world, holdingEntity.blockPosition());
        int oldBrightness = ILightInfluencedItem.getLightLevel(stack);

        if (oldBrightness > 0 != currentBrightness > 0) {
            ILightInfluencedItem.setLightLevel(stack, currentBrightness);
        }
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
        if (!hurtEnemy(stack, target, attacker)) return false;

        obsidianToolHandler.hurtEnemy(stack, target, Effects.POISON);

        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        double poisonChance = obsidianToolHandler.getEffectChance(stack);

        tooltip.add(new StringTextComponent((poisonChance == 0 ? "§c" : "§a") + "Poison Chance: " + poisonChance + "%"));
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