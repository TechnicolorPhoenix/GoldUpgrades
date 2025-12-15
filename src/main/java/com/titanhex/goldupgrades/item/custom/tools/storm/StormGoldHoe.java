package com.titanhex.goldupgrades.item.custom.tools.storm;

import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import com.titanhex.goldupgrades.item.custom.inter.IElementalHoe;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectHoe;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectPickaxe;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier of the pickaxe.
     *
     * @param tier                  The base material of the tool.
     * @param attackDamage          The base attack damage of the tool.
     * @param attackSpeed           The attack speed modifier of the tool.
     * @param effectAmplifications  A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration        The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost        The number of durability points to subtract on each use.
     * @param properties            Item properties.
     */
    public StormGoldHoe(IItemTier tier, int attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed + 1.333F, effectAmplifications, effectDuration, durabilityCost, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int uInt, boolean uBoolean) {
        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);

        if (world.isClientSide)
            return;

        Weather oldWeather = getWeather(stack);
        Weather currentWeather = Weather.getCurrentWeather(world);

        if (oldWeather != currentWeather) {
            setWeather(stack, currentWeather);
        }

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.OFFHAND) == stack;
        int elementalHoeLevel = getElementalHoeEnchantmentLevel(stack);

        if (isEquipped && elementalHoeLevel > 0 && (isThundering(stack, world))) {
            int duration = 60;
            if (livingEntity.tickCount % 60 == 0)
                stack.hurtAndBreak(1, livingEntity,
                        (e) -> {
                            e.broadcastBreakEvent(EquipmentSlotType.OFFHAND);
                        }
                );
            livingEntity.addEffect(new EffectInstance(
                    Effects.DIG_SPEED,
                    duration,
                    elementalHoeLevel-1,
                    false,
                    false
            ));
        }

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        boolean isThundering = getWeather(stack) == Weather.THUNDERING;
        int weatherBoostLevel = getWeatherBoosterEnchantmentLevel(stack);
        boolean hasElementalHoeEnchantment = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ELEMENTAL_HOE_ENCHANTMENT.get(), stack) > 0;

        if (isThundering) {
            tooltip.add(new StringTextComponent("§9+30% Harvest Speed"));
            tooltip.add(new StringTextComponent("§eMaxed Harvest Level."));
        }
        if (weatherBoostLevel > 0)
            tooltip.add(new StringTextComponent("§e"));
        if (hasElementalHoeEnchantment)
            tooltip.add(new StringTextComponent("§eHold for Dig Speed, use for Healing"));
    }

    @Override
    public boolean mineBlock(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
        return super.mineBlock(p_179218_1_, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_);
    }

    @Override
    public int getHarvestLevel(@NotNull ItemStack stack, @NotNull ToolType toolType, @Nullable PlayerEntity player, @Nullable BlockState state) {
        if (getWeather(stack) == Weather.THUNDERING)
            return 5;

        return super.getHarvestLevel(stack, toolType, player, state);
    }

    @Override
    public ActionResult<ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int elementalHoeLevel = getElementalHoeEnchantmentLevel(stack);

        if (elementalHoeLevel > 0) {
            player.addEffect(new EffectInstance(
                    Effects.HEAL,
                    1,
                    0,
                    true,
                    true
            ));

            stack.hurtAndBreak(60-(elementalHoeLevel*10), player, (e) -> {e.broadcastBreakEvent(hand);});

            return ActionResult.consume(stack);
        }

        return super.use(world, player, hand);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        boolean isThundering = getWeather(stack) == Weather.THUNDERING;

        if (isThundering) {
            if (baseSpeed > 1.0F) {
                return baseSpeed + 0.3F;
            }
        }

        return super.getDestroySpeed(stack, state);
    }
}
