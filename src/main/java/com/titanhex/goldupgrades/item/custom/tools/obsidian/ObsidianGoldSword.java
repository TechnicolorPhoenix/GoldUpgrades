package com.titanhex.goldupgrades.item.custom.tools.obsidian;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.data.MoonPhase;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
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

public class ObsidianGoldSword extends SwordItem
{
    // Defines the amount of durability to restore when absorbing lava
    private final int repairAmount;
    private static final String NBT_LIGHT_LEVEL = "WeaponLightLevel";
    private static final String NBT_IS_NIGHT = "WeaponNight";
    private static final String NBT_MOON_PHASE = "WeaponMoonPhase";
    public static final UUID NIGHT_DAMAGE_UUID = UUID.fromString("f2d3d9e0-3e3a-4a8f-9a4a-3b6b6b6b6b6b");

    public ObsidianGoldSword(IItemTier tier, int atkDamage, float atkSpeed, int repairAmount, Properties itemProperties) {
        super(tier, atkDamage, atkSpeed, itemProperties);
        this.repairAmount = repairAmount;
    }

    private int getLightLevel(ItemStack stack) {
        return stack.getOrCreateTag().getInt(NBT_LIGHT_LEVEL);
    }
    private void setLightLevel(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(NBT_LIGHT_LEVEL, value);
    }

    private boolean getNight(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IS_NIGHT);
    }
    private void setNight(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IS_NIGHT, value);
    }
    private MoonPhase getMoonPhase(ItemStack stack) {
        return MoonPhase.getMoonPhaseByString(stack.getOrCreateTag().getString(NBT_MOON_PHASE));
    }
    private void setMoonPhase(ItemStack stack, MoonPhase value) {
        stack.getOrCreateTag().putString(NBT_MOON_PHASE, value.name());
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, World world, @NotNull Entity holdingEntity, int uInt, boolean uBoolean) {
        if (world.isClientSide)
            return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND) == stack;

        int currentBrightness = world.getRawBrightness(holdingEntity.blockPosition(), 0);
        MoonPhase currentMoonPhase = MoonPhase.getCurrentMoonPhase(world);
        boolean currentNight = world.isNight() ;

        int oldBrightness = getLightLevel(stack);
        MoonPhase oldMoonPhase = this.getMoonPhase(stack);
        boolean oldNight = getNight(stack);

        boolean shouldRefresh = false;

        ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

        if (isEquipped && attackInstance != null)
            if (attackInstance.getModifier(NIGHT_DAMAGE_UUID) != null)
                shouldRefresh = oldMoonPhase != currentMoonPhase;

        if (currentNight != oldNight || oldMoonPhase != currentMoonPhase || oldBrightness != currentBrightness) {
            setLightLevel(stack, currentBrightness);
            setMoonPhase(stack, currentMoonPhase);
            setNight(stack, currentNight);

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
        float bonusSpeed = getNight(stack) ? 0.15F : 0;

        if (getLightLevel(stack) == 0) {
            baseSpeed = 1.25F;
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

        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            float damage = 0F;
            switch (getMoonPhase(stack)){
                case FULL_MOON:
                    damage = 1F * this.repairAmount;
                    break;
                case WAXING_GIBBOUS:
                case WANING_GIBBOUS:
                    damage = 0.75F * this.repairAmount;
                    break;
                case FIRST_QUARTER:
                case LAST_QUARTER:
                    damage = 0.5F * this.repairAmount;
                    break;
                case WANING_CRESCENT:
                case WAXING_CRESCENT:
                    damage = 0.25F * this.repairAmount;
                    break;
                case NEW_MOON:
                    return builder.build();
            }
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                    NIGHT_DAMAGE_UUID,
                    "Weapon modifier",
                    damage,
                    AttributeModifier.Operation.ADDITION
            ));
        }

        return builder.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (getMoonPhase(stack) == MoonPhase.NEW_MOON )
            tooltip.add(new StringTextComponent("§cInactive: Damage Bonus (Due to New Moon)"));

        if (getLightLevel(stack) == 0)
            tooltip.add(new StringTextComponent("§eHarvest Anything."));

        if (getNight(stack))
            tooltip.add(new StringTextComponent("§a+15% Harvest Speed ."));
        else
            tooltip.add(new StringTextComponent("§cInactive: Harvest Speed Bonus (Requires Night)"));
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
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();

        if (!world.isClientSide && player != null && world.dimension() != World.NETHER) {

            RayTraceResult hitResult = world.clip(
                    new RayTraceContext(
                            player.getEyePosition(1.0F),
                            player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)),
                            RayTraceContext.BlockMode.OUTLINE,
                            RayTraceContext.FluidMode.ANY, // Check ANY block/fluid in range
                            player
                    )
            );

            // Check 2: Block is Lava source block
            if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockHit = (BlockRayTraceResult) hitResult;
                BlockPos rayHitPos = blockHit.getBlockPos();
                BlockState rayHitState = world.getBlockState(rayHitPos);

                // 1. Check for Water -> Ice conversion (Priority 1)
                if (rayHitState.getBlock() == Blocks.LAVA) {

                    // Remove Lava by setting it to air (or block if desired, but air removes the source)
                    world.setBlock(rayHitPos, Blocks.AIR.defaultBlockState(), 3);

                    // Repair Tool by LAVA_REPAIR_AMOUNT
                    int currentDamage = stack.getDamageValue();
                    stack.setDamageValue(Math.max(0, currentDamage - repairAmount)); // Sets damage to a lower value, max 0.

                    // Give EXP
                    player.giveExperiencePoints(1);

                    // Play sound (Extinguish)
                    world.playSound(null, clickedPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }
}