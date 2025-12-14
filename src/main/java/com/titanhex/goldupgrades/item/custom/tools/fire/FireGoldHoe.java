package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import com.titanhex.goldupgrades.item.custom.inter.*;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FireGoldHoe extends HoeItem implements ILevelableItem, IIgnitableTool, IDimensionInfluencedItem, IWeatherInfluencedItem, IDayInfluencedItem, ILightInfluencedItem
{
    int burnTicks;
    int durabilityUse;

    public FireGoldHoe(IItemTier tier, int atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int uInt, boolean uBoolean) {
        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);

        int currentBrightness = getLightLevel(stack, world, holdingEntity.blockPosition());

        int oldBrightness = getLightLevel(stack);

        if (oldBrightness != currentBrightness)
            setLightLevel(stack, currentBrightness);

        if (world.isClientSide)
            return;

        if (!(holdingEntity instanceof LivingEntity))
            return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;

        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.OFFHAND) == stack;

        DimensionType oldDimension = getDimension(stack);
        Weather oldWeather = getWeather(stack);
        boolean oldIsDay = isDay(stack);

        DimensionType currentDimension = DimensionType.getCurrentDimension(world);
        Weather currentWeather = Weather.getCurrentWeather(world);
        boolean currentIsDay = isDay(stack, world);

        if (oldWeather != currentWeather || oldDimension != currentDimension || currentIsDay != oldIsDay) {
            setWeather(stack, currentWeather);
            setDimension(stack, currentDimension);
            setIsDay(stack, currentIsDay);
        }

        int elementalHoeLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ELEMENTAL_HOE_ENCHANTMENT.get(), stack);

        if (isEquipped && elementalHoeLevel > 0 && (isClear(stack) || inValidDimension(stack))) {
            if (livingEntity.tickCount % 60 == 0)
                stack.hurtAndBreak(1, livingEntity,
                        (e) -> {
                            e.broadcastBreakEvent(EquipmentSlotType.OFFHAND);
                        }
                );
            livingEntity.addEffect(new EffectInstance(
                    Effects.FIRE_RESISTANCE,
                    20,
                    elementalHoeLevel-1,
                    false,
                    false
            ));
        }
    }

    private float calculateBonusDestroySpeed(ItemStack stack) {
        int lightLevel = getLightLevel(stack);

        return (lightLevel > 7 ? 0.15F : 0.00F + (float) getWeatherBoosterEnchantment(stack))/100;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float bonusSpeed = calculateBonusDestroySpeed(stack);

        if (baseSpeed > 1.0F) {
            float speedMultiplier = 1.0F + bonusSpeed;

            return baseSpeed * speedMultiplier;        }

        return baseSpeed;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        int lightLevel = getLightLevel(stack);

        boolean hasElementalHoeEnchantment = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ELEMENTAL_HOE_ENCHANTMENT.get(), stack) > 0;

        float bonus = calculateBonusDestroySpeed(stack)*100;

        if (lightLevel > 7)
            tooltip.add(new StringTextComponent("§9+" + bonus + "% Harvest Speed"));
        if (hasElementalHoeEnchantment) {
            tooltip.add(new StringTextComponent("§eHold for Fire Resistance, use for Strength"));
        }
    }

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    @Override
    public void igniteEntity(LivingEntity target, ItemStack stack) {
        target.setSecondsOnFire(2 + (isDay(stack) ? 2 : 0));
    }

    /**
     * Attempts to ignite a block, similar to a Flint and Steel.
     * This logic is typically called from the Item's onItemUse method.
     *
     * @param player The player performing the action.
     * @param world  The world the action is taking place in.
     * @param firePos    The BlockPos of the block being targeted.
     * @return ActionResultType.SUCCESS if fire was placed, ActionResultType.PASS otherwise.
     */
    @Override
    public ActionResultType igniteBlock(PlayerEntity player, World world, BlockPos firePos) {
        if (world.isEmptyBlock(firePos) || Blocks.FIRE.getBlock().defaultBlockState().canSurvive(world, firePos)) {
            world.playSound(player, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);

            world.setBlock(firePos, Blocks.FIRE.getBlock().defaultBlockState(), burnTicks);

            if (player != null) {
                player.getItemInHand(player.getUsedItemHand()).hurtAndBreak(durabilityUse, player, (p) -> {
                    p.broadcastBreakEvent(player.getUsedItemHand());
                });
            }

            return ActionResultType.sidedSuccess(world.isClientSide); // ActionResultType.success(world.isRemote)
        }

        return ActionResultType.PASS;
    }

    /**
     * Handles the entity hit event (Left Click on Entity).
     */
    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        igniteEntity(target, stack);
        return true;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand); // Get the ItemStack directly from the context
        int elementalHoeLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ELEMENTAL_HOE_ENCHANTMENT.get(), stack);
        if (elementalHoeLevel > 0) {
            player.addEffect(new EffectInstance(
               Effects.DAMAGE_BOOST,
               30*20,
               elementalHoeLevel,
               true,
               true
            ));

            stack.hurtAndBreak(15, player, (e) -> {e.broadcastBreakEvent(hand);});

            return ActionResult.consume(stack);
        }

        return super.use(world, player, hand);
    }

    /**
     * Handles the block use event (Right Click) with custom Hoe and Fire functionality.
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide)
            return ActionResultType.SUCCESS;

        PlayerEntity player = context.getPlayer();
        if (player == null)
            return super.useOn(context);

        Direction face = context.getClickedFace();
        ItemStack stack = context.getItemInHand(); // Get the ItemStack directly from the context
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);
        Block clickedBlock = clickedState.getBlock();

        if (clickedState.is(BlockTags.LOGS)) {
            world.removeBlock(clickedPos, false);

            Block.popResource(world, clickedPos, new ItemStack(Items.CHARCOAL));

            player.giveExperiencePoints(1);
            world.playSound(null, clickedPos, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 0.8F, 1.2F);

            stack.hurtAndBreak(durabilityUse*2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

            return ActionResultType.SUCCESS;
        }

        BlockPos facePos = clickedPos.relative(face);
        Block faceBlock = world.getBlockState(facePos).getBlock();

        if (clickedBlock == Blocks.FIRE) {
            world.setBlock(clickedPos, Blocks.AIR.getBlock().defaultBlockState(), 11);
            setDamage(stack, getDamage(stack) - 2);
            player.giveExperiencePoints(1);

            world.playSound(null, clickedPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1.2F);

            return ActionResultType.SUCCESS;
        } else if (clickedBlock == Blocks.TORCH || faceBlock == Blocks.TORCH) {
            return ActionResultType.PASS;
        } else if (world.isEmptyBlock(facePos) || Blocks.FIRE.getBlock().defaultBlockState().canSurvive(world, facePos)) {
            if (context.getClickedFace() == Direction.UP) {
                world.setBlock(facePos, Blocks.TORCH.defaultBlockState(), 11);
            } else if (face.getAxis().isHorizontal()) {
                Block wallTorch = Blocks.WALL_TORCH;
                BlockState torchState = wallTorch.defaultBlockState().setValue(WallTorchBlock.FACING, face);
                world.setBlock(facePos, torchState, 1);
            } else
                return ActionResultType.PASS;

            stack.hurtAndBreak(5, player, (e) -> e.broadcastBreakEvent(context.getHand()));
            player.giveExperiencePoints(1);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public DimensionType primaryDimension() {
        return DimensionType.NETHER;
    }
}
