package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.data.Weather;
import com.titanhex.goldupgrades.item.IgnitableTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class FireGoldAxe extends AxeItem implements IgnitableTool
{
    int burnTicks;
    int durabilityUse;
    protected int lightLevel = 15;
    protected Weather weather = Weather.CLEAR;
    protected DimensionType dimension = DimensionType.OVERWORLD;

    private static final UUID SUN_DAMAGE_MODIFIER = UUID.fromString("6F21A77E-F0C6-44D1-A12A-14C2D8397E9C");

    public FireGoldAxe(IItemTier tier, float atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int uInt, boolean uBoolean) {
        int currentBrightness = Math.max(world.getBrightness(LightType.BLOCK, holdingEntity.blockPosition()), world.getBrightness(LightType.SKY, holdingEntity.blockPosition()));

        if (this.lightLevel != currentBrightness) {
            this.lightLevel = currentBrightness;
            stack.setTag(stack.getTag());
        }

        if (world.isClientSide)
            return;

        Weather newWeather = Weather.getCurrentWeather(world);
        DimensionType newDimension = DimensionType.getCurrentDimension(world);

        if (this.weather != newWeather) {
            this.weather = newWeather;
            stack.setTag(stack.getTag());
        }
        if (this.dimension != newDimension) {
            this.dimension = newDimension;
            stack.setTag(stack.getTag());
        }

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    private boolean bonusConditionsMet(){
        return this.weather == Weather.CLEAR || this.dimension == DimensionType.NETHER;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float bonusSpeed = this.lightLevel * 0.01F;

        if (baseSpeed > 1.0F) {

            float speedMultiplier = 1.0F + bonusSpeed;

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    /**
     * Ignites a target LivingEntity for a short duration when hit.
     * This logic is typically called from the Item's hitEntity method.
     *
     * @param target The LivingEntity to ignite.
     */
    @Override
    public void igniteEntity(LivingEntity target) {
        target.setSecondsOnFire(2);
    }

    /**
     * Attempts to ignite a block, similar to a Flint and Steel.
     * This logic is typically called from the Item's onItemUse method.
     *
     * @param player The player performing the action.
     * @param world  The world the action is taking place in.
     * @param pos    The BlockPos of the block being targeted.
     * @return ActionResultType.SUCCESS if fire was placed, ActionResultType.PASS otherwise.
     */
    @Override
    public ActionResultType igniteBlock(PlayerEntity player, World world, BlockPos pos) {
        BlockPos firePos = pos;

        if (world.isEmptyBlock(firePos) || Blocks.FIRE.getBlock().defaultBlockState().canSurvive(world, firePos)) {

            world.playSound(
                    player,
                    firePos,
                    SoundEvents.FLINTANDSTEEL_USE,
                    SoundCategory.BLOCKS,
                    1.0F,
                    random.nextFloat() * 0.4F + 0.8F
            );

            world.setBlock(firePos, Blocks.FIRE.getBlock().defaultBlockState(), burnTicks);

            if (player != null) {
                player.getItemInHand(player.swingingArm).hurtAndBreak(durabilityUse, player, (p) -> {
                    p.broadcastBreakEvent(player.getUsedItemHand());
                });
            }

            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        return ActionResultType.PASS;
    }

    /**
     * Handles the entity hit event (Left Click on Entity).
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        igniteEntity(target);

        return true;
    }

    // --- Attribute Modifiers (Reads state from NBT) ---
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));

        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            // Check the synchronized NBT state
            if (weather == Weather.CLEAR || dimension == DimensionType.NETHER) {
                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        SUN_DAMAGE_MODIFIER,
                        "Weapon modifier",
                        1,
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }

        return builder.build();
    }

    // --- Tooltip Display (Reads state from NBT) ---
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        // Read the synchronized NBT state for display
        boolean isClearSkyActive = weather == Weather.CLEAR;
        boolean isNetherActive = dimension == DimensionType.NETHER;
        boolean isDamageBonusActive = isClearSkyActive || isNetherActive;

        if (isDamageBonusActive) {
            tooltip.add(new StringTextComponent("§aActive: Damage Bonus (+1)"));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Damage Bonus (Requires Clear Skies or Nether)"));
        }

        tooltip.add(new StringTextComponent("§eHarvest Speed +" + this.lightLevel + "%."));
    }

    /**
     * Handles the block use event (Right Click) and contains the corrected Flint and Steel logic.
     */
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        Direction face = context.getClickedFace();
        ItemStack stack = context.getItemInHand(); // Get the ItemStack directly from the context
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);

        if (clickedState.is(BlockTags.LOGS)) {
            if (!world.isClientSide) {
                world.removeBlock(clickedPos, false);

                Block.popResource(world, clickedPos, new ItemStack(Items.CHARCOAL));

                if (player != null) {
                    player.giveExperiencePoints(1);
                    world.playSound(null, clickedPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1.2F);
                }

                stack.hurtAndBreak(durabilityUse*2, player, (p) -> p.broadcastBreakEvent(context.getHand()));

                return ActionResultType.SUCCESS;
            }
            return ActionResultType.SUCCESS;
        }

        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        BlockPos facePos = clickedPos.relative(face);

        if (world.isEmptyBlock(facePos) || Blocks.FIRE.getBlock().defaultBlockState().canSurvive(world, facePos)) {
            world.setBlock(facePos, Blocks.TORCH.defaultBlockState(), 11);
            stack.hurtAndBreak(5, player, (e) -> e.broadcastBreakEvent(context.getHand()));
            player.giveExperiencePoints(1);

            return ActionResultType.SUCCESS;
        } else if (world.getBlockState(facePos).getBlock() == Blocks.FIRE) {
            world.setBlock(facePos, Blocks.AIR.getBlock().defaultBlockState(), 11);
            setDamage(stack, getDamage(stack) + 2);
            player.giveExperiencePoints(1);

            world.playSound(null, clickedPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8F, 1.2F);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
