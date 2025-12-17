package com.titanhex.goldupgrades.item.components;

import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Objects;

public class TreasureToolComponent {

    public TreasureToolComponent() {}

    public void tryDropTreasure(World world, BlockPos pos, BlockState state, LivingEntity miner,
                                ItemStack tool, ResourceLocation targetBiome,
                                net.minecraft.tags.ITag.INamedTag<Block> targetBlockTag,
                                ResourceLocation lootTableID, int baseRoll) {

        if (world.isClientSide) return;

        // 1. Check Biome
        boolean isCorrectBiome = Objects.equals(world.getBiome(pos).getRegistryName(), targetBiome);
        if (!isCorrectBiome) return;

        // 2. Check Block Tag
        boolean isCorrectBlock = state.is(targetBlockTag);
        if (!isCorrectBlock) return;

        int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(tool); // Retrieve this from your enchantment helper logic

        if (weatherBoosterLevel <= 0) return;

        // 4. Calculate Chance (Luck + Enchantment)
        int minersLuck = (int) miner.getAttributeValue(Attributes.LUCK);
        int luckAdjustedRollRange = baseRoll - weatherBoosterLevel - minersLuck;
        int finalRollRange = Math.max(2, luckAdjustedRollRange);

        // 5. Roll for Treasure
        if (world.getRandom().nextInt(finalRollRange) == 0) {
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) world;
                MinecraftServer server = serverWorld.getServer();

                // Get the Loot Table
                LootTable table = server.getLootTables().get(lootTableID);

                // Build Loot Context
                LootContext.Builder builder = (new LootContext.Builder(serverWorld))
                        .withParameter(LootParameters.ORIGIN, new Vector3d(pos.getX(), pos.getY(), pos.getZ()))
                        .withParameter(LootParameters.TOOL, tool)
                        .withParameter(LootParameters.THIS_ENTITY, miner)
                        .withRandom(world.getRandom());

                // Generate Items
                List<ItemStack> drops = table.getRandomItems(builder.create(LootParameterSets.CHEST));

                // Spawn Drops
                for (ItemStack drop : drops) {
                    Block.popResource(world, pos, drop);
                }

                // Spawn Particles & XP (Preserving your original flair)
                spawnTreasureParticlesAndXP(serverWorld, pos, state);
            }
        }
    }

    private void spawnTreasureParticlesAndXP(ServerWorld world, BlockPos pos, BlockState state) {
        // Moved your particle/XP logic here to keep the main method clean
        int bonusExp = state.getExpDrop(world, pos, 0, 0) + 5;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;

        ExperienceOrbEntity expOrb = new ExperienceOrbEntity(world, x, y, z, bonusExp);

        world.sendParticles(ParticleTypes.ENCHANT, x, y, z, 10, 0.2D, 0.0D, 0.2D, 0.5D);
        world.addFreshEntity(expOrb);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, List<ITextComponent> tooltip, String treasureText) {
        if (treasureText != null && !treasureText.isEmpty()) {
            int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack);
            if (weatherBoosterLevel > 0)
                tooltip.add(new StringTextComponent(treasureText));
        }
    }

}
