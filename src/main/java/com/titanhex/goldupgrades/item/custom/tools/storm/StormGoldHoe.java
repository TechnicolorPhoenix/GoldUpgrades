package com.titanhex.goldupgrades.item.custom.tools.storm;

import com.titanhex.goldupgrades.data.Weather;
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
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
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
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int uInt, boolean uBoolean) {
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
        int elementalHoeLevel = IElementalHoe.getElementalHoeEnchantmentLevel(stack);

        if (isEquipped && elementalHoeLevel > 0 && (isThundering(stack, world))) {
            int duration = 60;
            if (livingEntity.tickCount % 60 == 0)
                stack.hurtAndBreak(1, livingEntity,
                        (e) -> e.broadcastBreakEvent(EquipmentSlotType.OFFHAND)
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
        boolean isThundering = isThundering(stack);
        int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);

        if (isThundering) {
            tooltip.add(new StringTextComponent("§9+"+(30 + weatherBoosterLevel*5)+"% Harvest Speed"));
            tooltip.add(new StringTextComponent("§eMaxed Harvest Level."));
        }
        treasureHandler.appendHoverText(stack, tooltip, "§Pruning leaves in plains yields treasure during thunderstorms.");
        IElementalHoe.appendHoverText(stack, tooltip, "§eHold for Dig Speed, use for Healing");
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);

        treasureHandler.tryDropTreasure(world, blockPos, blockState, miningEntity, usedStack,
                Biomes.PLAINS.location(), (state) -> (state.is(BlockTags.LEAVES)),
                (stack) -> isThundering(stack, world),
                new ResourceLocation("goldupgrades", "gameplay/treasure/plains_cutting"),
                10
                );

        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    @Override
    public int getHarvestLevel(@NotNull ItemStack stack, @NotNull ToolType toolType, @Nullable PlayerEntity player, @Nullable BlockState state) {
        if (getWeather(stack) == Weather.THUNDERING)
            return 5;

        return super.getHarvestLevel(stack, toolType, player, state);
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int elementalHoeLevel = IElementalHoe.getElementalHoeEnchantmentLevel(stack);

        if (elementalHoeLevel > 0) {
            player.addEffect(new EffectInstance(
                    Effects.HEAL,
                    1,
                    0,
                    true,
                    true
            ));

            stack.hurtAndBreak(60-(elementalHoeLevel*10), player, (e) -> e.broadcastBreakEvent(hand));

            return ActionResult.consume(stack);
        }

        return super.use(world, player, hand);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        boolean isThundering = isThundering(stack);

        if (isThundering) {
            if (baseSpeed > 1.0F) {
                return baseSpeed + 0.3F;
            }
        }

        return super.getDestroySpeed(stack, state);
    }
}
