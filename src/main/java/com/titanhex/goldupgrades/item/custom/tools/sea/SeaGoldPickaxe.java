package com.titanhex.goldupgrades.item.custom.tools.sea;

import com.google.gson.internal.bind.JsonTreeReader;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWaterInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectPickaxe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class SeaGoldPickaxe extends EffectPickaxe implements IWaterInfluencedItem, IWeatherInfluencedItem, ILevelableItem
{

    private static final Random RANDOM = new Random();

    /**
     * Constructor for the AuraPickaxe.
     * * @param tier The material tier of the pickaxe.
     *
     * @param tier                  The tier enum for the armor type.
     * @param attackDamage         The base attack damage of the tool.
     * @param attackSpeed          The attack speed modifier of the tool.
     * @param effectAmplifications A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration       The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost       The number of durability points to subtract on each use.
     * @param properties           Item properties.
     */
    public SeaGoldPickaxe(IItemTier tier, int attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
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
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        boolean inRain = this.getIsInRain(stack);
        boolean submerged = this.getIsSubmerged(stack);
        boolean weatherIsRain = this.getWeather(stack) == Weather.RAINING;

        if (submerged && weatherIsRain) {
            tooltip.add(new StringTextComponent("§aActive: Harvest Speed +20%."));
        } else if (inRain || submerged) {
            tooltip.add(new StringTextComponent("§aActive: Harvest Speed +15%."));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Water required for harvest bonus."));
        }
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
        boolean weatherIsRain = this.getWeather(stack) == Weather.RAINING;
        float bonusSpeed = getIsSubmerged(stack) ? weatherIsRain ? 0.20F : 0.15F : getIsInRain(stack) ? 0.15F : 0F;

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
                    hitState.getBlock() == Blocks.PACKED_ICE) {

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

        return result;
    }

    /**
     * Handles the item use event (Right Click) with custom logic for water/ice
     * conversion, falling back to the parent class's aura application.
     */
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (world.isClientSide || player == null)
            return super.useOn(context);

        BlockRayTraceResult hitResult = world.clip(
                new RayTraceContext(
                        player.getEyePosition(1.0F),             // Start position (player's eyes)
                        player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)), // End position (5 blocks away)
                        RayTraceContext.BlockMode.OUTLINE,       // Only hit blocks with an outline
                        RayTraceContext.FluidMode.SOURCE_ONLY,           // Crucially, hit ANY fluid block
                        player
                )
        );

        int toolLevel = getItemLevel();

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
        } else if (state.getBlock() == Blocks.PACKED_ICE && toolLevel > 2) {
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
        return ActionResultType.PASS;
    }
}
