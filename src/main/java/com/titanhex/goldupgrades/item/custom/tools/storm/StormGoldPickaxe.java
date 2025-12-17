package com.titanhex.goldupgrades.item.custom.tools.storm;

import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectPickaxe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StormGoldPickaxe extends EffectPickaxe implements ILevelableItem, IWeatherInfluencedItem {
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
    public StormGoldPickaxe(IItemTier tier, int attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed + 1.333F, effectAmplifications, effectDuration, durabilityCost, properties);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack usedStack, @NotNull World world, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity miningEntity) {
        if (world.isClientSide) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
        int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(usedStack);

        if (weatherBoosterLevel == 0) return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
        boolean isPlains = Objects.equals(world.getBiome(blockPos).getRegistryName(), Biomes.PLAINS.location());
        boolean minedStone = blockState.is(BlockTags.BASE_STONE_OVERWORLD);

        GoldUpgrades.LOGGER.debug("IS PLAINS: {}, MINED STONE: {}", isPlains, minedStone);

        if (isPlains && minedStone && isThundering(usedStack, world)) {
            int minersLuck = (int) miningEntity.getAttributeValue(Attributes.LUCK);
            int luckAdjustedRollRange = 13 - weatherBoosterLevel - minersLuck;
            int finalRollRange = Math.max(2, luckAdjustedRollRange);
            int calculatedRoll = world.getRandom().nextInt(finalRollRange);

            GoldUpgrades.LOGGER.debug("FINAL RANGE: {}, ROLLED: {}: ", finalRollRange, calculatedRoll);
            if (calculatedRoll == 0) {

                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    int bonusExp = blockState.getExpDrop(serverWorld, blockPos, 0, 0) + 5;
//                    ItemStack bonusDrop = new ItemStack(Items.SLIME_BALL, 1);

                    double x = blockPos.getX() + 0.5D;
                    double y = blockPos.getY() + 0.5D;
                    double z = blockPos.getZ() + 0.5D;

                    SlimeEntity slime = new SlimeEntity(EntityType.SLIME, world);
                    slime.setHealth(2F);
                    slime.setPos(x, y-0.5F, z);

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
                    serverWorld.addFreshEntity(slime);
                }
            }
        }

        return super.mineBlock(usedStack, world, blockState, blockPos, miningEntity);
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
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        boolean isThundering = getWeather(stack) == Weather.THUNDERING;
        int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);

        if (weatherBoosterLevel > 0)
            tooltip.add(new StringTextComponent("§eMining stone in plains yields slime during thunderstorms."));

        if (isThundering) {
            tooltip.add(new StringTextComponent("§9+"+(30 + 5*weatherBoosterLevel)+"% Harvest Speed"));
            tooltip.add(new StringTextComponent("§eMaxed Harvest Level."));
        }
    }

    @Override
    public int getHarvestLevel(@NotNull ItemStack stack, @NotNull ToolType toolType, @Nullable PlayerEntity player, @Nullable BlockState state) {
        if (getWeather(stack) == Weather.THUNDERING)
            return 5;

        return super.getHarvestLevel(stack, toolType, player, state);
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
