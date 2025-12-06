package com.titanhex.goldupgrades.item.custom.tools.sea;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.titanhex.goldupgrades.GoldUpgrades;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectPickaxe;
import com.titanhex.goldupgrades.item.custom.tools.effect.EffectSword;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
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
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SeaGoldSword extends EffectSword {

    int durabilityCost;
    private static final String NBT_SUBMERGED = "ArmorSubmerged";
    private static final String NBT_IN_RAIN = "ArmorInRain";

    private static final UUID SEA_DAMAGE_MODIFIER = UUID.fromString("6F21A77E-F0C6-44D1-A12A-14C2D8397E9C");
    /**
     * Constructor for the AuraPickaxe.
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
    public SeaGoldSword(IItemTier tier, int attackDamage, float attackSpeed, Map<Effect, Integer> effectAmplifications, int effectDuration, int durabilityCost, Properties properties) {
        super(tier, attackDamage, attackSpeed, effectAmplifications, effectDuration, durabilityCost, properties);
        this.durabilityCost = durabilityCost;
    }

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

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(equipmentSlot, stack));

        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            // Check the synchronized NBT state
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
        boolean isInRainNow = world.isRainingAt(holdingEntity.blockPosition());

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

        if (!isEquipped && attackInstance != null)
            if (attackInstance.getModifier(SEA_DAMAGE_MODIFIER) != null)
                shouldRefresh = true;

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

        boolean inRain = this.getInRain(stack);
        boolean submerged = this.getSubmerged(stack);

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
                        RayTraceContext.FluidMode.ANY, // Check ANY block/fluid in range
                        player
                )
        );

        if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = ((BlockRayTraceResult) hitResult).getBlockPos();
            BlockState hitState = world.getBlockState(hitPos);

            if (hitState.getBlock() == Blocks.WATER ||
                    hitState.getBlock() == Blocks.ICE ||
                    hitState.getBlock() == Blocks.PACKED_ICE) {

                return ActionResult.pass(stack);
            }
        }
        ActionResult<ItemStack> result = super.use(world, player, hand);

        if (result.getResult().consumesAction()) {
            if (!world.isClientSide) {
                ServerWorld serverWorld = (ServerWorld) world;

                // Spawn Falling Water Particles around the player
                double x = player.getX();
                double y = player.getY() + player.getBbHeight() / 2.0D;
                double z = player.getZ();

                serverWorld.sendParticles(
                        ParticleTypes.FALLING_WATER, // The particle type
                        x, y, z,                     // Position (center of the player)
                        30,                          // Count (number of particles to spawn)
                        0.5D, 0.5D, 0.5D,             // X, Y, Z displacement variance (spread)
                        0.05D                         // Speed
                );
            }
        }

        return result;
    }


    /**
     * Handles the item use event (Right Click) with custom logic for water/ice
     * conversion, falling back to the parent class's aura application.
     */
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        // --- Server-side Logic ---
        if (!world.isClientSide) {

            // Get the RayTraceResult from the context, which holds the precise block hit.
            RayTraceResult hitResult = world.clip(
                    new RayTraceContext(
                            player.getEyePosition(1.0F),             // Start position (player's eyes)
                            player.getEyePosition(1.0F).add(player.getLookAngle().scale(7.0D)), // End position (5 blocks away)
                            RayTraceContext.BlockMode.OUTLINE,       // Only hit blocks with an outline
                            RayTraceContext.FluidMode.SOURCE_ONLY,           // Crucially, hit ANY fluid block
                            player
                    )
            );

            if (state.getBlock() == Blocks.ICE) {

                // Set the block state to Packed Ice
                world.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 11);

                // Play a sound to indicate the hardening/packing
                world.playSound(null, pos, SoundEvents.STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);

                // Damage the tool by a small amount (1 point for block conversion)
                if (player != null) {
                    stack.hurtAndBreak(durabilityCost/2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                }

                return ActionResultType.sidedSuccess(world.isClientSide);
//            } else if (state.getBlock() == Blocks.PACKED_ICE) {
//
//                // Set the block state to Packed Ice
//                world.setBlock(pos, Blocks.BLUE_ICE.defaultBlockState(), 11);
//
//                // Play a sound to indicate the hardening/packing
//                world.playSound(null, pos, SoundEvents.STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 0.8F);
//
//                // Damage the tool by a small amount (1 point for block conversion)
//                if (player != null) {
//                    stack.hurtAndBreak(durabilityCost, player, (p) -> p.broadcastBreakEvent(context.getHand()));
//                }
//
//                return ActionResultType.sidedSuccess(world.isClientSide);
            } else if (hitResult.getType() == RayTraceResult.Type.BLOCK && !getSubmerged(stack)) {
                BlockRayTraceResult blockHit = (BlockRayTraceResult) hitResult;
                BlockPos rayHitPos = blockHit.getBlockPos();
                BlockState rayHitState = world.getBlockState(rayHitPos);

                // 1. Check for Water -> Ice conversion (Priority 1)
                if (rayHitState.getBlock() == Blocks.WATER) {
                    // Set the block state to Ice
                    world.setBlock(rayHitPos, Blocks.ICE.defaultBlockState(), 11);

                    // Play a freezing sound
                    world.playSound(null, rayHitPos, SoundEvents.GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.5F);

                    // Damage the tool by a small amount (1 point for block conversion)
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
