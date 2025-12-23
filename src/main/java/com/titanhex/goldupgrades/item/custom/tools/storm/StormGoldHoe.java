package com.titanhex.goldupgrades.item.custom.tools.storm;

import com.titanhex.goldupgrades.item.components.StormToolComponent;
import com.titanhex.goldupgrades.item.components.TreasureToolComponent;
import com.titanhex.goldupgrades.item.custom.inter.IElementalHoe;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectHoe;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

public class StormGoldHoe extends EffectHoe implements ILevelableItem, IWeatherInfluencedItem, IElementalHoe
{

    TreasureToolComponent treasureHandler;
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
    public StormGoldHoe(IItemTier tier, int attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed + 1.333F, effectAmplifications, effectDuration, durabilityCost, properties);
        treasureHandler = new TreasureToolComponent();
        stormToolHandler = new StormToolComponent();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int inventorySlot, boolean isSelected) {
        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);

        if (world.isClientSide)
            return;

        changeWeather(stack, world);
        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        stormToolHandler.updateInventory(stack, world, livingEntity, isSelected);

        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.OFFHAND) == stack;

        if (isEquipped) {
            holdingElementalHoe(stack, livingEntity, Effects.DIG_SPEED, () -> isThundering(stack, world));
        }

        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        stormToolHandler.appendHoverText(stack, worldIn, tooltip);
        treasureHandler.appendHoverText(stack, tooltip, "§Pruning leaves in plains yields treasure during thunderstorms.");
        IElementalHoe.appendHoverText(stack, tooltip, "§eHold for Dig Speed, use for Healing");
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);

        treasureHandler.tryDropTreasure(world, blockPos, blockState, miningEntity, usedStack,
                () -> (blockState.is(BlockTags.LEAVES)),
                new ResourceLocation("goldupgrades", "gameplay/treasure/plains_cutting"),
                10
                );

        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    @Override
    public int getHarvestLevel(@NotNull ItemStack stack, @NotNull ToolType toolType, @Nullable PlayerEntity player, @Nullable BlockState state) {
        return stormToolHandler.getHarvestLevel(stack, super.getHarvestLevel(stack, toolType, player, state));
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int elementalHoeLevel = IElementalHoe.getElementalHoeEnchantmentLevel(stack);

        return IElementalHoe.use(stack, player, Effects.HEAL, 1, 60-(elementalHoeLevel*10));
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        return baseSpeed * (1.0F + stormToolHandler.getDestroyBonus(stack));
    }
}
