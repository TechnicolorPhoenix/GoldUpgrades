package com.titanhex.goldupgrades.item.custom.tools.obsidian;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.MoonPhase;
import com.titanhex.goldupgrades.item.custom.inter.IDayInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.ILevelableItem;
import com.titanhex.goldupgrades.item.custom.inter.ILightInfluencedItem;
import com.titanhex.goldupgrades.item.custom.inter.IMoonPhaseInfluencedItem;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ObsidianGoldAxe extends AxeItem implements ILightInfluencedItem, IDayInfluencedItem, IMoonPhaseInfluencedItem, ILevelableItem
{
    private final int repairAmount;
    public static final UUID NIGHT_DAMAGE_UUID = UUID.randomUUID();

    public ObsidianGoldAxe(IItemTier tier, float atkDamage, float atkSpeed, int repairAmount, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.repairAmount = repairAmount;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return super.getItemEnchantability(stack) + getMoonPhaseValue(getMoonPhase(stack));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int uInt, boolean uBoolean) {
        if (world.isClientSide)
            return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND) == stack;

        int currentBrightness = world.getRawBrightness(holdingEntity.blockPosition(), 0);
        MoonPhase currentMoonPhase = MoonPhase.getCurrentMoonPhase(world);
        boolean currentIsDay = isDay(stack, world);

        int oldBrightness = getLightLevel(stack);
        MoonPhase oldMoonPhase = this.getMoonPhase(stack);
        boolean oldIsDay = isDay(stack);

        boolean shouldRefresh = false;

        ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

        if (isEquipped && attackInstance != null)
            if (attackInstance.getModifier(NIGHT_DAMAGE_UUID) != null)
                shouldRefresh = oldMoonPhase != currentMoonPhase;

        if (currentIsDay != oldIsDay || oldMoonPhase != currentMoonPhase || oldBrightness != currentBrightness) {
            setLightLevel(stack, currentBrightness);
            setMoonPhase(stack, currentMoonPhase);
            setIsDay(stack, currentIsDay);

            stack.setTag(stack.getTag());
        }

        if (shouldRefresh && holdingEntity instanceof LivingEntity) {

            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);

            attackInstance.removeModifier(NIGHT_DAMAGE_UUID);

            for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                if (instance != null) {
                    if (entry.getValue().getId().equals(NIGHT_DAMAGE_UUID)) {
                        instance.addTransientModifier(entry.getValue());
                    }
                }
            }
        }

        super.inventoryTick(stack, world, holdingEntity, uInt, uBoolean);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float bonusSpeed = isNight(stack) ? 0.15F : 0F;

        if (getLightLevel(stack) == 0) {
            baseSpeed = 1.1F + bonusSpeed;
        }

        if (baseSpeed > 1.0F) {
            float speedMultiplier = 1.0F + bonusSpeed;
            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));
        float phaseValue = getMoonPhaseValue(getMoonPhase(stack));
        int itemLevel = this.getItemLevel();

        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            float damage = phaseValue / 4;
            if (damage == 0)
                return builder.build();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                    NIGHT_DAMAGE_UUID,
                    "Weapon modifier",
                    damage * itemLevel,
                    AttributeModifier.Operation.ADDITION
            ));
        }

        return builder.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        int phaseValue = getMoonPhaseValue(getMoonPhase(stack));
        boolean isNight = isNight(stack, worldIn);

        if (phaseValue < 0)
            tooltip.add(new StringTextComponent("§aEnchantment Boost from Moon"));
        else
            tooltip.add(new StringTextComponent("§9+" + phaseValue + " Enchantment Level"));

        if (getLightLevel(stack) == 0)
            tooltip.add(new StringTextComponent("§eHarvest Anything."));

        if (isNight)
            tooltip.add(new StringTextComponent("§a+15% Harvest Speed."));
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        if (getLightLevel(stack) == 0)
            return true;

        return super.canHarvestBlock(stack, state);
    }

    /**
     * Handles the block use event (Right Click) with custom Pickaxe and Fire functionality.
     */
    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide) return super.useOn(context);
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();

        if (player != null) {

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
        }

        return ActionResultType.PASS;
    }
}