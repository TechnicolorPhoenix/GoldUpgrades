package com.titanhex.goldupgrades.item.components;

import com.titanhex.goldupgrades.data.MoonPhase;
import com.titanhex.goldupgrades.item.custom.inter.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ObsidianToolComponent {
    public static final UUID NIGHT_DAMAGE_UUID = UUID.randomUUID();
    private final Random RANDOM = new Random();

    public ObsidianToolComponent(){}

    public int getItemEnchantability(ItemStack stack) {
        IMoonPhaseInfluencedItem moonPhaseItem = (IMoonPhaseInfluencedItem) stack.getItem();
        return moonPhaseItem.getMoonPhaseValue(stack);
    }

    public double getEffectChance(ItemStack stack){
        Item item = stack.getItem();
        IMoonPhaseInfluencedItem moonItem = (IMoonPhaseInfluencedItem) item;
        ILevelableItem levelableItem = (ILevelableItem) item;

        double phaseValue = moonItem.getMoonPhaseValue(stack);

        return phaseValue*(2+ levelableItem.getItemLevel());
    }

    public double getObsidianDamage(ItemStack stack){
        Item item = stack.getItem();
        ILevelableItem levelableItem = (ILevelableItem) item;
        IMoonPhaseInfluencedItem moonPhaseItem = (IMoonPhaseInfluencedItem) item;
        float phaseValue = moonPhaseItem.getMoonPhaseValue(stack);

        return phaseValue / 4 * levelableItem.getItemLevel();
    }

    public float getDestroySpeed(ItemStack stack, float baseSpeed){
        IDayInfluencedItem dayItem = (IDayInfluencedItem) stack.getItem();
        boolean isNight = dayItem.isNight(stack);
        float bonusSpeed = isNight ? 0.15F : 0F;

        if (ILightInfluencedItem.getLightLevel(stack) == 0) {
            baseSpeed = 1.1F + bonusSpeed;
        }

        if (baseSpeed > 1.0F) {
            float speedMultiplier = 1.0F + bonusSpeed;
            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    public void hurtEnemy(ItemStack stack, LivingEntity target, Effect effect) {
        Item item = stack.getItem();
        ILevelableItem levelableItem = (ILevelableItem) item;
        IMoonPhaseInfluencedItem moonPhaseItem = (IMoonPhaseInfluencedItem) item;

        double phaseChance = getEffectChance(stack);
        int calculatedRoll = RANDOM.nextInt(100) + 1;

        if (calculatedRoll <= phaseChance) {
            target.addEffect(new EffectInstance(
                    effect, 140, 0, true, true
            ));
        }
    }

        public void appendHoverText(World worldIn, ItemStack stack, List<ITextComponent> tooltip){
        Item item = stack.getItem();
        IMoonPhaseInfluencedItem moonPhaseItem = (IMoonPhaseInfluencedItem) item;
        IDayInfluencedItem nightItem = (IDayInfluencedItem) item;
        int phaseValue = moonPhaseItem.getMoonPhaseValue(stack, MoonPhase.getCurrentMoonPhase(worldIn));
        boolean isNight = nightItem.isNight(stack, worldIn);

        if (phaseValue < 0)
            tooltip.add(new StringTextComponent("§aEnchantment Boost from Moon"));
        else
            tooltip.add(new StringTextComponent("§9+" + getItemEnchantability(stack) + " Enchantment Bonus"));

        if (ILightInfluencedItem.getLightLevel(stack) == 0)
            tooltip.add(new StringTextComponent("§eHarvest Anything."));

        if (isNight)
            tooltip.add(new StringTextComponent("§9+" + (getDestroySpeed(stack, 1.0F) - 1.0D) + "% Harvest Speed."));
        else
            tooltip.add(new StringTextComponent("§cInactive: Harvest Speed Bonus (Requires Night)"));
        }
    public boolean canHarvestBlock(ItemStack itemStack, BlockState state, Boolean originalCheck){
        if (ILightInfluencedItem.getLightLevel(itemStack) == 0)
            if (!(state.getDestroySpeed(null, null) < 0))
                return true;

        return originalCheck;
    }

    public ActionResultType useOn(ItemUseContext context, ItemStack stack) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();

        if (world.isClientSide) return ActionResultType.PASS;
        if (player == null) return ActionResultType.PASS;

        ILevelableItem levelableItem = (ILevelableItem) context.getItemInHand().getItem();
        int repairAmount = levelableItem.getItemLevel();

        BlockRayTraceResult hitResult = world.clip(
            new RayTraceContext(
                    player.getEyePosition(1.0F),
                    player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)),
                    RayTraceContext.BlockMode.OUTLINE,
                    RayTraceContext.FluidMode.ANY,
                    player
            )
        );

        if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos rayHitPos = hitResult.getBlockPos();
            BlockState rayHitState = world.getBlockState(rayHitPos);

            if (rayHitState.getBlock() == Blocks.LAVA) {

                world.setBlock(rayHitPos, Blocks.OBSIDIAN.defaultBlockState(), 3);

                if (world.dimension() == World.NETHER) {
                    stack.hurtAndBreak((4-repairAmount)*3, player, (entity) -> entity.broadcastBreakEvent(context.getHand()));
                } else {
                    int currentDamage = stack.getDamageValue();
                    stack.setDamageValue(Math.max(0, currentDamage - repairAmount));
                }
                player.giveExperiencePoints(1);

                world.playSound(null, clickedPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);

                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.PASS;
    }

}
