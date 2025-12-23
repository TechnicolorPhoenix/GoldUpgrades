package com.titanhex.goldupgrades.item.components;

import com.titanhex.goldupgrades.item.custom.inter.IWeatherInfluencedItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
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
import java.util.function.Supplier;

public class TreasureToolComponent {

    BasicParticleType treasureParticle = ParticleTypes.CRIT;
    SoundEvent treasureFoundSound = SoundEvents.ENCHANTMENT_TABLE_USE;

    public TreasureToolComponent(){

    }
    public TreasureToolComponent(BasicParticleType treasureParticle, SoundEvent treasureFoundSound){
        this.treasureParticle = treasureParticle;
        this.treasureFoundSound = treasureFoundSound;
    }

    public void tryMonsterSpawn(World world, BlockPos blockPos, BlockState state, LivingEntity miner, ItemStack tool,
                                ResourceLocation targetBiome,
                                Supplier<Boolean> blockMatcher,
                                Supplier<Boolean> environmentCheck,
                                Entity spawnableEntity, int baseRoll) {

        boolean isCorrectBiome = Objects.equals(world.getBiome(blockPos).getRegistryName(), targetBiome);
        if (!isCorrectBiome) return;

        boolean isCorrectBlock = blockMatcher.get();
        if (!isCorrectBlock) return;

        boolean isCorrectEnvironment = environmentCheck.get();
        if (!isCorrectEnvironment) return;

        int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(tool);
        if (weatherBoosterLevel <= 0) return;

        int minersLuck = (int) miner.getAttributeValue(Attributes.LUCK);
        int luckAdjustedRollRange = baseRoll - weatherBoosterLevel - minersLuck;
        int finalRollRange = Math.max(2, luckAdjustedRollRange);

        int calculatedRoll = world.getRandom().nextInt(finalRollRange);

        if (calculatedRoll == 0) {
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) world;

                double x = blockPos.getX() + 0.5D;
                double y = blockPos.getY() + 0.5D;
                double z = blockPos.getZ() + 0.5D;

                spawnableEntity.setPos(x, y - 0.5F, z);

                if (world.getRandom().nextInt(2) == 0) {
                    ItemStack bonusDrop = new ItemStack(Items.MAGMA_CREAM, 1);
                    Block.popResource(world, blockPos, bonusDrop);
                }

                serverWorld.addFreshEntity(spawnableEntity);

                int bonusExp = state.getExpDrop(serverWorld, blockPos, 0, 0) + 5;

                ExperienceOrbEntity expOrb = new ExperienceOrbEntity(
                        world,
                        x, y, z,
                        bonusExp
                );

                serverWorld.addFreshEntity(expOrb);
            }
        }
    }

    public void tryDropTreasure(World world, BlockPos pos, BlockState state, LivingEntity miner,
                                ItemStack tool, Supplier<Boolean> blockMatcher,
                                ResourceLocation lootTableID, int baseRoll) {

        if (world.isClientSide || !(world instanceof ServerWorld)) return;

        if (!blockMatcher.get()) return;

        int weatherBoosterLevel = IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(tool); // Retrieve this from your enchantment helper logic
        if (weatherBoosterLevel <= 0) return;

        int minersLuck = (int) miner.getAttributeValue(Attributes.LUCK);
        int calculatedRoll = Math.max(2, baseRoll - weatherBoosterLevel - minersLuck);

        ServerWorld serverWorld = (ServerWorld) world;

        LootTable table = serverWorld.getServer().getLootTables().get(lootTableID);

        LootContext.Builder builder = new LootContext.Builder(serverWorld)
                .withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(pos))
                .withParameter(LootParameters.BLOCK_STATE, state)
                .withParameter(LootParameters.TOOL, tool)
                .withParameter(LootParameters.THIS_ENTITY, miner)
                .withLuck(minersLuck);

        List<ItemStack> drops = table.getRandomItems(builder.create(LootParameterSets.BLOCK));

        if (!drops.isEmpty()){
            for (ItemStack drop : drops) {
                Block.popResource(world, pos, drop);
            }

            spawnTreasureParticlesAndXP(serverWorld, pos, state);
        }
    }

    private void spawnTreasureParticlesAndXP(ServerWorld world, BlockPos pos, BlockState state) {
        int bonusExp = state.getExpDrop(world, pos, 0, 0) + 5;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;

        ExperienceOrbEntity expOrb = new ExperienceOrbEntity(world, x, y, z, bonusExp);

        world.sendParticles(treasureParticle, x, y, z, 10, 0.2D, 0.0D, 0.2D, 0.5D);
        world.playSound(null, pos, treasureFoundSound, SoundCategory.BLOCKS, 0.66F, 0.8F + world.random.nextFloat() * 0.4F );
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
