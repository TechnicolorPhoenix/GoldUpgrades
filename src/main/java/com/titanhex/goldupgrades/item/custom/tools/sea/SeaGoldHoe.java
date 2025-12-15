package com.titanhex.goldupgrades.item.custom.tools.sea;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.custom.inter.IElementalHoe;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWaterInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectHoe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
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
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;

public class SeaGoldHoe extends EffectHoe implements IWeatherInfluencedItem, IWaterInfluencedItem, ILevelableItem, IElementalHoe
{

    private static final Random RANDOM = new Random();

    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier of the pickaxe.
     *
     * @param tier                  The tier enum for the item type.
     * @param attackDamage         The base attack damage of the tool.
     * @param attackSpeed          The attack speed modifier of the tool.
     * @param effectAmplifications A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration       The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost       The number of durability points to subtract on each use.
     * @param properties           Item properties.
     */
    public SeaGoldHoe(IItemTier tier, int attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed, effectAmplifications, effectDuration, durabilityCost, properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int unknownInt, boolean unknownConditional) {
        if (world.isClientSide) return;

        boolean currentSubmerged = holdingEntity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
        boolean isInRainOrWaterNow = holdingEntity.isInWaterOrRain();
        boolean currentInRain = isInRainOrWaterNow && !currentSubmerged;
        Weather currentWeather = Weather.getCurrentWeather(world);

        boolean oldSubmerged = this.getIsSubmerged(stack);
        boolean oldInRain = this.getIsInRain(stack);
        Weather oldWeather = this.getWeather(stack);

        boolean environmentalStateChanged = currentInRain != oldInRain || currentSubmerged != oldSubmerged || oldWeather != currentWeather;

        if (environmentalStateChanged) {
            setIsInRain(stack, currentInRain);
            setIsSubmerged(stack, currentSubmerged);
            setWeather(stack, currentWeather);

            if (currentInRain || currentSubmerged) {
                world.playSound(null, holdingEntity.blockPosition(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }

        if (!(holdingEntity instanceof LivingEntity))
            return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;

        int elementalHoeLevel = getElementalHoeEnchantmentLevel(stack);
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.OFFHAND) == stack;

        if (isEquipped && elementalHoeLevel > 0) {
            int duration = 60;
            if (livingEntity.tickCount % duration == 0) {
                stack.hurtAndBreak(5 - elementalHoeLevel, livingEntity,
                        (e) -> {
                            e.broadcastBreakEvent(EquipmentSlotType.OFFHAND);
                        }
                );
                livingEntity.addEffect(new EffectInstance(
                        (currentSubmerged ? Effects.DOLPHINS_GRACE : Effects.MOVEMENT_SPEED),
                        duration,
                        elementalHoeLevel - 1,
                        true,
                        true
                ));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        int elementalHoeEnchantmentLevel = getElementalHoeEnchantmentLevel(stack);

        boolean inRain = this.getIsInRain(stack);
        boolean submerged = this.getIsSubmerged(stack);
        boolean weatherIsRain = isRain(stack, worldIn);

        if (elementalHoeEnchantmentLevel > 0)
            tooltip.add(new StringTextComponent("§eHold for Dolphin's Grace, use for Water Breathing"));

        if (submerged && weatherIsRain) {
            tooltip.add(new StringTextComponent("§aActive: Harvest Speed +20%."));
        } else if (inRain || submerged) {
            tooltip.add(new StringTextComponent("§aActive: Harvest Speed +15%."));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Water required for harvest bonus."));
        }
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (!world.isClientSide) {
            int weatherBoostLevel = getWeatherBoosterEnchantmentLevel(usedStack);
            if (isRain(usedStack) && weatherBoostLevel > 0) {
                if (world.getRandom().nextInt(11-weatherBoostLevel) == 0 && blockState.is(BlockTags.LEAVES)) {
                    ItemStack bonusDrop = new ItemStack(Items.G, 1);

                    Block.popResource(world, blockPos, bonusDrop);

                    if (world instanceof ServerWorld) {
                        ServerWorld serverWorld = (ServerWorld) world;
                        BlockState state = serverWorld.getBlockState(blockPos);
                        int bonusExp = state.getExpDrop(serverWorld, blockPos, 0, 0) + 5;

                        net.minecraft.entity.item.ExperienceOrbEntity expOrb = new net.minecraft.entity.item.ExperienceOrbEntity(
                                world,
                                blockPos.getX() + 0.5D,
                                blockPos.getY() + 0.5D,
                                blockPos.getZ() + 0.5D,
                                bonusExp
                        );

                        // Spawn the entity into the world
                        serverWorld.addFreshEntity(expOrb);
                    }
                }
            }
        }

        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (getIsSubmerged(stack) && hasWaterDiverEnchantment(stack)){
            int lowestValue = RANDOM.nextInt(2);
            amount = Math.max(lowestValue, amount - getWaterDiverEnchantmentLevel(stack));
        }
        return super.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        boolean weatherIsRain = isRain(stack);
        int weatherBoostLevel = getWeatherBoosterEnchantmentLevel(stack);
        float bonusSpeed = getIsSubmerged(stack) ? 0.15F : getIsInRain(stack) ? 0.15F : 0F;

        if (weatherIsRain)
            bonusSpeed += 0.03F * weatherBoostLevel;

        if (baseSpeed > 1.0F) {

            float speedMultiplier = 1.0F + bonusSpeed;

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, @NotNull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

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
            BlockPos hitPos = hitResult.getBlockPos();
            BlockState hitState = world.getBlockState(hitPos);

            if (hitState.getBlock() == Blocks.WATER ||
                    hitState.getBlock() == Blocks.ICE ||
                    hitState.getBlock() == Blocks.PACKED_ICE ||
                    hitState.getBlock() == Blocks.BLUE_ICE) {

                return ActionResult.pass(stack);
            }
        }
        ActionResult<ItemStack> result = super.use(world, player, hand);

        if (result.getResult().consumesAction()) {
            if (!world.isClientSide) {
                ServerWorld serverWorld = (ServerWorld) world;

                // Spawn Falling Water Particles around the player
                double x = player.getX();
                double y = player.getY() + player.getBbHeight() / 2.0D;
                double z = player.getZ();

                serverWorld.sendParticles(
                        ParticleTypes.FALLING_WATER, // The particle type
                        x, y, z,                     // Position (center of the player)
                        30,                          // Count (number of particles to spawn)
                        0.5D, 0.5D, 0.5D,             // X, Y, Z displacement variance (spread)
                        0.05D                         // Speed
                );
            }
        }

        int elementalHoeLevel = getElementalHoeEnchantmentLevel(stack);
        if (elementalHoeLevel > 0) {
                player.addEffect(new EffectInstance(
                        Effects.WATER_BREATHING,
                        30*20,
                        elementalHoeLevel,
                        true,
                        true
                ));

                stack.hurtAndBreak(5*elementalHoeLevel, player, (e) -> {e.broadcastBreakEvent(hand);});

                return ActionResult.consume(stack);
            }

        return result;
    }

    /**
     * Handles the item use event (Right Click) with custom logic for water/ice
     * conversion, falling back to the parent class's aura application.
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (world.isClientSide || player == null)
            return super.useOn(context);

        // --- Server-side Logic ---
        BlockRayTraceResult hitResult = world.clip(
                new RayTraceContext(
                        player.getEyePosition(1.0F),             // Start position (player's eyes)
                        player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)), // End position (5 blocks away)
                        RayTraceContext.BlockMode.OUTLINE,       // Only hit blocks with an outline
                        RayTraceContext.FluidMode.SOURCE_ONLY,           // Crucially, hit ANY fluid block
                        player
                )
        );

        int elementalHoeLevel = getElementalHoeEnchantmentLevel(stack);
        int toolLevel = getItemLevel() + elementalHoeLevel;

        if (state.getBlock() == Blocks.SNOW) {
            world.setBlock(pos, Blocks.ICE.defaultBlockState(), 11);

            world.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.5F);

            stack.hurtAndBreak(super.baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

            return ActionResultType.SUCCESS;
        } else if (state.getBlock() == Blocks.ICE && toolLevel > 1) {
            world.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 11);

            world.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 0.8F);

            stack.hurtAndBreak(super.baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

            return ActionResultType.SUCCESS;
        } else if (state.getBlock() == Blocks.PACKED_ICE && toolLevel > 0) {
            world.setBlock(pos, Blocks.BLUE_ICE.defaultBlockState(), 11);

            world.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 0.8F);

            stack.hurtAndBreak(super.baseDurabilityCost, player, (p) -> p.broadcastBreakEvent(context.getHand()));

            return ActionResultType.SUCCESS;
        } else if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos rayHitPos = hitResult.getBlockPos();
            BlockState rayHitState = world.getBlockState(rayHitPos);

            if (rayHitState.getBlock() == Blocks.WATER) {
                world.setBlock(rayHitPos, Blocks.ICE.defaultBlockState(), 11);

                world.playSound(null, rayHitPos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.5F);

                stack.hurtAndBreak(super.baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

                return ActionResultType.SUCCESS;
            }
        }

        if (state.getBlock() instanceof IGrowable && world instanceof ServerWorld) {
            IGrowable growable = (IGrowable) state.getBlock();

            if (growable.isValidBonemealTarget(world, pos, state, world.isClientSide)) {
                growable.performBonemeal((ServerWorld) world, world.getRandom(), pos, state);

                world.playSound(null, pos, SoundEvents.HOE_TILL, SoundCategory.PLAYERS, 1.0F, 1.0F);

                stack.hurtAndBreak(super.baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                player.giveExperiencePoints(1);
                return ActionResultType.SUCCESS;
            }
        }

        if (state.getBlock() == Blocks.FARMLAND) {
            if (state.hasProperty(BlockStateProperties.MOISTURE)) {
                int currentMoisture = state.getValue(BlockStateProperties.MOISTURE);

                if (currentMoisture < 7) {
                    world.setBlock(pos, state.setValue(BlockStateProperties.MOISTURE, 7), 2);

                    world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    stack.hurtAndBreak(super.baseDurabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                    player.giveExperiencePoints(1);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }
}
