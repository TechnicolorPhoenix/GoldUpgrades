package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.enchantment.ModEnchantments;
import com.titanhex.goldupgrades.item.custom.inter.*;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
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
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class FireGoldHoe extends HoeItem implements ILevelableItem, IIgnitableTool, IDimensionInfluencedItem, IWeatherInfluencedItem, IDayInfluencedItem, ILightInfluencedItem, IElementalHoe
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

        int elementalHoeLevel = getElementalHoeEnchantmentLevel(stack);

        if (isEquipped && elementalHoeLevel > 0 && (isClear(stack) || inValidDimension(stack))) {
            int duration = 60;
            if (livingEntity.tickCount % 60 == 0)
                stack.hurtAndBreak(1, livingEntity,
                        (e) -> {
                            e.broadcastBreakEvent(EquipmentSlotType.OFFHAND);
                        }
                );
            livingEntity.addEffect(new EffectInstance(
                    Effects.FIRE_RESISTANCE,
                    duration,
                    elementalHoeLevel-1,
                    false,
                    false
            ));
        }
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);

        int weatherBoosterLevel = getWeatherBoosterEnchantmentLevel(usedStack);

        if (weatherBoosterLevel == 0) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);

        int minersLuck = (int) miningEntity.getAttributeValue(Attributes.LUCK);

        boolean isDesert = Objects.equals(world.getBiome(blockPos).getRegistryName(), Biomes.BADLANDS.location());
        boolean minedDeadBush = blockState.is(Blocks.DEAD_BUSH);

        if (isDesert && minedDeadBush && isClear(usedStack, world)) {
            int luckAdjustedRollRange = 11 - weatherBoosterLevel - minersLuck;
            int finalRollRange = Math.max(2, luckAdjustedRollRange);

            if (world.getRandom().nextInt(finalRollRange) == 0) {
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    BlockState state = serverWorld.getBlockState(blockPos);

                    double x = blockPos.getX() + 0.5D;
                    double y = blockPos.getY() + 0.5D;
                    double z = blockPos.getZ() + 0.5D;
                    
                    MagmaCubeEntity magmaCube = new MagmaCubeEntity(EntityType.MAGMA_CUBE, world);
                    magmaCube.setHealth(6F);
                    magmaCube.setPos(x, y-0.5F, z);

                    if (world.getRandom().nextInt(2) == 0) {
                        ItemStack bonusDrop = new ItemStack(Items.MAGMA_CREAM, 1);
                        Block.popResource(world, blockPos, bonusDrop);
                    }
                    
                    serverWorld.addFreshEntity(magmaCube);

                    int bonusExp = state.getExpDrop(serverWorld, blockPos, 0, 0) + 5;

                    ExperienceOrbEntity expOrb = new ExperienceOrbEntity(
                            world,
                            x, y, z,
                            bonusExp
                    );

                    serverWorld.addFreshEntity(expOrb);
                    return true;
                }
            }
        }

        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    private float calculateBonusDestroySpeed(ItemStack stack) {
        int lightLevel = getLightLevel(stack);

        return (lightLevel > 7 ? 0.15F : 0.00F) + (isClear(stack) ? (float) getWeatherBoosterEnchantmentLevel(stack)/100 : 0);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        if (baseSpeed > 1.0F) {
            float bonusSpeed = calculateBonusDestroySpeed(stack);
            float speedMultiplier = 1.0F + bonusSpeed;

            return baseSpeed * speedMultiplier;        }

        return baseSpeed;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        int lightLevel = getLightLevel(stack);
        int weatherBoosterLevel = getWeatherBoosterEnchantmentLevel(stack);

        boolean hasElementalHoeEnchantment = hasElementalHoeEnchantment(stack);

        float bonus = calculateBonusDestroySpeed(stack)*100;

        if (lightLevel > 7)
            tooltip.add(new StringTextComponent("§9+" + bonus + "% Harvest Speed"));
        if (hasElementalHoeEnchantment)
            tooltip.add(new StringTextComponent("§eHold for Fire Resistance, use for Strength"));
        if (weatherBoosterLevel > 0)
            tooltip.add(new StringTextComponent("§eCutting dead bushes in badlands yields treasure in clear weather."));
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

            stack.hurtAndBreak(10+5*elementalHoeLevel, player, (e) -> {e.broadcastBreakEvent(hand);});

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

        return IIgnitableTool.handleUseOn(context, (itemStack) -> {});
    }

    @Override
    public DimensionType primaryDimension() {
        return DimensionType.NETHER;
    }
}
