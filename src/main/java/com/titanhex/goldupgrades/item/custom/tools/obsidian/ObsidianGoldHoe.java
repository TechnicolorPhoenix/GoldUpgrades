package com.titanhex.goldupgrades.item.custom.tools.obsidian;

import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.MoonPhase;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import com.titanhex.goldupgrades.item.custom.inter.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ObsidianGoldHoe extends HoeItem implements IMoonPhaseInfluencedItem, IDayInfluencedItem, ILightInfluencedItem, ILevelableItem, IElementalHoe
{

    private static final Random RANDOM = new Random();

    public ObsidianGoldHoe(IItemTier tier, int atkDamage, float atkSpeed, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int uInt, boolean uBoolean) {
        if (world.isClientSide) {
            super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
            return;
        }

        int currentBrightness = world.getRawBrightness(holdingEntity.blockPosition(), 0);
        MoonPhase currentMoonPhase = MoonPhase.getCurrentMoonPhase(world);
        boolean currentIsDay = isDay(stack, world);

        int oldBrightness = ILightInfluencedItem.getLightLevel(stack);
        MoonPhase oldMoonPhase = this.getMoonPhase(stack);
        boolean oldIsDay = isDay(stack);

        if (currentIsDay != oldIsDay || oldMoonPhase != currentMoonPhase || oldBrightness > 0 != currentBrightness > 0) {
            ILightInfluencedItem.setLightLevel(stack, currentBrightness);
            setMoonPhase(stack, currentMoonPhase);
            setIsDay(stack, currentIsDay);
        }

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.OFFHAND) == stack;
        int elementalHoeLevel = getElementalHoeEnchantmentLevel(stack);

        if (isEquipped && elementalHoeLevel > 0 && (isNight(stack))) {
            int duration = 60;
            if (livingEntity.tickCount % 60 == 0)
                stack.hurtAndBreak(1, livingEntity,
                        (e) -> {
                            e.broadcastBreakEvent(EquipmentSlotType.OFFHAND);
                        }
                );
            livingEntity.addEffect(new EffectInstance(
                    Effects.LUCK,
                    duration,
                    elementalHoeLevel-1,
                    false,
                    false
            ));
        }

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float bonusSpeed = isDay(stack) ? 0 : 0.15F;

        if (ILightInfluencedItem.getLightLevel(stack) == 0)
            baseSpeed = 1.1F;

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
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity enemy) {
        int phaseValue = getMoonPhaseValue(stack)*(2+getItemLevel());
        int chance = RANDOM.nextInt(100)+1;

        if (super.hurtEnemy(stack, target, enemy) && chance < phaseValue) {
            target.addEffect(new EffectInstance(
                    Effects.MOVEMENT_SLOWDOWN,
                    100,
                    0
            ));
            return true;
        }
        return false;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        return super.getAttributeModifiers(equipmentSlot, stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        boolean hasElementalHoeEnchantment = hasElementalHoeEnchantment(stack);

        int phaseValue = getMoonPhaseValue(stack, MoonPhase.getCurrentMoonPhase(worldIn));
        int slowChance = phaseValue*(2+getItemLevel());

        tooltip.add(new StringTextComponent((slowChance == 0 ? "§c" : "§a" ) + "Slow Chance: " + slowChance + "%"));
        tooltip.add(new StringTextComponent("§9+" + phaseValue + " Enchantment Level"));

        if (ILightInfluencedItem.getLightLevel(stack) == 0)
            tooltip.add(new StringTextComponent("§eHarvest Anything."));

        if (hasElementalHoeEnchantment)
            tooltip.add(new StringTextComponent("§eHold for Luck, use for Night Vision"));

        if (isNight(stack))
            tooltip.add(new StringTextComponent("§9+15% Harvest Speed."));
        else
            tooltip.add(new StringTextComponent("§cInactive: Harvest Speed Bonus (Requires Night)"));
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        if (ILightInfluencedItem.getLightLevel(stack) == 0)
            return true;

        return super.canHarvestBlock(stack, state);
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int elementalHoeLevel = getElementalHoeEnchantmentLevel(stack);

        if (elementalHoeLevel > 0) {
            player.addEffect(new EffectInstance(
                    Effects.NIGHT_VISION,
                    30*20*2+(elementalHoeLevel*20*20)-(20*20),
                    0,
                    true,
                    true
            ));

            stack.hurtAndBreak(5*elementalHoeLevel*2, player, (e) -> {e.broadcastBreakEvent(hand);});

            return ActionResult.consume(stack);
        }

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
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        int itemLevel = getItemLevel();

        if (player != null) {

            BlockRayTraceResult hitResult = world.clip(
                    new RayTraceContext(
                            player.getEyePosition(1.0F),
                            player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)),
                            RayTraceContext.BlockMode.OUTLINE,
                            RayTraceContext.FluidMode.ANY, // Check ANY block/fluid in range
                            player
                    )
            );

            if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos rayHitPos = hitResult.getBlockPos();
                BlockState rayHitState = world.getBlockState(rayHitPos);

                if (rayHitState.getBlock() == Blocks.LAVA) {

                    world.setBlock(rayHitPos, Blocks.OBSIDIAN.defaultBlockState(), 3);

                    if (world.dimension() == World.NETHER) {
                        stack.hurtAndBreak((4-itemLevel)*3, player, (entity) -> entity.broadcastBreakEvent(context.getHand()));
                    } else {
                        int currentDamage = stack.getDamageValue();
                        stack.setDamageValue(Math.max(0, currentDamage - itemLevel));
                    }

                    player.giveExperiencePoints(1);

                    world.playSound(null, clickedPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public boolean isDayInfluenced() {
        return false;
    }
}