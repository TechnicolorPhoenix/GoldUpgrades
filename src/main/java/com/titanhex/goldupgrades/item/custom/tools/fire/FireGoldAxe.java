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
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
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
import java.util.Map;
import java.util.UUID;

public class FireGoldAxe extends AxeItem implements IgnitableTool
{
    int burnTicks;
    int durabilityUse;

    private static final String NBT_NETHER = "WeaponInNether";
    private static final String NBT_IN_CLEAR = "WeaponInClearWeather";
    private static final String NBT_LIGHT_LEVEL = "WeaponLightLevel";

    private static final UUID SUN_DAMAGE_MODIFIER = UUID.fromString("6F21A77E-F0C6-44D1-A12A-14C2D8397E9C");

    public FireGoldAxe(IItemTier tier, float atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
    }

    private boolean getNether(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_NETHER);
    }
    private void setNether(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_NETHER, value);
    }

    private boolean getInClear(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IN_CLEAR);
    }
    private void setInClear(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IN_CLEAR, value);
    }

    private int getLightLevel(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_LIGHT_LEVEL);
    }
    private void setLightLevel(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(NBT_LIGHT_LEVEL, value);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int uInt, boolean uBoolean) {
        int currentBrightness = world.getRawBrightness(holdingEntity.blockPosition(), 0);

        int oldBrightness = getLightLevel(stack);

        if (oldBrightness != currentBrightness) {
            setLightLevel(stack, currentBrightness);
        }

        if (world.isClientSide)
            return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND) == stack;

        boolean shouldRefresh = false;

        boolean weatherIsClear = Weather.getCurrentWeather(world) == Weather.CLEAR;
        boolean dimensionIsNether = DimensionType.getCurrentDimension(world) == DimensionType.NETHER;

        boolean oldNether = this.getNether(stack);
        boolean oldInClear = this.getInClear(stack);

        if (oldInClear != weatherIsClear || oldNether != dimensionIsNether) {
            setInClear(stack, weatherIsClear);
            setNether(stack, dimensionIsNether);

            stack.setTag(stack.getTag());
            if (isEquipped) {
                shouldRefresh = true;
            }
        }

        ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

        if (!isEquipped && attackInstance != null)
            if (attackInstance.getModifier(SUN_DAMAGE_MODIFIER) != null)
                shouldRefresh = true;

        if (shouldRefresh && holdingEntity instanceof LivingEntity) {

            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);

            if (attackInstance != null) {
                attackInstance.removeModifier(SUN_DAMAGE_MODIFIER);
            }

            if (isEquipped) {
                for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                    ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                    if (instance != null) {
                        if (entry.getValue().getId().equals(SUN_DAMAGE_MODIFIER))
                        {
                            instance.addTransientModifier(entry.getValue());
                        }
                    }
                }
            }
        }

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float bonusSpeed = getLightLevel(stack) * 0.01F;

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
    public void igniteEntity(LivingEntity target, ItemStack stack) {
        target.setSecondsOnFire(burnTicks);
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
        igniteEntity(target, stack);

        return true;
    }

    // --- Attribute Modifiers (Reads state from NBT) ---
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));

        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            // Check the synchronized NBT state
            if (getInClear(stack) || getNether(stack)) {
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

        int lightLevel = getLightLevel(stack);

        if (!getNether(stack) && !getInClear(stack)) {
            tooltip.add(new StringTextComponent("§cInactive: Damage Bonus (Requires Clear Skies or Nether)"));
        }

        tooltip.add(new StringTextComponent((lightLevel > 0 ? "§9" : "§c") + "+" + lightLevel + "% Harvest Speed"));
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
