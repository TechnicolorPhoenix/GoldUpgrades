package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.custom.inter.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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

public class FireGoldPickaxe extends PickaxeItem implements ILevelableItem, IIgnitableTool, IDimensionInfluencedItem, IDayInfluencedItem, IWeatherInfluencedItem, ILightInfluencedItem
{
    int burnTicks;
    int durabilityUse;

    public FireGoldPickaxe(IItemTier tier, int atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, Entity holdingEntity, int uInt, boolean uBoolean) {
        int currentBrightness = getLightLevel(stack, world, holdingEntity.blockPosition());

        int oldBrightness = getLightLevel(stack);

        if (oldBrightness != currentBrightness) {
            setLightLevel(stack, currentBrightness);
        }

        if (world.isClientSide)
            return;

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

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
        int weatherBoosterLevel = getWeatherBoosterEnchantmentLevel(usedStack);
        int minersLuck = (int) miningEntity.getAttributeValue(Attributes.LUCK);

        boolean isBadlands = Objects.equals(world.getBiome(blockPos).getRegistryName(), Biomes.BADLANDS.location());
        boolean minedStone = blockState.is(BlockTags.BASE_STONE_OVERWORLD);

        if (isBadlands && weatherBoosterLevel > 0 && minedStone && isClear(usedStack, world)) {
            int luckAdjustedRollRange = 13 - weatherBoosterLevel - minersLuck;
            int finalRollRange = Math.max(2, luckAdjustedRollRange);

            if (world.getRandom().nextInt(finalRollRange) == 0) {

                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;

                    ItemStack bonusDrop = new ItemStack(Items.REDSTONE, 1);
                    Block.popResource(world, blockPos, bonusDrop);

                    int bonusExp = blockState.getExpDrop(serverWorld, blockPos, 0, 0) + 5;

                    double x = blockPos.getX() + 0.5D;
                    double y = blockPos.getY() + 0.5D;
                    double z = blockPos.getZ() + 0.5D;

                    ExperienceOrbEntity expOrb = new net.minecraft.entity.item.ExperienceOrbEntity(
                            world,
                            x, y, z,
                            bonusExp
                    );

                    int count = 10;
                    double xz_variance = 0.2D;
                    double y_velocity = 0.5D;

                    serverWorld.sendParticles(
                            ParticleTypes.ENCHANT,
                            x, y, z,
                            count,
                            xz_variance,
                            0.0D,
                            xz_variance,
                            y_velocity
                    );

                    serverWorld.addFreshEntity(expOrb);
                }
            }
        }

        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
    }

    private float calculateBonusDestroySpeed(ItemStack stack) {
        int lightLevel = getLightLevel(stack);
        float weatherBoosterLevel = getWeatherBoosterEnchantmentLevel(stack);

        return (lightLevel > 7 ? 0.15F : 0.00F) + weatherBoosterLevel/100;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        if (baseSpeed > 1.0F) {
            float bonus = calculateBonusDestroySpeed(stack);
            float speedMultiplier = 1.0F + bonus;

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    // --- Tooltip Display (Reads state from NBT) ---
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        int lightLevel = getLightLevel(stack);
        int weatherBoosterLevel = getWeatherBoosterEnchantmentLevel(stack);
        double bonus = calculateBonusDestroySpeed(stack)*100;

        if (lightLevel > 7)
            tooltip.add(new StringTextComponent("§9+" + bonus + "% Harvest Speed ."));

        if (weatherBoosterLevel > 0)
            tooltip.add(new StringTextComponent("§eMining stone in the badlands yields treasure during clear weather."));
    }

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    @Override
    public void igniteEntity(LivingEntity target, ItemStack stack) {
        target.setSecondsOnFire(2);
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

            // Play sound effect
            world.playSound(player, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);

            // Set the block to fire
            world.setBlock(firePos, Blocks.FIRE.getBlock().defaultBlockState(), burnTicks);

            // Damage the tool item
            if (player != null) {
                player.getItemInHand(player.getUsedItemHand()).hurtAndBreak(durabilityUse, player, (p) -> {
                    p.broadcastBreakEvent(player.getUsedItemHand());
                });
            }

            return ActionResultType.sidedSuccess(world.isClientSide); // ActionResultType.success(world.isClientSide)
        }

        return ActionResultType.PASS;
    }

    /**
     * Handles the entity hit event (Left Click on Entity).
     */
    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        // 1. Call the interface method to apply the custom effect
        igniteEntity(target, stack);

        // 2. Damage the tool item (separate from combat damage)
        stack.hurtAndBreak(1, attacker, (e) -> e.broadcastBreakEvent(attacker.getUsedItemHand()));

        // Return true to indicate the attack was successful
        return true;
    }

    /**
     * Handles the block use event (Right Click).
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();

        if (world.isClientSide)
            return super.useOn(context);

        PlayerEntity player = context.getPlayer();
        if (player == null)
            return super.useOn(context);

        Direction face = context.getClickedFace();
        ItemStack stack = context.getItemInHand();
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
