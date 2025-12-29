package com.titanhex.goldupgrades.item.custom.tools.fire;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.DimensionType;
import com.titanhex.goldupgrades.item.interfaces.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FireGoldAxe extends AxeItem implements ILevelableItem, IIgnitableTool, IDimensionInfluencedItem, IWeatherInfluencedItem, ILightInfluencedItem, IDayInfluencedItem
{
    int burnTicks;
    int durabilityUse;

    private static final UUID SUN_DAMAGE_MODIFIER = UUID.randomUUID();

    public FireGoldAxe(IItemTier tier, float atkDamage, float atkSpeed, int burnTicks, int durabilityUse, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.burnTicks = burnTicks;
        this.durabilityUse = durabilityUse;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity holdingEntity, int inventorySlot, boolean isSelected) {
        calibrateLightLevel(stack, world, holdingEntity);

        if (world.isClientSide)
            return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND) == stack;

        boolean changeWeather = changeWeather(stack, world);
        boolean changeDimension = changeDimension(stack, world);
        changeDay(stack, world);

        ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

        boolean environmentChanged = false;

        if (isEquipped && attackInstance != null)
            if (attackInstance.getModifier(SUN_DAMAGE_MODIFIER) != null)
                environmentChanged = changeDimension || changeWeather;

        if (environmentChanged && holdingEntity instanceof LivingEntity) {

            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);

            attackInstance.removeModifier(SUN_DAMAGE_MODIFIER);

            for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                if (instance != null) {
                    if (entry.getValue().getId().equals(SUN_DAMAGE_MODIFIER)) {
                        instance.addTransientModifier(entry.getValue());
                    }
                }
            }
        }

        super.inventoryTick(stack, world, holdingEntity, inventorySlot, isSelected);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);

        if (baseSpeed > 1.0F) {
            float bonusSpeed = IIgnitableTool.calculateBonusDestroySpeed(stack);
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
     * Handles the entity hit event (Left Click on Entity).
     */
    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        igniteEntity(target, stack);

        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));
        boolean isDay = isDay(stack);

        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            if (isClear(stack) || inValidDimension(stack)) {
                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        SUN_DAMAGE_MODIFIER,
                        "Weapon modifier",
                        (isDay ? 2 : 1) + (isClear(stack) ? (double) IWeatherInfluencedItem.getWeatherBoosterEnchantmentLevel(stack) /2 : 0),
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }

        return builder.build();
    }

    // --- Tooltip Display (Reads state from NBT) ---
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (!inValidDimension(stack, worldIn) && !isClear(stack))
            tooltip.add(new StringTextComponent("§cInactive: Damage Bonus (Requires Clear Skies or Nether)"));

        IIgnitableTool.appendHoverText(stack, tooltip);
    }

    /**
     * Handles the block use event (Right Click) and contains the corrected Flint and Steel logic.
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        PlayerEntity player = context.getPlayer();
        if (player == null)
            return super.useOn(context);

        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);

        return IIgnitableTool.useOn(context, (sentStack) -> {
            if (clickedState.is(BlockTags.LOGS) || clickedState.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
                world.removeBlock(clickedPos, false);

                Block.popResource(world, clickedPos, new ItemStack(Items.CHARCOAL));

                player.giveExperiencePoints(1);
                world.playSound(null, clickedPos, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 0.8F, 1.2F);

                stack.hurtAndBreak(durabilityUse*2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                return ActionResultType.CONSUME;
            }
            return ActionResultType.PASS;
        });
    }

    @Override
    public DimensionType primaryDimension() {
        return DimensionType.NETHER;
    }
}
