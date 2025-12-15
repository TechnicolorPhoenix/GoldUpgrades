package com.titanhex.goldupgrades.item.custom.tools.storm;

import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectPickaxe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        if (!world.isClientSide) {
            int weatherBoostLevel = getWeatherBoosterEnchantmentLevel(usedStack);
            if (isThundering(usedStack) && weatherBoostLevel > 0 && Objects.equals(world.getBiome(blockPos).getRegistryName(), Biomes.PLAINS.location())) {
                if (world.getRandom().nextInt(11-weatherBoostLevel) == 0 && blockState.is(BlockTags.BASE_STONE_OVERWORLD)) {
                    ItemStack bonusDrop = new ItemStack(Items.HONEYCOMB, 1);

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

        if (isThundering) {
            tooltip.add(new StringTextComponent("§9+30% Harvest Speed"));
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
