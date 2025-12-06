package com.titanhex.goldupgrades.item.custom.tools.sea;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectAxe;
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
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.potion.Effect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SeaGoldAxe extends EffectAxe {

    int durabilityCost;
    private static final String NBT_SUBMERGED = "ArmorSubmerged";
    private static final String NBT_IN_RAIN = "ArmorInRain";

    private static final UUID SEA_DAMAGE_MODIFIER = UUID.fromString("6F21A77E-F0C6-44D1-A12A-14C2D8397E9C");

    private boolean getSubmerged(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_SUBMERGED);
    }
    private void setSubmerged(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_SUBMERGED, value);
    }

    private boolean getInRain(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(NBT_IN_RAIN);
    }
    private void setInRain(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putBoolean(NBT_IN_RAIN, value);
    }

    /**
     * Constructor for the Sea Gold Pickaxe.
     * * @param tier The material tier of the pickaxe.
     *
     * @param tier
     * @param attackDamage         The base attack damage of the tool.
     * @param attackSpeed          The attack speed modifier of the tool.
     * @param effectAmplifications A map where keys are the Effect and values are the amplification level (1 for Level I, 2 for Level II, etc.).
     * @param effectDuration       The duration of the effects in ticks (20 ticks = 1 second).
     * @param durabilityCost       The number of durability points to subtract on each use.
     * @param properties           Item properties.
     */
    public SeaGoldAxe(IItemTier tier, float attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed, effectAmplifications, effectDuration, durabilityCost, properties);
        this.durabilityCost = durabilityCost;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));

        // Check the synchronized NBT state
        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            if (getInRain(stack) || getSubmerged(stack)) {
                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        SEA_DAMAGE_MODIFIER,
                        "Weapon modifier",
                        1,
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }

        return builder.build();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity holdingEntity, int unknownInt, boolean unknownConditional) {
        if (world.isClientSide) return;

        if (!(holdingEntity instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity) holdingEntity;
        boolean isEquipped = livingEntity.getItemBySlot(EquipmentSlotType.MAINHAND) == stack;

        boolean isSubmergedNow = holdingEntity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
        boolean isInRainOrWaterNow = holdingEntity.isInWaterOrRain();
        boolean isInRainNow = isInRainOrWaterNow && !isSubmergedNow;

        boolean oldSubmerged = this.getSubmerged(stack);
        boolean oldInRain = this.getInRain(stack);

        boolean shouldRefresh = false;

        if (isInRainNow != oldInRain || isSubmergedNow != oldSubmerged) {
            setInRain(stack, isInRainNow);
            setSubmerged(stack, isSubmergedNow);

            stack.setTag(stack.getTag());
            if (isEquipped) {
                shouldRefresh = true;
            }
        }

        ModifiableAttributeInstance attackInstance = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);

        if (!isEquipped && attackInstance != null) {
            if (attackInstance.getModifier(SEA_DAMAGE_MODIFIER) != null)
                shouldRefresh = true;
        }

        if (shouldRefresh && holdingEntity instanceof LivingEntity) {

            Multimap<Attribute, AttributeModifier> newModifiers = this.getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);

            if (attackInstance != null) {
                attackInstance.removeModifier(SEA_DAMAGE_MODIFIER);
            }

            if (isEquipped) {
                for (Map.Entry<Attribute, AttributeModifier> entry : newModifiers.entries()) {
                    ModifiableAttributeInstance instance = livingEntity.getAttribute(entry.getKey());
                    if (instance != null) {
                        if (entry.getValue().getId().equals(SEA_DAMAGE_MODIFIER))
                        {
                            instance.addTransientModifier(entry.getValue());
                        }
                    }
                }
            }
        }

        super.inventoryTick(stack, world, holdingEntity, unknownInt, unknownConditional);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        boolean inRain = getInRain(stack);
        boolean submerged = getSubmerged(stack);

        if (inRain && submerged) {
            tooltip.add(new StringTextComponent("§aActive: Harvest Speed +20%."));
        } else if (inRain || submerged) {
            tooltip.add(new StringTextComponent("§aActive: Harvest Speed +12%."));
        } else {
            tooltip.add(new StringTextComponent("§cInactive: Water required for harvest bonus."));
        }
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float bonusSpeed = getInRain(stack) ? getSubmerged(stack) ? 20.0F : 12.0F : getSubmerged(stack) ? 12.0F : 0F;

        if (baseSpeed > 1.0F) {

            float speedMultiplier = 1.0F + bonusSpeed;

            return baseSpeed * speedMultiplier;
        }

        return baseSpeed;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        RayTraceResult hitResult = world.clip(
                new RayTraceContext(
                        player.getEyePosition(1.0F),
                        player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)),
                        RayTraceContext.BlockMode.OUTLINE,
                        RayTraceContext.FluidMode.ANY,
                        player
                )
        );

        if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = ((BlockRayTraceResult) hitResult).getBlockPos();
            BlockState hitState = world.getBlockState(hitPos);

            if (hitState.getBlock() == Blocks.WATER ||
                    hitState.getBlock() == Blocks.ICE ||
                    hitState.getBlock() == Blocks.PACKED_ICE ) {

                if (!getSubmerged(stack))
                    return ActionResult.pass(stack);
            }
        }

        return super.use(world, player, hand);
    }

    /**
     * Handles the item use event (Right Click) with custom logic for water/ice
     * conversion, falling back to the parent class's aura application.
     */
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockPos hitPos = context.getClickedPos();

        if (!world.isClientSide) {

            RayTraceResult hitResult = world.clip(
                    new RayTraceContext(
                            player.getEyePosition(1.0F),
                            player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)),
                            RayTraceContext.BlockMode.OUTLINE,
                            RayTraceContext.FluidMode.SOURCE_ONLY,
                            player
                    )
            );

            BlockState state = world.getBlockState(hitPos);

            if (state.getBlock() == Blocks.ICE) {

                world.setBlock(hitPos, Blocks.PACKED_ICE.defaultBlockState(), 11);

                world.playSound(null, hitPos, SoundEvents.STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);

                if (player != null) {
                    stack.hurtAndBreak(durabilityCost/2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                }

                return ActionResultType.SUCCESS;
//            } else if (state.getBlock() == Blocks.PACKED_ICE) {
//
//                world.setBlock(hitPos, Blocks.BLUE_ICE.defaultBlockState(), 11);
//
//                world.playSound(null, hitPos, SoundEvents.STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);
//
//                if (player != null) {
//                    stack.hurtAndBreak(durabilityCost, player, (p) -> p.broadcastBreakEvent(context.getHand()));
//                }
//
//                return ActionResultType.SUCCESS;
            } else if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockHit = (BlockRayTraceResult) hitResult;
                BlockPos rayHitPos = blockHit.getBlockPos();
                BlockState rayHitState = world.getBlockState(rayHitPos);

                if (rayHitState.getBlock() == Blocks.WATER) {
                    world.setBlock(rayHitPos, Blocks.ICE.defaultBlockState(), 11);

                    world.playSound(null, rayHitPos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.5F);

                    if (player != null) {
                        stack.hurtAndBreak(durabilityCost / 2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                    }

                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }
}
