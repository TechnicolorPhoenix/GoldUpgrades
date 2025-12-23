package com.titanhex.goldupgrades.item.custom.tools.storm;

import com.titanhex.goldupgrades.item.components.StormToolComponent;
import com.titanhex.goldupgrades.item.components.TreasureToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectShovel;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class StormGoldShovel extends EffectShovel implements ILevelableItem, IWeatherInfluencedItem {

    StormToolComponent stormToolHandler;
    TreasureToolComponent treasureHandler;

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
    public StormGoldShovel(IItemTier tier, float attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed, effectAmplifications, effectDuration, durabilityCost, properties);
        this.treasureHandler = new TreasureToolComponent();
        this.stormToolHandler = new StormToolComponent();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int inventorySlot, boolean isSelected) {
        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);

        if (world.isClientSide)
            return;

        changeWeather(stack, world);
        stormToolHandler.updateInventory(stack, world, (LivingEntity) holdingEntity, isSelected);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        treasureHandler.appendHoverText(stack, tooltip, "§eDigging grass in swamps yields treasure during thunderstorms.");
        stormToolHandler.appendHoverText(stack, worldIn, tooltip);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);

        treasureHandler.tryDropTreasure(
                world, blockPos, blockState, miningEntity, usedStack,
                () -> blockState.is(Blocks.GRASS_BLOCK),
                new ResourceLocation("goldupgrades", "gameplay/treasure/swamp_digging"),
                6
        );

        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
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
